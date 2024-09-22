package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchDto;
import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements BatchService {
    private final BatchRepository batchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final BatchProductRepository batchProductRepository;

    public BatchServiceImpl(BatchRepository batchRepository, SupplierRepository supplierRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository, BatchProductRepository batchProductRepository) {
        this.batchRepository = batchRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.batchProductRepository = batchProductRepository;
    }

    @Override
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    @Override
    public Batch getBatchById(int id) {
        Optional<Batch> batch = batchRepository.findById((long) id);
        return batch.orElse(null);
    }

    @Override
    public Batch createBatch(BatchDto batchDto) {
        Batch batch = new Batch();
        batch.setBatchCode(batchDto.getBatchCode());
        batch.setNumberOfBags(batchDto.getNumberOfBags());
        batch.setTotalWeight(batchDto.getTotalWeight());
        batch.setTotalPrice(batchDto.getTotalPrice());
        batch.setImportDate(batchDto.getImportDate());
        batch.setDamaged(batchDto.isDamaged());

        Supplier supplier = supplierRepository.findById(batchDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        Warehouse warehouse = warehouseRepository.findById(batchDto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        batch.setSupplier(supplier);
        batch.setWarehouse(warehouse);

        // Save the batch to get the generated ID
        batchRepository.save(batch);

        Set<BatchProduct> batchProducts = batchDto.getBatchProducts().stream()
                .map(batchProductDTO -> {
                    BatchProduct batchProduct = new BatchProduct();
                    return getBatchProduct(batch, batchProductDTO, batchProduct);
                })
                .collect(Collectors.toSet());

        batch.setBatchProducts(batchProducts);
        return batchRepository.save(batch);
    }

    @Override
    public Batch updateBatch(Long batchId, BatchDto batchDto) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        batch.setBatchCode(batchDto.getBatchCode());
        batch.setNumberOfBags(batchDto.getNumberOfBags());
        batch.setTotalWeight(batchDto.getTotalWeight());
        batch.setTotalPrice(batchDto.getTotalPrice());
        batch.setImportDate(batchDto.getImportDate());
        batch.setDamaged(batchDto.isDamaged());

        // Update supplier and warehouse
        if (batch.getSupplier().getId() != batchDto.getSupplierId()) {
            Supplier supplier = supplierRepository.findById(batchDto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            batch.setSupplier(supplier);
        }

        if (batch.getWarehouse().getId() != batchDto.getWarehouseId()) {
            Warehouse warehouse = warehouseRepository.findById(batchDto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found"));
            batch.setWarehouse(warehouse);
        }

        // Update batch products
        Set<BatchProduct> existingBatchProducts = batch.getBatchProducts();
        Set<Long> newBatchProductIds = batchDto.getBatchProducts().stream()
                .map(BatchProductDto::getProductId)
                .collect(Collectors.toSet());

        // Remove BatchProducts that are no longer in the updated batch
        existingBatchProducts.removeIf(bp -> !newBatchProductIds.contains(bp.getProduct().getId()));
        batchProductRepository.deleteAll(existingBatchProducts);

        // Update existing or add new BatchProduct
        Set<BatchProduct> updatedBatchProducts = batchDto.getBatchProducts().stream()
                .map(batchProductDTO -> {
                    BatchProduct batchProduct = existingBatchProducts.stream()
                            .filter(bp -> bp.getProduct().getId() == batchProductDTO.getProductId())
                            .findFirst()
                            .orElse(new BatchProduct());

                    return getBatchProduct(batch, batchProductDTO, batchProduct);
                })
                .collect(Collectors.toSet());

        batch.setBatchProducts(updatedBatchProducts);
        return batchRepository.save(batch);
    }

    private BatchProduct getBatchProduct(Batch batch, BatchProductDto batchProductDTO, BatchProduct batchProduct) {
        Product product = productRepository.findById(batchProductDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        batchProduct.setBatch(batch);
        batchProduct.setProduct(product);
        batchProduct.setQuantity(batchProductDTO.getQuantity());
        batchProduct.setWeight(batchProductDTO.getWeight());
        batchProduct.setPrice(batchProductDTO.getPrice());
        return batchProductRepository.save(batchProduct);
    }

    @Override
    public Batch getBatchByBatchCode(String code) {
        Optional<Batch> batch = Optional.ofNullable(batchRepository.findByBatchCode(code));
        return batch.orElse(null);
    }

    @Override
    public Batch getBatchBySupplierName(String supplierName) {
        Supplier supplier = supplierRepository.findByName(supplierName)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return batchRepository.findFirstBySupplier(supplier)
                .orElseThrow(() -> new RuntimeException("No batch found for this supplier"));
    }

    @Override
    public Batch getBatchByProductCode(String code) {
        Product product = productRepository.findByProductCode(code)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return batchRepository.findFirstByProduct(product)
                .orElseThrow(() -> new RuntimeException("No batch found for this product"));
    }
}