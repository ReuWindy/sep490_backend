package com.fpt.sep490.api.Service;


import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.InventoryDetailDto;
import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.service.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateInventoryTests {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductWareHouseRepository productWareHouseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Warehouse warehouse;
    @Mock
    private Product product;
    @Mock
    private ProductWarehouse productWarehouse;
    @Mock
    private User user;
    @Mock
    private InventoryDto inventoryDto;
    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    public void setUp(){
        warehouse = new Warehouse(1L, "Main Warehouse", "123 Street");
        product = new Product(1L, "Sample Product", "Description", 100.0, "image.png", "PROD123", null, null, null, new Date(), new Date(), true, Set.of(), Set.of(), 90.0);
        productWarehouse = new ProductWarehouse(1L, 10, 100.0, 1.0, 50.0,10.0,"Kg",product,warehouse);
        user = new User();
        user.setUsername("username");

        // Tạo DTO Inventory
        InventoryDetailDto inventoryDetailDto = new InventoryDetailDto(1L, 10, "Description", "kg", 10.0,1,0,null);
        inventoryDto = new InventoryDto(1L,"INVENTORY-123",1,new Date(),warehouse,"Pending",true,Set.of(inventoryDetailDto),"username");
    }

    @Test
    public void InventoryService_CreateInventory_CreateInventorySuccess() {

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                1L, 1L, "kg", 10.0)).thenReturn(Optional.of(productWarehouse));
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        // Act
        Inventory inventory = inventoryService.createInventory(inventoryDto,"userTest");

        // Assert
        assertNotNull(inventory);
        assertEquals(StatusEnum.PENDING, inventory.getStatus());
        assertEquals(1, inventory.getInventoryDetails().size());
        InventoryDetail detail = inventory.getInventoryDetails().iterator().next();
        assertEquals(0, detail.getQuantity_discrepancy());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    public void InventoryService_CreateInventory_WarehouseNotFound() {
        // Arrange
        inventoryDto.setWarehouseId(999L);
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.createInventory(inventoryDto,"userTest");
        });
        assertEquals("Không tìm thấy kho", exception.getMessage());
    }

    @Test
    public void InventoryService_CreateInventory_UserNotFound(){
        // Arrange
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.createInventory(inventoryDto,"userTest");
        });

        assertEquals("Không tìm thấy người dùng",exception.getMessage());
    }

    @Test
    public void InventoryService_CreateInventory_ProductWarehouseNotFound(){

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                1L, 1L, "kg", 10.0)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.createInventory(inventoryDto,"userTest");
        });

        assertEquals("Lỗi: Không tìm thấy sản phẩm phù hợp trong kho",exception.getMessage());
    }

    @Test
    public void InventoryService_CreateInventory_QuantityDiscrepancyCalculationSuccess() {
        productWarehouse.setQuantity(50);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                1L, 1L, "kg", 10.0)).thenReturn(Optional.of(productWarehouse));
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        Inventory createdInventory = inventoryService.createInventory(inventoryDto,"userTest");

        InventoryDetail detail = createdInventory.getInventoryDetails().iterator().next();
        assertEquals(inventoryDto.getInventoryDetails().iterator().next().getQuantity() - 50, detail.getQuantity_discrepancy());
    }

    @Test
    public void InventoryService_CreateInventory_GenerateInventoryCodeSuccess() {

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                1L, 1L, "kg", 10.0)).thenReturn(Optional.of(productWarehouse));
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        Inventory createdInventory = inventoryService.createInventory(inventoryDto,"userTest");

        assertNotNull(createdInventory.getInventoryCode());
        assertTrue(createdInventory.getInventoryCode().startsWith("INVENTORY-"));
    }
}
