package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateProductTests {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ProductWareHouseRepository productWareHouseRepository;
    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDto productDto;

    @BeforeEach
    public void setUp(){
        productDto = ProductDto.builder()
                .name("Product1")
                .description("Sample Description")
                .price(100.0)
                .image("image_url")
                .productCode("P1001")
                .categoryId("1")
                .supplierId(1L)
                .unitOfMeasureId(1L)
                .warehouseId(1L)
                .build();
    }

    @Test
    public void createProduct_ShouldThrowException_WhenProductAlreadyExists() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.of(new Product()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Error:  Sản phẩm đã tồn tại", exception.getMessage());
    }

    @Test
    public void createProduct_ShouldThrowException_WhenCategoryNotFound() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    public void createProduct_ShouldThrowException_WhenSupplierNotFound() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Supplier not found", exception.getMessage());
    }

    @Test
    public void createProduct_ShouldThrowException_WhenUnitOfMeasureNotFound() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(new Supplier()));
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Unit of Measure not found", exception.getMessage());
    }

    @Test
    public void createProduct_ShouldThrowException_WhenWarehouseNotFound() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(new Supplier()));
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(Optional.of(new UnitOfMeasure()));
        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Warehouse not found", exception.getMessage());
    }


    @Test
    public void createProduct_ShouldCreateProductAndSaveToWarehouse_WhenWarehouseIdIsProvided() {

        // Mock Category
        Category category = new Category();
        category.setId(1L);
        category.setName("Category1");

        // Mock Supplier
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier1");

        // Mock UnitOfMeasure
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setId(1L);
        unitOfMeasure.setUnitName("Kg");

        // Mock Warehouse
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse1");

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(new Supplier()));
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(Optional.of(new UnitOfMeasure()));
        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.of(new Warehouse()));

        Product savedProduct = new Product();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.createProduct(productDto);

        assertNotNull(result);
        assertEquals(savedProduct, result);
        verify(productWareHouseRepository, times(1)).save(any(ProductWarehouse.class));
    }

}