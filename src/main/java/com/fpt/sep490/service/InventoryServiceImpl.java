package com.fpt.sep490.service;

import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.model.Inventory;
import com.fpt.sep490.model.InventoryDetail;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.model.Warehouse;
import com.fpt.sep490.repository.InventoryRepository;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    public InventoryServiceImpl(WarehouseRepository warehouseRepository,InventoryRepository inventoryRepository,ProductRepository productRepository){
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }
    @Override
    public Inventory createInventory(InventoryDto inventoryDto) {
        Inventory createdInventory = new Inventory();
        Warehouse warehouse = warehouseRepository.findById(inventoryDto.getWarehouseId()).orElseThrow(()->new RuntimeException("Warehouse Not Found!"));
        createdInventory.setWarehouse(warehouse);
        createdInventory.setInventoryDate(inventoryDto.getInventoryDate());
        Set<InventoryDetail> details = inventoryDto.getInventoryDetails().stream().map(detailDto ->{
            InventoryDetail inventoryDetail = new InventoryDetail();
            Product product = productRepository.findById(detailDto.getProductId()).orElseThrow(()->new RuntimeException("Product Not Found"));
            inventoryDetail.setProduct(product);
            inventoryDetail.setQuantity(detailDto.getQuantity());
            inventoryDetail.setDescription(detailDto.getDescription());
            inventoryDetail.setInventory(createdInventory);
            return inventoryDetail;
        }).collect(Collectors.toSet());
        createdInventory.setInventoryDetails(details);
        inventoryRepository.save(createdInventory);
        return createdInventory;
    }

    @Override
    public Inventory getInventoryById(long inventoryId) {
        return inventoryRepository.findById(inventoryId).orElseThrow(()-> new RuntimeException("Inventory Not Found"));
    }
}
