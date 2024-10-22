package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.service.ProductWarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/productwarehouse")
public class ProductWarehouseController {
    private final  ProductWarehouseService productWarehouseService;

    public ProductWarehouseController(ProductWarehouseService productWarehouseService) {
        this.productWarehouseService = productWarehouseService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProductWarehouse() {
        List<ProductWarehouse> productWarehouses = productWarehouseService.getAll();
        if(!productWarehouses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(productWarehouses);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Found Nothing", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/createforbatch/{id}")
    public ResponseEntity<?> createProductWarehouse(@PathVariable long id) {
        ProductWarehouse productWarehouse = productWarehouseService.createProductWarehouseFromBatchProduct(id);
        if(productWarehouse != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(productWarehouse);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}
