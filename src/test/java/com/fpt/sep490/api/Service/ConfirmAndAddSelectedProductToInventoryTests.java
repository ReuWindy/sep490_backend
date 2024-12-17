package com.fpt.sep490.api.Service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.InventoryDetailDto;
import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.InventoryRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
import com.fpt.sep490.repository.WarehouseReceiptRepository;
import com.fpt.sep490.service.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfirmAndAddSelectedProductToInventoryTests {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ProductWareHouseRepository productWareHouseRepository;
    @Mock
    private Inventory inventory;
    @Mock
    private InventoryDto inventoryDto;
    @Mock
    private Product product;
    @Mock
    private Warehouse warehouse;
    @Mock
    private ProductWarehouse productWarehouse;
    @Mock
    private BatchRepository batchRepository;
    @Mock
    private WarehouseReceiptRepository warehouseReceiptRepository;
    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    public void setUp() {
        // Tạo dữ liệu mẫu
        product = new Product();
        product.setId(1L);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("warehouse");
        warehouse.setLocation("address");

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setStatus(StatusEnum.PENDING);
        inventory.setInventoryDetails(new HashSet<>());

        InventoryDetail detail1 = new InventoryDetail();
        detail1.setProduct(product);
        detail1.setUnit("box");
        detail1.setWeightPerUnit(10.0);
        detail1.setQuantity(50);
        inventory.getInventoryDetails().add(detail1);

        productWarehouse = new ProductWarehouse();
        productWarehouse.setId(1L);
        productWarehouse.setProduct(product);
        productWarehouse.setWarehouse(warehouse);
        productWarehouse.setUnit("box");
        productWarehouse.setWeightPerUnit(10.0);
        productWarehouse.setQuantity(40);

        inventoryDto = new InventoryDto();
        inventoryDto.setWarehouseId(1L);
        InventoryDetailDto detailDto = new InventoryDetailDto();
        detailDto.setProductId(1L);
        detailDto.setUnit("box");
        detailDto.setWeightPerUnit(10.0);
        detailDto.setQuantity(50);
        detailDto.setSystemQuantity(40);
        detailDto.setDescription("Test Description");
        inventoryDto.setInventoryDetails(Set.of(detailDto));
    }

    @Test
    public void InventoryService_ConfirmAndAddSelectedProductToInventory_UpdateQuantitySuccess() {
        // Mock repository
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                1L, 1L, "box", 10.0))
                .thenReturn(Optional.of(productWarehouse));

        // Thực thi
        String result = inventoryService.confirmAndAddSelectedProductToInventory(1L, inventoryDto);

        // Kiểm tra kết quả
        assertEquals("Xác nhận phiếu kiểm kho và cập nhật số lượng thành công !", result);
        assertEquals(StatusEnum.COMPLETED, inventory.getStatus());
        assertEquals(40, productWarehouse.getQuantity());

    }

    @Test
    public void InventoryService_ConfirmAndAddSelectedProductToInventory_ProductNotFoundInWarehouse() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                anyLong(), anyLong(), anyString(), anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                inventoryService.confirmAndAddSelectedProductToInventory(1L, inventoryDto)
        );

        assertEquals("Lỗi: Không tìm thấy sản phẩm phù hợp trong kho", exception.getMessage());
    }

    @Test
    public void InventoryService_ConfirmAndAddSelectedProductToInventory_InventoryDetailNotFound() {
        inventory.getInventoryDetails().clear(); // Xóa chi tiết để giả lập không tìm thấy
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        Exception exception = assertThrows(RuntimeException.class, () ->
                inventoryService.confirmAndAddSelectedProductToInventory(1L, inventoryDto)
        );

        assertEquals("Lỗi: Không tìm thấy chi tiết kiểm kho phù hợp.", exception.getMessage());
    }

    @Test
    public void InventoryService_ConfirmAndAddSelectedProductToInventory_InventoryNotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                inventoryService.confirmAndAddSelectedProductToInventory(1L, inventoryDto)
        );

        assertEquals("Không tìm thấy phiếu kiểm kho phù hợp!", exception.getMessage());
    }

    @Test
    public void InventoryService_ConfirmAndAddSelectedProductToInventory_QuantityDiscrepancyCalculation() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                1L, 1L, "box", 10.0))
                .thenReturn(Optional.of(productWarehouse));

        inventoryService.confirmAndAddSelectedProductToInventory(1L, inventoryDto);

        InventoryDetail updatedDetail = inventory.getInventoryDetails().iterator().next();
        assertEquals(50, updatedDetail.getQuantity());
        assertEquals(40, updatedDetail.getSystemQuantity());
        assertEquals(10, updatedDetail.getQuantity_discrepancy());
    }

    @Test
    public void InventoryService_ConfirmAndAddSelectedProductToInventory_InventoryStatusUpdated() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                1L, 1L, "box", 10.0))
                .thenReturn(Optional.of(productWarehouse));

        inventoryService.confirmAndAddSelectedProductToInventory(1L, inventoryDto);

        assertEquals(StatusEnum.COMPLETED, inventory.getStatus());
    }
}

