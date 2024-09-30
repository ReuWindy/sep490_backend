package com.fpt.sep490.controller;

import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.service.ProductService;
import com.fpt.sep490.service.ProductWarehouseService;
import com.fpt.sep490.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (!products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(products);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
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

    @GetMapping("/{id}/totalQuantity")
    public ResponseEntity<?> getTotalProductQuantity(@PathVariable long id) {
        double totalQuantity = productService.getTotalProductQuantity(id);
        return ResponseEntity.status(HttpStatus.OK).body(totalQuantity);
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
}
