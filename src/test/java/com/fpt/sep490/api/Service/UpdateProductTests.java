package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.dto.importProductDto;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.repository.CategoryRepository;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.repository.SupplierRepository;
import com.fpt.sep490.repository.UnitOfMeasureRepository;
import com.fpt.sep490.service.ProductServiceImpl;
import com.fpt.sep490.utils.RandomProductCodeGenerator;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductTests {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;
    @Mock
    private RandomProductCodeGenerator randomProductCodeGenerator;
    @Mock
    private ProductDto productDto;
    @Mock
    private Product existingProduct;
    @Mock
    private Category category;
    @Mock
    private Supplier supplier;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        // Tạo đối tượng ProductDto để sử dụng trong các test
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Updated Product");
        productDto.setCategoryId(Long.valueOf("2"));
        productDto.setSupplierId(2L);
        productDto.setDescription("Updated product description");
        productDto.setPrice(150.0);
        productDto.setImage("updated_image_url");

        // Tạo đối tượng Product đã tồn tại trong cơ sở dữ liệu
        existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Updated Product");
        existingProduct.setPrice(150.0);
        existingProduct.setDescription("Updated product description");
        existingProduct.setImage("updated_image_url");

        // Tạo đối tượng Category và Supplier
        category = new Category();
        category.setId(2L);
        category.setActive(true);

        supplier = new Supplier();
        supplier.setId(2L);
    }

    @Test
    public void ProductService_UpdateProduct_UpdateProductSuccess() {
        // Giả lập productRepository.existsByNameAndCategoryIdAndSupplierId trả về false (không có sản phẩm trùng tên)
        when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameAndCategoryIdAndSupplierId(
                productDto.getId(), productDto.getName(), Long.valueOf(productDto.getCategoryId()), productDto.getSupplierId()))
                .thenReturn(false);

        // Giả lập productRepository.save trả về đối tượng Product đã cập nhật
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Giả lập các repository khác trả về đối tượng Category và Supplier hợp lệ
        when(categoryRepository.findById(Long.valueOf(productDto.getCategoryId()))).thenReturn(Optional.of(category));
        when(supplierRepository.findById(productDto.getSupplierId())).thenReturn(Optional.of(supplier));

        // Gọi phương thức updateProduct
        Product result = productService.updateProduct(productDto);

        // Assertions: Kiểm tra các trường thông tin trong Product sau khi cập nhật
        assertEquals(productDto.getName(), result.getName());
        assertEquals(productDto.getPrice(), result.getPrice());
        assertEquals(productDto.getDescription(), result.getDescription());
        verify(productRepository).save(existingProduct); // Kiểm tra xem save đã được gọi
    }

    @Test
    public void ProductService_UpdateProduct_ProductNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
        // Giả lập findById trả về Optional.empty() (sản phẩm không tìm thấy)
        when(productRepository.findById(productDto.getId())).thenReturn(Optional.empty());

        // Assertions: Kiểm tra nếu sản phẩm không tồn tại, phương thức phải ném ngoại lệ
        assertThrows(RuntimeException.class, () -> productService.updateProduct(productDto));
    }

    @Test
    public void ProductService_UpdateProduct_CategoryNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
        // Giả lập findById của CategoryRepository trả về Optional.empty() (category không tìm thấy)
        when(categoryRepository.findById(Long.valueOf(productDto.getCategoryId()))).thenReturn(Optional.empty());

        // Assertions: Kiểm tra nếu danh mục không tồn tại, phương thức phải ném ngoại lệ
        assertThrows(RuntimeException.class, () -> productService.updateProduct(productDto));
    }

    @Test
    public void ProductService_UpdateProduct_SupplierNotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));


        // Assertions: Kiểm tra nếu nhà cung cấp không tồn tại, phương thức phải ném ngoại lệ
        assertThrows(RuntimeException.class, () -> productService.updateProduct(productDto));
    }

    @Test
    public void ProductService_UpdateProduct_ProductNameConflict() {
        when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
        // Giả lập productRepository.existsByNameAndCategoryIdAndSupplierId trả về true (trùng tên)
        when(productRepository.existsByNameAndCategoryIdAndSupplierId(
                productDto.getId(), productDto.getName(), Long.valueOf(productDto.getCategoryId()), productDto.getSupplierId()))
                .thenReturn(true);

        // Assertions: Kiểm tra nếu sản phẩm đã tồn tại trùng tên, phương thức phải ném ngoại lệ
        assertThrows(RuntimeException.class, () -> productService.updateProduct(productDto));
    }

}
