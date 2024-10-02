package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchDto;
import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements BatchService {
    private final BatchRepository batchRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final BatchProductRepository batchProductRepository;
    private final WarehouseReceiptRepository warehouseReceiptRepository;
    private final BatchProductService batchProductService;
    private final ProductWareHouseRepository productWareHouseRepository;
    private final ProductService productService;

    public BatchServiceImpl(BatchRepository batchRepository, SupplierRepository supplierRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository, BatchProductRepository batchProductRepository, WarehouseReceiptRepository warehouseReceiptRepository, BatchProductService batchProductService, ProductWareHouseRepository productWareHouseRepository, ProductService productService) {
        this.batchRepository = batchRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.batchProductRepository = batchProductRepository;
        this.warehouseReceiptRepository = warehouseReceiptRepository;
        this.batchProductService = batchProductService;
        this.productWareHouseRepository = productWareHouseRepository;
        this.productService = productService;
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
        batch.setImportDate(batchDto.getImportDate());
        batch.setDamaged(batchDto.isDamaged());

        Supplier supplier = supplierRepository.findById(batchDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        Warehouse warehouse = warehouseRepository.findById(batchDto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        batch.setSupplier(supplier);
        batch.setWarehouse(warehouse);

        int totalBags = 0;
        double totalWeight = 0;
        double totalPrice = 0;

        Set<BatchProduct> batchProducts = new HashSet<>();
        for (BatchProductDto batchProductDto : batchDto.getBatchProducts()) {
            // Try to find the product by ID
            Product product = productRepository.findById(batchProductDto.getProductId())
                    .orElseGet(() -> {
                        // If product doesn't exist, create a new one using createProduct method
                        ProductDto productDto = new ProductDto();
                        productDto.setName("Product " + batchProductDto.getProductId()); // Set a default name
                        productDto.setDescription("Description for product " + batchProductDto.getProductId());
                        productDto.setPrice(batchProductDto.getPrice());
                        productDto.setImage("default_image.jpg"); // Default image
                        productDto.setSupplierId(batchDto.getSupplierId());
                        productDto.setUnitOfMeasureId(1L); // You can set a default unit of measure if needed
                        productDto.setWarehouseId(batchDto.getWarehouseId()); // Optional, if needed

                        // Create the product
                        return productService.createProduct(productDto);
                    });

            // Now check or create the ProductWarehouse
            ProductWarehouse productWarehouse = productWareHouseRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                    .orElseGet(() -> {
                        ProductWarehouse pw = new ProductWarehouse();
                        pw.setProduct(product);
                        pw.setWarehouse(warehouse);
                        pw.setQuantity(0); // Initial quantity is zero
                        return productWareHouseRepository.save(pw);
                    });

            // Create BatchProduct and link to the batch and product
            BatchProduct batchProduct = new BatchProduct();
            batchProduct.setBatch(batch);
            batchProduct.setProduct(product);
            batchProduct.setQuantity(batchProductDto.getQuantity());
            batchProduct.setWeight(batchProductDto.getWeight());
            batchProduct.setPrice(batchProductDto.getPrice());

            batchProducts.add(batchProduct);

            // Update total values for batch
            totalBags += batchProductDto.getQuantity();
            totalWeight += batchProductDto.getWeight();
            totalPrice += batchProductDto.getPrice();

            // Update warehouse stock for product
            productWarehouse.setQuantity(productWarehouse.getQuantity() + batchProductDto.getQuantity());
            productWareHouseRepository.save(productWarehouse);
        }

        // Set calculated totals
        batch.setNumberOfBags(totalBags);
        batch.setTotalWeight(totalWeight);
        batch.setTotalPrice(totalPrice);
        batch.setBatchProducts(batchProducts);

        // Save the batch
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
        return getBatchProduct(batch, batchProductDTO, batchProduct, productRepository, batchProductRepository);
    }

    static BatchProduct getBatchProduct(Batch batch, BatchProductDto batchProductDTO, BatchProduct batchProduct, ProductRepository productRepository, BatchProductRepository batchProductRepository) {
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

    @Override
    public List<Batch> getBatchesByImportDate(Date importDate) {
        return batchRepository.findAllByImportDate(importDate);
    }
}