package com.fpt.sep490.controller;

import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.dto.DeleteBatchProductRequest;
import com.fpt.sep490.dto.UpdateBatchProductRequest;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.BatchProductService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/batchproducts")
public class BatchProductController {
    private final BatchProductService batchProductService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;


    public BatchProductController(BatchProductService batchProductService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.batchProductService = batchProductService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
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

    @GetMapping("/getByBatchId/{id}")
    public ResponseEntity<?> getByBatchId(@PathVariable long id) {
        try{
            List<BatchProduct> batchProductList = batchProductService.getBatchProductByBatchId(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(batchProductList);
        }catch (Exception e){
            final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

//    @PostMapping("/createBatchProducts/{batchCode}")
//    public ResponseEntity<?> createBatchProduct(@PathVariable String batchCode, @RequestBody BatchProductDto batchProductDto) {
//        BatchProduct batchProduct = batchProductService.createBatchProduct(batchProductDto, batchCode);
//        if (batchProduct != null) {
//            return ResponseEntity.status(HttpStatus.CREATED).body(batchProduct);
//        }
//        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }

    @PostMapping("/addMoreBatchProductToBatch")
    public ResponseEntity<?> addMoreBatchProductToBatch(HttpServletRequest request, @Valid @RequestBody BatchProductDto batchProductDto) {
        try{
            BatchProduct batchProduct = batchProductService.addMoreBatchProductToBatch(batchProductDto);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "ADD_BATCH_PRODUCT_TO_BATCH", "Tạo danh mục: "+ batchProduct.getProduct().getName() + " by " + username);
            return ResponseEntity.status(HttpStatus.OK).body(batchProduct);
        }catch (Exception e){
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/update/{batchProductId}")
    public ResponseEntity<?> updateBatchProduct(HttpServletRequest request, @Valid @RequestBody UpdateBatchProductRequest requestUpdate, Long batchProductId) {
        try{
            BatchProduct batchProduct = batchProductService.updateBatchProduct(requestUpdate, batchProductId);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_BATCH_PRODUCT", "Tạo danh mục: "+ batchProduct.getProduct().getName() + " by " + username);
            return ResponseEntity.status(HttpStatus.OK).body(batchProduct);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/deleteMany")
    public ResponseEntity<?> deleteBatchProduct(HttpServletRequest request, @Valid @RequestBody DeleteBatchProductRequest requestDelete) {
        try {
            List<BatchProduct> batchProduct = batchProductService.deleteBatchProducts(requestDelete);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DELETE_BATCH_PRODUCT", "Xóa sản phẩm trong lô bởi: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(batchProduct);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
