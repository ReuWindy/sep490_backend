package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.BatchProductSelection;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.service.ProductServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfirmAndAddSelectedProductToWarehouseTests {
    @Mock
    private BatchRepository batchRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ProductWareHouseRepository productWareHouseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BatchProductRepository batchProductRepository;
    @Mock
    private Batch batch;
    @Mock
    private Product product;
    @Mock
    private BatchProduct batchProduct;
    @Mock
    private Warehouse warehouse;
    @Mock
    private BatchProductSelection selection;
    @Mock
    private List<BatchProductSelection> batchProductSelections;
    @Mock
    private Validator validator;
    @InjectMocks
    private ProductServiceImpl productService;


    @BeforeEach
    public void setUp() {
        // Mock batch data
        batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH001");
        batch.setBatchStatus("Chưa xác nhận");

        // Mock product
        product = new Product();
        product.setId(101L);
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        product.setSupplier(supplier);

        // Mock batch product
        batchProduct = new BatchProduct();
        batchProduct.setProduct(product);
        batchProduct.setUnit("kg");
        batchProduct.setWeightPerUnit(1.0);
        batchProduct.setQuantity(10);
        batchProduct.setPrice(100.0);
        batchProduct.setWarehouseId(1L);

        // Attach batch product to batch
        batch.setBatchProducts(Set.of(batchProduct));

        // Mock warehouse
        warehouse = new Warehouse();
        warehouse.setId(1L);

        // Mock batch product selection
        selection = new BatchProductSelection();
        selection.setProductId(101L);
        selection.setUnit("kg");
        selection.setWeighPerUnit(1.0);
        selection.setSupplierId(1);

        batchProductSelections = List.of(selection);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void ProductService_ConfirmAndAddSelectedProductToWarehouse_SuccessfulAddition() {
        long batchId = 1L;


        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(new Warehouse()));

        // Execute
        String result = productService.confirmAndAddSelectedProductToWarehouse(batchId, batchProductSelections);

        // Verify
        assertEquals("BATCH001", result);
        assertTrue(batchProduct.isAdded());
        assertEquals("Đã thêm vào kho", batchProduct.getDescription());
        verify(productWareHouseRepository, times(1)).save(any(ProductWarehouse.class));
        assertEquals("Đã xác nhận", batch.getBatchStatus());
    }

    @Test
    public void ProductService_ConfirmAndAddSelectedProductToWarehouse_ProductNotInSelection() {
        long batchId = 1L;

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

        // Mock an empty selection (no matching product)
        List<BatchProductSelection> batchProductSelections = List.of();

        // Execute
        String result = productService.confirmAndAddSelectedProductToWarehouse(batchId, batchProductSelections);

        // Verify
        assertEquals("BATCH001", result);
        assertFalse(batchProduct.isAdded());
        assertEquals(null, batchProduct.getDescription());
        verify(productWareHouseRepository, never()).save(any(ProductWarehouse.class));
        assertEquals("Đã xác nhận", batch.getBatchStatus());
    }

    @Test
    public void ProductService_ConfirmAndAddSelectedProductToWarehouse_BatchIdNotFound(){
        long batchId = 999L;

        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            productService.confirmAndAddSelectedProductToWarehouse(batchId, batchProductSelections);
        });

        assertEquals("Lỗi: Không tìm thấy lô hàng với id:"+ batchId, exception.getMessage());
    }

    @Test
    public void ProductService_ConfirmAndSelectedProductToWarehouse_WarehouseIdNotFound(){
        long batchId = 1L;
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            productService.confirmAndAddSelectedProductToWarehouse(batchId, batchProductSelections);
        });

        assertEquals("Lỗi: Không tìm thấy kho hàng với Id: "+1L, exception.getMessage());
    }

    @Test
    public void ProductService_ConfirmAndSelectedProductToWarehouse_BatchProductSelectionsUnitInvalid(){
        long batchId = 1L;
        selection.setUnit(null);

        Set<ConstraintViolation<BatchProductSelection>> violations = validator.validate(batchProductSelections.get(0));

        assertEquals(1, violations.size());
        assertEquals("Loại đóng gói không được để trống.", violations.iterator().next().getMessage());
    }

    @Test
    public void ProductService_ConfirmAndSelectedProductToWarehouse_BatchProductSelectionsWeightPerUnitInvalid(){
        long batchId = 1L;
        selection.setWeighPerUnit(-1);

        Set<ConstraintViolation<BatchProductSelection>> violations = validator.validate(batchProductSelections.get(0));

        assertEquals(1, violations.size());
        assertEquals("Khối lượng đóng gói của đơn vị phải là số dương.", violations.iterator().next().getMessage());
    }

    @Test
    public void ProductService_ConfirmAndSelectedProductToWarehouse_BatchProductSelectionsSupplierIdInvalid(){
        long batchId = 1L;
        selection.setSupplierId(-1);

        Set<ConstraintViolation<BatchProductSelection>> violations = validator.validate(batchProductSelections.get(0));

        assertEquals(1, violations.size());
        assertEquals("Id nhà cung cấp phải là số dương.", violations.iterator().next().getMessage());
    }

}
