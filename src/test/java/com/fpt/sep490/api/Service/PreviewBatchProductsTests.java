package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.importProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.service.ProductServiceImpl;
import com.fpt.sep490.service.WarehouseReceiptService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PreviewBatchProductsTests {
    @Mock
    private BatchRepository batchRepository;
    @Mock
    private BatchProductRepository batchProductRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;
    @Mock
    private WarehouseReceiptService warehouseReceiptService;
    @Mock
    private UserService userService;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        lenient().when(mockUserDetails.getUsername()).thenReturn("test_user");

        Authentication mockAuthentication = mock(Authentication.class);
        lenient().when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        lenient().when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);

        SecurityContextHolder.setContext(mockSecurityContext);

        com.fpt.sep490.model.User mockUser = new User();
        mockUser.setUsername("testUser");
        lenient().when(userService.findByUsername(anyString())).thenReturn(mockUser);
    }

    @Test
    public void ProductService_PreviewBatchProducts_WhenListIsNotEmpty() {
        // Arrange
        List<importProductDto> importProductDtoList = new ArrayList<>();
        importProductDtoList.add(new importProductDto("Product1","1", 10, "image", 5, 10.0, "Kg", "1",1L,1L,1L));

        // Mock các đối tượng cần thiết
        Supplier mockSupplier = new Supplier();
        mockSupplier.setId(1L);
        mockSupplier.setName("Supplier1");

        Category mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Supplier1");

        UnitOfMeasure mockMeasure = new UnitOfMeasure(); // Tạo một đối tượng nhà cung cấp giả
        mockMeasure.setId(1L);
        mockMeasure.setUnitName("Supplier1");
        mockMeasure.setConversionFactor(100);

        // Mock phương thức tìm nhà cung cấp
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(mockSupplier));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(Optional.of(mockMeasure));

        // Mock các phương thức phụ thuộc
        Batch mockBatch = new Batch();
        when(batchRepository.save(any(Batch.class))).thenReturn(mockBatch);
        when(batchProductRepository.save(any(BatchProduct.class))).thenReturn(new BatchProduct());

        // Act
        List<BatchProduct> batchProducts = productService.previewBatchProducts(importProductDtoList);

        // Assert
        assertNotNull(batchProducts);
        assertEquals(1, batchProducts.size());  // Kiểm tra xem có đúng số lượng BatchProduct được tạo không
        verify(batchProductRepository, times(1)).save(any(BatchProduct.class));  // Kiểm tra xem batchProductRepository.save được gọi đúng số lần
    }

    @Test
    public void ProductService_PreviewBatchProducts_WhenListIsEmpty() {
        // Arrange
        List<importProductDto> importProductDtoList = new ArrayList<>();

        // Mock phương thức phụ thuộc
        when(batchRepository.save(any(Batch.class))).thenReturn(new Batch());

        // Act
        List<BatchProduct> batchProducts = productService.previewBatchProducts(importProductDtoList);

        // Assert
        assertNotNull(batchProducts);
        assertEquals(0, batchProducts.size());  // Kiểm tra xem phương thức trả về danh sách rỗng khi không có sản phẩm
        verify(batchProductRepository, never()).save(any(BatchProduct.class));  // Kiểm tra không gọi save khi không có sản phẩm
    }

    @Test
    public void ProductService_PreviewBatchProducts_WhenSupplierDoesNotExist() {
        // Arrange
        List<importProductDto> importProductDtoList = new ArrayList<>();
        importProductDtoList.add(new importProductDto("Product1", "1", 10, "image", 5, 10.0, "Kg", "1", 1L, 1L, 1L));

        // Mock các đối tượng cần thiết
        Category mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Category1");

        UnitOfMeasure mockMeasure = new UnitOfMeasure();
        mockMeasure.setId(1L);
        mockMeasure.setUnitName("Kg");
        mockMeasure.setConversionFactor(100);

        // Mock phương thức tìm kiếm nhà cung cấp, trả về Optional.empty() để mô phỏng trường hợp không tồn tại
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.previewBatchProducts(importProductDtoList);
        });

        // Verify không gọi save bất kỳ BatchProduct nào khi xảy ra lỗi
        verify(batchProductRepository, never()).save(any(BatchProduct.class));
    }

    @Test
    public void ProductService_PreviewBatchProducts_WhenCategoryDoesNotExist() {
        // Arrange
        List<importProductDto> importProductDtoList = new ArrayList<>();
        importProductDtoList.add(new importProductDto("Product2", "2", 20, "image2", 10, 20.0, "Kg", "2", 1L, 2L, 1L));

        // Mock các đối tượng cần thiết
        Supplier mockSupplier = new Supplier();
        mockSupplier.setId(1L);
        mockSupplier.setName("Supplier1");

        UnitOfMeasure mockMeasure = new UnitOfMeasure();
        mockMeasure.setId(1L);
        mockMeasure.setUnitName("Kg");
        mockMeasure.setConversionFactor(100);

        // Mock phương thức tìm kiếm Category trả về Optional.empty() để mô phỏng trường hợp không tồn tại
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(mockSupplier));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.previewBatchProducts(importProductDtoList);
        });

        // Verify không gọi save bất kỳ BatchProduct nào khi xảy ra lỗi
        verify(batchProductRepository, never()).save(any(BatchProduct.class));
    }

    @Test
    public void ProductService_PreviewBatchProducts_WhenUnitOfMeasureDoesNotExist() {
        // Arrange
        List<importProductDto> importProductDtoList = new ArrayList<>();
        importProductDtoList.add(new importProductDto("Product3", "3", 30, "image3", 15, 30.0, "Kg", "3", 1L, 1L, 2L));

        // Mock các đối tượng cần thiết
        Supplier mockSupplier = new Supplier();
        mockSupplier.setId(1L);
        mockSupplier.setName("Supplier1");

        Category mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Category1");

        // Mock phương thức tìm kiếm UnitOfMeasure trả về Optional.empty() để mô phỏng trường hợp không tồn tại
        when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(mockSupplier));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.previewBatchProducts(importProductDtoList);
        });

        // Verify không gọi save bất kỳ BatchProduct nào khi xảy ra lỗi
        verify(batchProductRepository, never()).save(any(BatchProduct.class));
    }

}
