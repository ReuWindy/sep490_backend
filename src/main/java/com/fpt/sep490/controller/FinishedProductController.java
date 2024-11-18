package com.fpt.sep490.controller;

import com.fpt.sep490.dto.FinishedProductDto;
import com.fpt.sep490.dto.FinishedProductView;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.FinishedProduct;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.FinishedProductService;
import com.fpt.sep490.service.FinishedProductSpecification;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/finishedProduct")
public class FinishedProductController {
    private final FinishedProductService finishedProductService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;

    public FinishedProductController(FinishedProductService finishedProductService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.finishedProductService = finishedProductService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllFinishedProduct() {
        List<FinishedProduct> finishedProducts = finishedProductService.getAllFinishedProduct();
        if (!finishedProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(finishedProducts);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getFinishedProductById(@PathVariable long id) {
        FinishedProduct finishedProduct = finishedProductService.getFinishedProductById(id);
        if (finishedProduct != null) {
            return ResponseEntity.status(HttpStatus.OK).body(finishedProduct);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Finished Product not found with id: " + id);
    }

    @GetMapping("/check-sum/{productId}")
    public ResponseEntity<?> checkSumProductProportion(@PathVariable("productId") long productId) {
        boolean isValid = finishedProductService.CheckSumProductProportion(productId);
        if (isValid) {
            return ResponseEntity.ok("Tổng tỷ lệ của các FinishedProduct đã đủ 100%.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tổng tỷ lệ của các FinishedProduct chưa đủ 100%.");
        }
    }

    @GetMapping("/finished-products")
    public ResponseEntity<?> getPagedFinishedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String productName) {

        Specification<FinishedProduct> spec = Specification.where(null);

        if (isActive != null) {
            spec = spec.and(FinishedProductSpecification.hasIsActive(isActive));
        }
        if (productName != null && !productName.isEmpty()) {
            spec = spec.and(FinishedProductSpecification.belongsToProductName(productName));
        }

        Page<FinishedProductView> pagedFinishedProducts = finishedProductService.getPagedFinishedProducts(page, size, spec);
        return ResponseEntity.ok(pagedFinishedProducts);
    }


    @PostMapping("/createFinishedProduct")
    public ResponseEntity<?> addFinishedProduct(HttpServletRequest request, @Valid @RequestBody FinishedProductDto finishedProductDto) {
        try {
            FinishedProduct createdFinishedProduct = finishedProductService.createFinishedProduct(finishedProductDto);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_FINISHED_PRODUCT", "Tạo sản phẩm đầu ra cho sản phẩm: " + createdFinishedProduct.getProduct().getName() + " by " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFinishedProduct);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFinishedProduct(@PathVariable("id") long id, HttpServletRequest request,
                                                   @Valid
                                                   @RequestBody FinishedProductDto finishedProductDto,
                                                   @RequestParam(required = false) Boolean status) {
        try {
            FinishedProduct updatedFinishedProduct = finishedProductService.updateFinishedProduct(id, finishedProductDto, status);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_FINISHED_PRODUCT", "Cập nhật sản phẩm đầu ra cho sản phẩm: " + updatedFinishedProduct.getProduct().getName() + " by " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedFinishedProduct);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteFinishedProduct(HttpServletRequest request, @PathVariable("id") long id) {
        try {
            FinishedProduct finishedProduct = finishedProductService.deleteFinishedProduct(id);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DELETE", "Xoá sản phẩm đầu ra cho sản phẩm: " + finishedProduct.getProduct().getName() + " by " + username);
            return ResponseEntity.status(HttpStatus.OK).body(finishedProduct);
        }catch (Exception e){
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
