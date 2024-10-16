package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.service.ProductWarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/productwarehouse")
public class ProductWarehouseController {
    private final  ProductWarehouseService productWarehouseService;

    public ProductWarehouseController(ProductWarehouseService productWarehouseService) {
        this.productWarehouseService = productWarehouseService;
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
