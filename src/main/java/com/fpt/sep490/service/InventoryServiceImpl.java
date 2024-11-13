package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.BatchProductSelection;
import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.InventoryRepository;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
import com.fpt.sep490.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final ProductWareHouseRepository productWareHouseRepository;

    public InventoryServiceImpl(WarehouseRepository warehouseRepository,InventoryRepository inventoryRepository,ProductRepository productRepository,ProductWareHouseRepository productWareHouseRepository){
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.productWareHouseRepository = productWareHouseRepository;
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
            inventoryDetail.setUnit(detailDto.getUnit());
            inventoryDetail.setWeightPerUnit(detailDto.getWeightPerUnit());

            ProductWarehouse productWarehouse = productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                            detailDto.getProductId(), inventoryDto.getWarehouseId(),detailDto.getUnit(), detailDto.getWeightPerUnit())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm phù hợp trong kho"));
            inventoryDetail.setSystemQuantity(productWarehouse.getQuantity());
            inventoryDetail.setQuantity_discrepancy(detailDto.getQuantity() - productWarehouse.getQuantity());

            return inventoryDetail;
        }).collect(Collectors.toSet());

        createdInventory.setInventoryDetails(details);
        createdInventory.setStatus(StatusEnum.PENDING);

        inventoryRepository.save(createdInventory);

        return createdInventory;
    }

    @Override
    public Inventory getInventoryById(long inventoryId) {
        return inventoryRepository.findById(inventoryId).orElseThrow(()-> new RuntimeException("Inventory Not Found"));
    }

    @Override
    public String confirmAndAddSelectedProductToInventory(Long inventoryId, InventoryDto inventoryDto) {
        Warehouse warehouse = warehouseRepository.findById(inventoryDto.getWarehouseId()).orElseThrow(()-> new EntityNotFoundException("Warehouse Not Found"));
        Inventory inventory = inventoryRepository.findById(inventoryId).orElse(new Inventory());

        for(var detail: inventoryDto.getInventoryDetails()) {

            ProductWarehouse productWarehouse = productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                            detail.getProductId(), inventoryDto.getWarehouseId(),detail.getUnit(), detail.getWeightPerUnit())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm phù hợp trong kho"));

            InventoryDetail inventoryDetail = inventory.getInventoryDetails().stream()
                    .filter(d -> d.getProduct().getId() == detail.getProductId()
                            && d.getUnit().equals(detail.getUnit())
                            && d.getWeightPerUnit() == detail.getWeightPerUnit())
                    .findFirst()
                    .orElse(null);  // Không tạo mới, nếu không tìm thấy thì bỏ qua

            // Nếu tìm thấy InventoryDetail, cập nhật nó
            if (inventoryDetail != null) {
                int discrepancy = detail.getQuantity() - productWarehouse.getQuantity();
                inventoryDetail.setQuantity(detail.getQuantity());
                inventoryDetail.setDescription(detail.getDescription());
                inventoryDetail.setSystemQuantity(productWarehouse.getQuantity());
                inventoryDetail.setQuantity_discrepancy(discrepancy);
            } else {
                // Nếu không tìm thấy InventoryDetail tương ứng, có thể bỏ qua hoặc báo lỗi
                throw new RuntimeException("Lỗi: Không tìm thấy chi tiết kiểm kho phù hợp.");
            }

        }
        inventory.setStatus(StatusEnum.COMPLETED);
        inventoryRepository.save(inventory);

        for(var detail : inventory.getInventoryDetails()){
            ProductWarehouse productWarehouse = productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                            detail.getProduct().getId(), inventoryDto.getWarehouseId(),detail.getUnit(), detail.getWeightPerUnit())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm phù hợp trong kho"));
            productWarehouse.setQuantity(detail.getQuantity());
            productWareHouseRepository.save(productWarehouse);
        }

        return "Done!";
    }

}
