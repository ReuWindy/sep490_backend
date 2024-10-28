package com.fpt.sep490.controller;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.WarehouseReceipt;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.UserActivityService;
import com.fpt.sep490.service.WarehouseReceiptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<WarehouseReceipt> warehouseReceipts = warehouseReceiptService.getAllWarehouseReceipts();
        if (!warehouseReceipts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(warehouseReceipts);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/")
    public ResponseEntity<PagedModel<EntityModel<WarehouseReceipt>>> getWarehouseReceiptByFilter(
            @RequestParam(required = false) Date importDate,
            @RequestParam(required = false) ReceiptType receiptType,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<WarehouseReceipt> pagedResourcesAssembler) {
        Page<WarehouseReceipt> warehouseReceipts = warehouseReceiptService.getWarehouseReceipts(importDate, receiptType, pageNumber, pageSize);
        PagedModel<EntityModel<WarehouseReceipt>> pagedModel = pagedResourcesAssembler.toModel(warehouseReceipts);
        return ResponseEntity.status(HttpStatus.OK).body(pagedModel);
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

    @PostMapping("/exportWarehouseReceipt/{batchCode}")
    public ResponseEntity<?> exportReceiptByBatchCode(@PathVariable String batchCode) {
        WarehouseReceipt receipt = warehouseReceiptService.createExportWarehouseReceipt(batchCode);
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
