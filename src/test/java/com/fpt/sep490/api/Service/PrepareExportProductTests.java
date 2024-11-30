package com.fpt.sep490.api.Service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.ExportProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.service.ProductServiceImpl;
import com.fpt.sep490.service.WarehouseReceiptService;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrepareExportProductTests {

    @Mock
    private BatchRepository batchRepository;
    @Mock
    private ProductWareHouseRepository productWareHouseRepository;
    @Mock
    private BatchProductRepository batchProductRepository;
    @Mock
    private WarehouseReceiptService warehouseReceiptService;
    @Mock
    private UserService userService;
    @Mock
    private Validator validator;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        // Tạo đối tượng Authentication giả lập
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("password")
                .roles("USER")
                .build();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        // Cung cấp đối tượng Authentication vào SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(context);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void ProductService_PrepareExportProduct_WhenQuantityIsInvalid() {
        // Arrange
        List<ExportProductDto> exportProductDtoList = new ArrayList<>();
        exportProductDtoList.add(new ExportProductDto("Product1", "kg", 10, 0, "1", 1L, 1)); // Số lượng = 0 (không hợp lệ)

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.prepareExportProduct(exportProductDtoList);
        });
        assertEquals("Lỗi: Số lượng không thể <= 0", exception.getMessage());
    }

    @Test
    public void ProductService_PrepareExportProduct_WhenProductNotFoundInWarehouse() {
        // Arrange
        List<ExportProductDto> exportProductDtoList = new ArrayList<>();
        exportProductDtoList.add(new ExportProductDto("Product1", "kg", 10, 100, "1", 1L, 1)); // Số lượng hợp lệ

        Batch batch = new Batch();
        when(batchRepository.save(any(Batch.class))).thenReturn(batch);
        when(productWareHouseRepository.findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(
                anyString(), anyString(), anyDouble(), anyLong()))
                .thenReturn(Optional.empty());  // Không tìm thấy sản phẩm

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.prepareExportProduct(exportProductDtoList);
        });
        assertEquals("Lỗi: Không tìm thấy batchProduct", exception.getMessage());

        verify(batchRepository, times(1)).delete(batch); // Kiểm tra xem batch có bị xóa không
    }

    @Test
    public void ProductService_PrepareExportProduct_WhenBatchIsCreatedSuccessfully() {
        // Arrange
        List<ExportProductDto> exportProductDtoList = new ArrayList<>();
        exportProductDtoList.add(new ExportProductDto("Product1", "kg", 10, 100, "1", 1L, 1)); // Số lượng hợp lệ

        Product product = new Product();
        product.setId(1L);
        product.setName("Product1");
        product.setDescription("A sample product");

        // Tạo Warehouse giả lập
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse1");
        warehouse.setLocation("Location1");

        Batch batch = new Batch();
        when(batchRepository.save(any(Batch.class))).thenReturn(batch);
        when(productWareHouseRepository.findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(
                anyString(), anyString(), anyDouble(), anyLong()))
                .thenReturn(Optional.of(new ProductWarehouse(1l, 100,150.0,200.0,500.0,5.0,"kg",product,warehouse))); // Sản phẩm có trong kho

        // Act
        String result = productService.prepareExportProduct(exportProductDtoList);

        // Assert
        assertEquals("Ok", result);
        verify(batchRepository, times(1)).save(any(Batch.class)); // Kiểm tra xem batch có được lưu không
        verify(batchRepository, times(0)).delete(any(Batch.class)); // Kiểm tra xem batch không bị xóa
    }

    @Test
    public void testPrepareExportProduct_WhenWarehouseReceiptIsCreated() {
        // Arrange
        List<ExportProductDto> exportProductDtoList = new ArrayList<>();
        exportProductDtoList.add(new ExportProductDto("Product1", "kg", 10, 100, "1", 1L, 1)); // Số lượng hợp lệ

        Product product = new Product();
        product.setId(1L);
        product.setName("Product1");
        product.setDescription("A sample product");

        // Tạo Warehouse giả lập
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse1");
        warehouse.setLocation("Location1");

        // Tạo đối tượng Batch giả lập (Bạn cần phải thay thế với các giá trị hợp lý)
        Batch batch = Batch.builder()
                .id(1L)
                .batchCode("Batch001")
                .build();

        // Tạo đối tượng ExpenseVoucher giả lập (Bạn cần phải thay thế với các giá trị hợp lý)
        ExpenseVoucher expenseVoucher = ExpenseVoucher.builder()
                .id(1L)
                .totalAmount(5000.00)
                .build();

        // Tạo đối tượng WarehouseReceipt giả lập với các giá trị cần thiết
        WarehouseReceipt warehouseReceipt = WarehouseReceipt.builder()
                .id(1L)
                .receiptDate(new Date())
                .receiptType(ReceiptType.EXPORT)
                .document("document-path.pdf")
                .batch(batch)
                .expenseVoucher(expenseVoucher)
                .build();

        when(batchRepository.save(any(Batch.class))).thenReturn(batch);
        when(productWareHouseRepository.findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(
                anyString(), anyString(), anyDouble(), anyLong()))
                .thenReturn(Optional.of(new ProductWarehouse(1l, 100,150.0,200.0,500.0,5.0,"kg",product,warehouse)));
        when(warehouseReceiptService.createExportWarehouseReceipt(anyString())).thenReturn(warehouseReceipt); // Chấp nhận bất kỳ đối số nào

        // Act
        String result = productService.prepareExportProduct(exportProductDtoList);

        // Assert
        assertEquals("Ok", result);
        verify(warehouseReceiptService, times(1)).createExportWarehouseReceipt(anyString()); // Kiểm tra tạo receipt
        assertNotNull(batch.getWarehouseReceipt()); // Kiểm tra xem warehouseReceipt có được gán cho batch
    }

    @Test
    public void ProductService_PrepareExportProduct_WhenExportProductDtoListInvalid() {
        // Arrange
        List<ExportProductDto> exportProductDtoList = new ArrayList<>();
        exportProductDtoList.add(new ExportProductDto(null, "kg", 10, 10, "1", 1L, 1));

        // Act & Assert
        Set<ConstraintViolation<ExportProductDto>> violations = validator.validate(exportProductDtoList.get(0));

        assertEquals(1, violations.size());
        assertEquals("Tên không được để trống", violations.iterator().next().getMessage());
    }


}
