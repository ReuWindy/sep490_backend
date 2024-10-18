package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchDto;
import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.utils.RandomBatchCodeGenerator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final UserService userService;

    public BatchServiceImpl(BatchRepository batchRepository, SupplierRepository supplierRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository, BatchProductRepository batchProductRepository, WarehouseReceiptRepository warehouseReceiptRepository, BatchProductService batchProductService, ProductWareHouseRepository productWareHouseRepository, ProductService productService, UserService userService) {
        this.batchRepository = batchRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.batchProductRepository = batchProductRepository;
        this.warehouseReceiptRepository = warehouseReceiptRepository;
        this.batchProductService = batchProductService;
        this.productWareHouseRepository = productWareHouseRepository;
        this.productService = productService;
        this.userService = userService;
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
        batch.setBatchCode(RandomBatchCodeGenerator.generateBatchCode());
        batch.setImportDate(LocalDateTime.now());

        Supplier supplier = supplierRepository.findById(batchDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Warehouse warehouse = warehouseRepository.findById(batchDto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        batch.setBatchStatus("OK");
        batch.setSupplier(supplier);
        batch.setWarehouse(warehouse);


        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        batch.setBatchCreator(user);
        batchRepository.save(batch);
        return batch;
    }


    @Override
    public Batch updateBatch(Long batchId, BatchDto batchDto) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

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

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        batch.setBatchCreator(user);

        return batchRepository.save(batch);
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
    public void deleteBatch(Long batchId) {
        Batch batch = getBatchById(Math.toIntExact(batchId));
        if(batch.getBatchProducts().isEmpty()){
            batchRepository.delete(batch);
        }
    }

    @Override
    public void deleteBatchWithProduct(Long batchId) {
        Batch batch = getBatchById(Math.toIntExact(batchId));
        if (!batch.getBatchProducts().isEmpty()) {
            batchProductRepository.deleteAll(batch.getBatchProducts());
            batchRepository.delete(batch);
        } else {
            throw new IllegalArgumentException("Batch doesn't have products");
        }
    }
}