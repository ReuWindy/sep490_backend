package com.fpt.sep490.controller;

import com.fpt.sep490.dto.*;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.ProductService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {
    private final ProductService productService;

    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final SimpMessagingTemplate messagingTemplate;

    public ProductController(ProductService productService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, SimpMessagingTemplate messagingTemplate) {
        this.productService = productService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/batchCode/{batchCode}")
    public ResponseEntity<?> getAllProducts(@PathVariable String batchCode) {
        try {
            List<Product> products = productService.getAllBatchProducts(batchCode);
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable long id) {
        Product product = productService.getProductById((int) id);
        if (product != null) {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto productDto) {
        Product product = productService.createProduct(productDto);
        if (product != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/import/preview")
    public ResponseEntity<?> importProduct(HttpServletRequest request, @Valid @RequestBody List<importProductDto> importProductDtoList) {
        try {
            List<BatchProduct> importList = productService.previewBatchProducts(importProductDtoList);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "IMPORT_PRODUCT", "Tạo lô hàng nhập kho bởi :" + username);
            messagingTemplate.convertAndSend("/topic/products", "Bản xem trước cho 1 lô hàng nhập kho mới vừa được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(importList);
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/import/excel")
    public ResponseEntity<?> importProductFromExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            List<importProductDto> importProductDtoList = productService.readExcelFile(file);
            List<BatchProduct> importList = productService.previewBatchProducts(importProductDtoList);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "IMPORT_PRODUCT_EXCEL", "Nhập lô hàng từ file Excel bởi: " + username);
            messagingTemplate.convertAndSend("/topic/products", "Lô hàng nhập kho từ file Excel vừa được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(importList);
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Lỗi khi đọc file Excel.", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<?> exportProductFromExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            List<ExportProductDto> exportProductDtoList = productService.readExcelFileExport(file);
            List<BatchProduct> exportList = productService.prepareExportProduct(exportProductDtoList);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "EXPORT_PRODUCT_EXCEL", "Xuất lô hàng từ file Excel bởi: " + username);
            messagingTemplate.convertAndSend("/topic/products", "Lô hàng nhập kho từ file Excel vừa được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(exportList);
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Lỗi khi đọc file Excel.", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/import/previewFromProduction")
    public ResponseEntity<?> importProductFromProduction(HttpServletRequest request, @Valid @RequestBody List<importProductFromProductionDto> importProductDtoList) {
        try {
            List<BatchProduct> importList = productService.previewBatchProductsFromProduction(importProductDtoList);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "IMPORT_PRODUCT_PRODUCTION", "Tạo lô hàng nhập kho bởi :" + username);
            messagingTemplate.convertAndSend("/topic/products", "Bản xem trước cho 1 lô hàng nhập kho mới vừa được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(importList);
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/confirm-add-to-warehouse/{batchId}")
    public ResponseEntity<?> confirmAndAddToWarehouse(HttpServletRequest request,
                                                      @PathVariable Long batchId,
                                                      @RequestBody List<BatchProductSelection> selectedProducts) {
        try {
            String message = productService.confirmAndAddSelectedProductToWarehouse(batchId, selectedProducts);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CONFIRM_ADD_TO_WAREHOUSE", "Xác nhận thêm lô hàng vào kho bởi :" + username);
            messagingTemplate.convertAndSend("/topic/products", "Lô hàng " + message + " đã được xác nhận thêm vào kho bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/export/preview")
    public ResponseEntity<?> prepareExportProduct(HttpServletRequest request, @Valid @RequestBody List<ExportProductDto> exportProductDtoList) {
        try {
            List<BatchProduct> exportList = productService.prepareExportProduct(exportProductDtoList);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "PREPARE_EXPORT_PRODUCT", "Tạo lô hàng xuất kho bởi " + username);
            messagingTemplate.convertAndSend("/topic/products", "Bản xem trước cho 1 lô hàng xuất kho mới vừa được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(exportList);
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/export/confirm/{batchId}")
    public ResponseEntity<?> confirmAndExportProducts(HttpServletRequest request, @PathVariable Long batchId, @Valid @RequestBody List<ExportProductDto> exportProductDtoList) {
        try {
            String message = productService.confirmAndExportProducts(batchId, exportProductDtoList);

            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CONFIRM_EXPORT_PRODUCT", "Xác nhận xuất kho lô hàng bởi: " + username);
            messagingTemplate.convertAndSend("/topic/products", "Lô hàng " + message + " đã được xác nhận xuất kho kho bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<Product>> getProductsByWarehouse(@PathVariable Long warehouseId) {
        List<Product> products = productService.getProductsByWarehouse(warehouseId);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(products);
        }
        return ResponseEntity.ok(products);
    }

//    @PutMapping("/update/{productCode}")
//    public ResponseEntity<?> updateProductStatus(@PathVariable String productCode) {
//        try{
//            Optional<Product> product= productRepository.findByProductCode(productCode);
//            if (product.isPresent()) {
//                productService.updateProductStatus(productCode);
//                return ResponseEntity.status(HttpStatus.OK).body(product.get().getIsDeleted());
//            }
//            re
//        }catch (Exception e){
//            final ApiExceptionResponse response = new ApiExceptionResponse("Update Status Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }

    @GetMapping("/admin/products")
    public ResponseEntity<PagedModel<EntityModel<AdminProductDto>>> adminProductPage(
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String batchCode,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date importDate,
            @RequestParam(required = false) String productQuantity,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "price") String priceOrder,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<AdminProductDto> pagedResourcesAssembler) {
        Page<AdminProductDto> productPage = productService.getProductByFilterForAdmin(productCode, productName, batchCode, warehouseId, importDate, productQuantity, sortDirection, priceOrder, pageNumber, pageSize);
        PagedModel<EntityModel<AdminProductDto>> pagedModel = pagedResourcesAssembler.toModel(productPage);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/customer/products")
    public ResponseEntity<PagedModel<EntityModel<ProductDto>>> customerProductPage(
            HttpServletRequest request,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String supplierName,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<ProductDto> pagedResourcesAssembler) {

        String token = jwtTokenManager.resolveTokenFromCookie(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        Page<ProductDto> productPage = productService.getProductByFilterForCustomer(name, productCode, categoryName, supplierName, username, pageNumber, pageSize);
        PagedModel<EntityModel<ProductDto>> pagedModel = pagedResourcesAssembler.toModel(productPage);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/admin/order/products")
    public ResponseEntity<PagedModel<EntityModel<ProductDto>>> customerProductPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) Long id,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<ProductDto> pagedResourcesAssembler) {

        Page<ProductDto> productPage = productService.getProductByFilterForCustomer(name, productCode, categoryName, supplierName, id, pageNumber, pageSize);
        PagedModel<EntityModel<ProductDto>> pagedModel = pagedResourcesAssembler.toModel(productPage);
        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/admin/createProduct")
    public ResponseEntity<?> createCustomerProduct(HttpServletRequest request, @RequestBody ProductDto productDto) {
        try {
            Product createdProduct = productService.createCustomerProduct(productDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_PRODUCT_ADMIN", "Tạo sản phẩm " + createdProduct.getName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/products", "Sản phẩm " + createdProduct.getName() + " đã được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/admin/updateProduct")
    public ResponseEntity<?> updateCustomerProduct(HttpServletRequest request, @Valid @RequestBody ProductDto productDto) {
        try {
            Product updatedProduct = productService.updateProduct(productDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_PRODUCT_CUSTOMER", "Cập nhật sản phẩm " + updatedProduct.getName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/products", "Sản phẩm " + updatedProduct.getName() + " đã được Cập nhậ bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> disableProduct(HttpServletRequest request, @PathVariable long id) {
        try {
            Product product = productService.disableProduct(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DISABLE_PRODUCT", "Ẩn sản phẩm " + product.getName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/products", "Sản phẩm " + product.getName() + " đã được ẩn bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/enable/{id}")
    public ResponseEntity<?> enableProduct(HttpServletRequest request, @PathVariable long id) {
        try {
            Product product = productService.enableProduct(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "ENABLE_PRODUCT", "Kích hoạt sản phẩm " + product.getName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/products", "Sản phẩm " + product.getName() + " đã được kích hoạt bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/generateTemplate")
    public ResponseEntity<?> generateExcelTemplate(HttpServletResponse response) throws IOException {
        try{
            productService.createExcelTemplate(response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            final ApiExceptionResponse res = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @GetMapping("/generateExportTemplate")
    public ResponseEntity<?> generateExcelTemplateExport(HttpServletResponse response) throws IOException {
        try{
            productService.createExcelTemplateExport(response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            final ApiExceptionResponse res = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }
}