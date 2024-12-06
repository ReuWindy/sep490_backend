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
                .categoryId(Long.valueOf("1"))
                .supplierId(1L)
                .unitOfMeasureId(1L)
                .warehouseId(1L)
                .build();
    }

    @Test
    public void ProductService_CreateProduct_ProductAlreadyExists() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.of(new Product()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Error:  Sản phẩm đã tồn tại", exception.getMessage());
    }

    @Test
    public void ProductService_CreateProduct_CategoryNotFound() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Không tìm thấy danh mục", exception.getMessage());
    }

    @Test
    public void ProductService_CreateProduct_SupplierNotFound() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Không tìm thấy nhà cung cấp", exception.getMessage());
    }

    @Test
    public void ProductService_CreateProduct_UnitOfMeasureNotFound() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(new Supplier()));
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Không tìm thấy đơn vị", exception.getMessage());
    }

    @Test
    public void ProductService_CreateProduct_WarehouseNotFound() {

        when(productRepository.findByNameAndCategoryIdAndSupplierId(anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(new Supplier()));
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(Optional.of(new UnitOfMeasure()));
        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDto);
        });

        assertEquals("Không tìm thấy kho", exception.getMessage());
    }


    @Test
    public void ProductService_CreateProduct_CreateProductSuccess() {

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
