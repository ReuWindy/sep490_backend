package com.fpt.sep490.controller;

import com.fpt.sep490.dto.AdminProductDto;
import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.service.ProductWarehouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/productwarehouse")
public class ProductWarehouseController {
    private final ProductWarehouseService productWarehouseService;

    public ProductWarehouseController(ProductWarehouseService productWarehouseService) {
        this.productWarehouseService = productWarehouseService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllProductWarehouse() {
        List<ProductWarehouse> productWarehouses = productWarehouseService.getAll();
        if (!productWarehouses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(productWarehouses);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found!!", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProductWarehouseProducts() {
        List<ProductWarehouseDto> productWarehouses = productWarehouseService.getAllProducts();
        if (!productWarehouses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(productWarehouses);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found!!", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/getAllProductsWarehouse")
    public ResponseEntity<PagedModel<EntityModel<ProductWarehouseDto>>> getAllProductsWarehouse(
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<ProductWarehouseDto> pagedResourcesAssembler) {
        Page<ProductWarehouseDto> productPage = productWarehouseService.getProductWarehousesByFilter(productCode, productName, categoryId, supplierId, warehouseId, pageNumber, pageSize);
        PagedModel<EntityModel<ProductWarehouseDto>> pagedModel = pagedResourcesAssembler.toModel(productPage);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/getAllIngredients")
    public ResponseEntity<?> getAllProductWarehouseIngredients() {
        List<ProductWarehouseDto> productWarehouses = productWarehouseService.getAllIngredients();
        if (!productWarehouses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(productWarehouses);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found!!", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createforbatch/{id}")
    public ResponseEntity<?> createProductWarehouse(@PathVariable long id) {
        ProductWarehouse productWarehouse = productWarehouseService.createProductWarehouseFromBatchProduct(id);
        if (productWarehouse != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(productWarehouse);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}