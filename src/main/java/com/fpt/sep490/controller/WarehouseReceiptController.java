package com.fpt.sep490.controller;

import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.WarehouseReceipt;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.UserActivityService;
import com.fpt.sep490.service.WarehouseReceiptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/WarehouseReceipt")

public class WarehouseReceiptController {
    private final WarehouseReceiptService warehouseReceiptService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;


    public WarehouseReceiptController(WarehouseReceiptService warehouseReceiptService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.warehouseReceiptService = warehouseReceiptService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
    }

    @PostMapping("/createReceipt/{batchCode}")
    public ResponseEntity<?> createReceipt(@PathVariable String batchCode , @RequestBody WarehouseReceiptDto warehouseReceiptDto) {
        WarehouseReceipt receipt = warehouseReceiptService.createWarehouseReceipt(warehouseReceiptDto, batchCode);
        if (receipt != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed!!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/importWarehouseReceipt/{batchCode}")
    public ResponseEntity<?> createReceiptByBatchCode(@PathVariable String batchCode) {
        WarehouseReceipt receipt = warehouseReceiptService.createImportWarehouseReceipt(batchCode);
        if (receipt != null) {
            return ResponseEntity.status(HttpStatus.OK).body(receipt);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed!!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReceipt(HttpServletRequest request, @PathVariable long id, @RequestBody String document) {
        WarehouseReceipt warehouseReceipt = warehouseReceiptService.updateReceiptDocument(id, document);
        String token = jwtTokenManager.resolveToken(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        userActivityService.logAndNotifyAdmin(username, "UPDATE_RECEIPT_DOCUMENT", "Update document for receipt: "+ warehouseReceipt.getId() +" by "+ username);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully updated receipt");
    }
}
