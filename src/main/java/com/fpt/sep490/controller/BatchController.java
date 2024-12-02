package com.fpt.sep490.controller;

import com.fpt.sep490.dto.BatchDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.BatchService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/batches")
public class BatchController {
    private final BatchService batchService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;

    public BatchController(BatchService batchService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.batchService = batchService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
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

    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<?> updateBatchStatus(HttpServletRequest request, @PathVariable Long id, @RequestBody String status) {
        Batch batch = batchService.updateBatchStatus(id, status);
        String token = jwtTokenManager.resolveTokenFromCookie(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        userActivityService.logAndNotifyAdmin(username, "UPDATE_BATCH_STATUS", "Update status for batch: " + batch.getBatchCode() + " by " + username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}