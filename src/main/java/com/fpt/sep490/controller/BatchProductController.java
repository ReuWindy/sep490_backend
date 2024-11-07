package com.fpt.sep490.controller;

import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.service.BatchProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/batchproducts")
public class BatchProductController {
    private final BatchProductService batchProductService;

    public BatchProductController(BatchProductService batchProductService) {
        this.batchProductService = batchProductService;
    }

    @GetMapping("/productId/{id}")
    public ResponseEntity<?> getBatchByProductId(@PathVariable Long id) {
        List<BatchProduct> batch = batchProductService.getBatchProductByProductId(id);
        if (batch != null) {
            return ResponseEntity.ok(batch);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Batch Product Not Found", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/batchCode/{batchCode}")
    public ResponseEntity<?> getBatchByBatchCode(@PathVariable String batchCode) {
        List<BatchProduct> batch = batchProductService.getBatchProductByBatchCode(batchCode);
        if (batch != null) {
            return ResponseEntity.ok(batch);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Batch Product Not Found", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createBatchProducts/{batchCode}")
    public ResponseEntity<?> createBatchProduct(@PathVariable String batchCode, @RequestBody BatchProductDto batchProductDto) {
        BatchProduct batchProduct = batchProductService.createBatchProduct(batchProductDto, batchCode);
        if (batchProduct != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(batchProduct);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
