package com.fpt.sep490.controller;

import com.fpt.sep490.dto.BatchDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.service.BatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/batches")
public class BatchController {
    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Batch>> getAllBatches() {
        List<Batch> batches = batchService.getAllBatches();
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBatchById(@PathVariable Long id) {
        Batch batch = batchService.getBatchById(Math.toIntExact(id));
        if (batch != null) {
            return ResponseEntity.ok(batch);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Batch Not Found", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBatch(@RequestBody BatchDto batchDto) {
        Batch createdBatch = batchService.createBatch(batchDto);
        if (createdBatch != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBatch);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Batch Creation Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/update/{batchId}")
    public ResponseEntity<?> updateBatch(@PathVariable Long batchId, @RequestBody BatchDto batchDto) {
        Batch updatedBatch = batchService.updateBatch(batchId, batchDto);
        if (updatedBatch != null) {
            return ResponseEntity.ok(updatedBatch);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Batch Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/batchCode/{code}")
    public ResponseEntity<?> getBatchByBatchCode(@PathVariable String code) {
        Batch batch = batchService.getBatchByBatchCode(code);
        if (batch != null) {
            return ResponseEntity.ok(batch);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Batch Not Found", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/supplier/{supplierName}")
    public ResponseEntity<?> getBatchBySupplierName(@PathVariable String supplierName) {
        Batch batch = batchService.getBatchBySupplierName(supplierName);
        if (batch != null) {
            return ResponseEntity.ok(batch);
        }
        ApiExceptionResponse response = new ApiExceptionResponse("Batch Not Found", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete Ok");
    }

    @PostMapping("/deletebatchwithproduct/{id}")
    public ResponseEntity<?> deleteBatchWithProduct(@PathVariable Long id) {
        batchService.deleteBatchWithProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete Ok");
    }
}

