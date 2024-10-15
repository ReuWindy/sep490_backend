package com.fpt.sep490.controller;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.WarehouseReceipt;
import com.fpt.sep490.service.WarehouseReceiptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/WarehouseReceipt")

public class WarehouseReceiptController {
    private final WarehouseReceiptService warehouseReceiptService;

    public WarehouseReceiptController(WarehouseReceiptService warehouseReceiptService) {
        this.warehouseReceiptService = warehouseReceiptService;
    }

    @PostMapping("/createReceipt/{batchCode}")
    public ResponseEntity<?> createReceipt(@PathVariable String batchCode , @RequestBody WarehouseReceiptDto warehouseReceiptDto) {
        WarehouseReceipt receipt = warehouseReceiptService.createWarehouseReceipt(warehouseReceiptDto, batchCode);
        if (receipt != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/createReceiptByBatchCode/{batchCode}")
    public ResponseEntity<?> createReceiptByBatchCode(@PathVariable String batchCode, @RequestParam ReceiptType receiptType ) {
        WarehouseReceipt receipt = warehouseReceiptService.createWarehouseReceiptByBatchCode(batchCode, receiptType);
        if (receipt != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
