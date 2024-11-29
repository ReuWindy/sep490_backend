package com.fpt.sep490.controller;

import com.fpt.sep490.dto.DiscountDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Discount;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.DiscountService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {
    private final DiscountService discountService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;

    public DiscountController(DiscountService discountService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.discountService = discountService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
    }

    @GetMapping
    public ResponseEntity<?> getAllDiscounts() {
        List<Discount> discounts = discountService.getAllDiscounts();
        if (!discounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(discounts);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDiscountById(@PathVariable int id) {
        Discount discount = discountService.getDiscountById(id);
        if (discount != null) {
            return ResponseEntity.status(HttpStatus.OK).body(discount);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDiscount(HttpServletRequest request, @RequestBody DiscountDto discountDto) {
        Discount createdDiscount = discountService.createDiscount(discountDto);
        String token = jwtTokenManager.resolveToken(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        userActivityService.logAndNotifyAdmin(username, "CREATE_DISCOUNT", discountDto.getDescription());
        if (createdDiscount != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDiscount);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDiscount(HttpServletRequest request, @PathVariable int id, @RequestBody Discount discount) {
        Discount existingDiscount = discountService.getDiscountById(id);
        if (existingDiscount == null) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Discount not found", HttpStatus.NOT_FOUND, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Discount updatedDiscount = discountService.updateDiscount(discount);
        String token = jwtTokenManager.resolveToken(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        userActivityService.logAndNotifyAdmin(username, "UPDATE_DISCOUNT", discount.getDescription());
        if (updatedDiscount != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedDiscount);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("disable/{id}")
    public ResponseEntity<?> disableDiscount(HttpServletRequest request, @PathVariable int id) {
        Discount disabledDiscount = discountService.disableDiscount(id);
        if (disabledDiscount != null) {
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DISABLE_DISCOUNT", disabledDiscount.getDescription());
            return ResponseEntity.status(HttpStatus.OK).body(disabledDiscount);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Disable Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/update-status")
    public ResponseEntity<?> updateDiscountStatus(HttpServletRequest request) {
        discountService.AutoDisableExpiredDiscount();
        return ResponseEntity.status(HttpStatus.OK).body("Discount status updated successfully");
    }

}
