package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.BatchProductSelection;
import com.fpt.sep490.dto.ExportProductDto;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfirmAndExportProductsTests {
    @Mock
    private BatchRepository batchRepository;
    @Mock
    private ProductWareHouseRepository productWareHouseRepository;
    @Mock
    private BatchProductRepository batchProductRepository;
    @Mock
    private Batch batch;
    @Mock
    private Product product;
    @Mock
    private BatchProduct batchProduct;
    @Mock
    private ProductWarehouse productWarehouse;
    @Mock
    private List<ExportProductDto> exportProductDtos;
    @Mock
    private Validator validator;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp(){
        batch = new Batch();
        batch.setId(1L);
        batch.setBatchCode("BATCH001");
        // Data test cho Product
        product = new Product(
                1L, "Product1", "Description", 120.0, "image.png", "CODE123",
                null, null, null, new Date(), new Date(), false, Set.of(), Set.of(), 100.0
        );

        // Data test cho BatchProduct
        batchProduct = new BatchProduct();
        batchProduct.setProduct(product);
        batchProduct.setUnit("kg");
        batchProduct.setWeightPerUnit(100);
        batchProduct.setAdded(false);

        // Thêm BatchProduct vào Batch
        batch.setBatchProducts(new HashSet<>(Set.of(batchProduct)));

        // Data test cho ProductWarehouse
        productWarehouse = new ProductWarehouse();
        productWarehouse.setProduct(product);
        productWarehouse.setUnit("kg");
        productWarehouse.setWeightPerUnit(100.0);
        productWarehouse.setQuantity(15);

        // Data test cho ExportProductDto
        exportProductDtos = new ArrayList<>();
        exportProductDtos.add(new ExportProductDto("Product1", "kg", 10, 10, "1", 1L, 1));

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_BatchNotFound() {
        // Arrange
        Long batchId = 1L;

        // Mock Repository
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.confirmAndExportProducts(batchId, exportProductDtos);
        });
        assertEquals("Lỗi: Không tìm thấy lô hàng", exception.getMessage());
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ProductQuantityNotEnough() {
        // Arrange
        Long batchId = 1L;
        // Số lượng sản phẩm trong kho ít hơn yêu cầu
        productWarehouse.setQuantity(5);

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(productWareHouseRepository.findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(
                anyString(), anyString(), anyDouble(), anyLong()))
                .thenReturn(Optional.of(productWarehouse));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.confirmAndExportProducts(batchId, exportProductDtos);
        });
        assertEquals("Lỗi: Số lượng sản phẩm Product1 trong kho không đủ.", exception.getMessage());
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ProductNotFound() {
        // Arrange
        Long batchId = 1L;

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(productWareHouseRepository.findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(
                "Product1", "kg", 10.0, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.confirmAndExportProducts(batchId, exportProductDtos);
        });
        assertEquals("Lỗi: Không tìm thấy sản phẩm " + exportProductDtos.get(0).getProductName() + " trong kho", exception.getMessage());
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ReturnBatchCode() {
        // Arrange
        Long batchId = 1L;

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(productWareHouseRepository.findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(
                "Product1", "kg", 10.0, 1L)).thenReturn(Optional.of(productWarehouse));

        // Act
        String result = productService.confirmAndExportProducts(batchId, exportProductDtos);

        // Assert
        assertEquals("BATCH001", result);
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ExportProductDtosProductNameNull(){
        Long batchId = 1L;
        exportProductDtos.get(0).setProductName(null);

        Set<ConstraintViolation<ExportProductDto>> violations = validator.validate(exportProductDtos.get(0));

        assertEquals(1, violations.size());
        assertEquals("Tên sản phẩm không được để trống", violations.iterator().next().getMessage());
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ExportProductDtosProductNameContainSpecialCharacter(){
        Long batchId = 1L;
        exportProductDtos.get(0).setProductName("!@#$%^");

        Set<ConstraintViolation<ExportProductDto>> violations = validator.validate(exportProductDtos.get(0));

        assertEquals(1, violations.size());
        assertEquals("Tên sản phẩm chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.", violations.iterator().next().getMessage());
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ExportProductDtosUnitNull(){
        Long batchId = 1L;
        exportProductDtos.get(0).setUnit(null);

        Set<ConstraintViolation<ExportProductDto>> violations = validator.validate(exportProductDtos.get(0));

        assertEquals(1, violations.size());
        assertEquals("Quy cách đóng gói  không được để trống", violations.iterator().next().getMessage());
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ExportProductDtosWeightPerUnitNegative(){
        Long batchId = 1L;
        exportProductDtos.get(0).setWeightPerUnit(-1);

        Set<ConstraintViolation<ExportProductDto>> violations = validator.validate(exportProductDtos.get(0));

        assertEquals(1, violations.size());
        assertEquals("Trọng lượng mỗi đơn vị phải lớn hơn hoặc bằng 0", violations.iterator().next().getMessage());
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ExportProductDtosCategoryIdNull(){
        Long batchId = 1L;
        exportProductDtos.get(0).setCategoryId(null);

        Set<ConstraintViolation<ExportProductDto>> violations = validator.validate(exportProductDtos.get(0));

        assertEquals(1, violations.size());
        assertEquals("Id loại sản phẩm không được để trống", violations.iterator().next().getMessage());
    }

    @Test
    public void ProductService_ConfirmAndExportProducts_ExportProductDtosWarehouseIdNegative(){
        Long batchId = 1L;
        exportProductDtos.get(0).setWarehouseId(-1);

        Set<ConstraintViolation<ExportProductDto>> violations = validator.validate(exportProductDtos.get(0));

        assertEquals(1, violations.size());
        assertEquals("Id kho hàng phải lớn hơn hoặc bằng 0", violations.iterator().next().getMessage());
    }

}
