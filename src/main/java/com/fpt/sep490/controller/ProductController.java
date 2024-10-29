package com.fpt.sep490.controller;

import com.fpt.sep490.dto.AdminProductDto;
import com.fpt.sep490.dto.ExportProductDto;
import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.dto.importProductDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.ProductService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;

    public ProductController(ProductService productService, ProductRepository productRepository, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllProducts() {
        try{
            List<Product> products = productService.getAllProducts();
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
    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto) {
        Product product = productService.createProduct(productDto);
        if(product != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //@ExceptionHandler(value = MethodArgumentNotValidException.class)
    @PostMapping("/import")
    public ResponseEntity<?> importProduct(HttpServletRequest request,@RequestBody List<importProductDto> importProductDtoList) {
        try {
            String message = productService.importProductToBatch(importProductDtoList);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "IMPORT_PRODUCT", "Import Product to warehouse by :"+ username);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/export")
    public ResponseEntity<?> exportProduct(HttpServletRequest request,@Valid @RequestBody List<ExportProductDto> exportProductDtoList) {
        try {
            String message = productService.exportProduct(exportProductDtoList);
                String token = jwtTokenManager.resolveToken(request);
                String username = jwtTokenManager.getUsernameFromToken(token);
                userActivityService.logAndNotifyAdmin(username, "EXPORT_PRODUCT", "Export Product by :" + username);
                return ResponseEntity.status(HttpStatus.CREATED).body(message);
        }catch (RuntimeException e) {
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

    @PutMapping("/update/{productCode}")
    public ResponseEntity<?> updateProductStatus(@PathVariable String productCode) {
        Optional<Product> product= productRepository.findByProductCode(productCode);
        if (product.isPresent()) {
            productService.updateProductStatus(productCode);
            return ResponseEntity.status(HttpStatus.OK).body(product.get().getIsDeleted());
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Update Status Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/admin/products")
    public ResponseEntity<PagedModel<EntityModel<AdminProductDto>>> adminProductPage(
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String batchCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date importDate,
            @RequestParam(required = false) String productQuantity,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "price") String priceOrder,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<AdminProductDto> pagedResourcesAssembler) {
        Page<AdminProductDto> productPage = productService.getProductByFilterForAdmin(productCode, productName, batchCode, importDate, productQuantity, sortDirection, priceOrder, pageNumber, pageSize);
        PagedModel<EntityModel<AdminProductDto>> pagedModel = pagedResourcesAssembler.toModel(productPage);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/customer/products")
    public ResponseEntity<PagedModel<EntityModel<ProductDto>>> customerProductPage(
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String supplierName,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<ProductDto> pagedResourcesAssembler){
        Page<ProductDto> productPage = productService.getProductByFilterForCustomer(productCode, categoryName, supplierName, pageNumber, pageSize);
        PagedModel<EntityModel<ProductDto>> pagedModel = pagedResourcesAssembler.toModel(productPage);
        return ResponseEntity.ok(pagedModel);
    }
}
