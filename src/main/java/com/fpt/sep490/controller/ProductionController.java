package com.fpt.sep490.controller;

import com.fpt.sep490.dto.ImportProductionDto;
import com.fpt.sep490.dto.ProductionOrderDto;
import com.fpt.sep490.dto.ProductionOrderView;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.ProductionOrder;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.ProductionOrderService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/productionOrder")
public class ProductionController {
    private final ProductionOrderService productionOrderService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;

    public ProductionController(ProductionOrderService productionOrderService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.productionOrderService = productionOrderService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<ProductionOrder> productionOrders = productionOrderService.getAllProductionOrders();
        if (!productionOrders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(productionOrders);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ProductionOrder productionOrder = productionOrderService.getProductionOrderById(id);
        if (productionOrder != null) {
            return ResponseEntity.status(HttpStatus.OK).body(productionOrder);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn sản xuất với id: " + id);
    }

    @GetMapping("/getWithFilter")
    public ResponseEntity<Page<ProductionOrderView>> getProductionOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productionDate,desc") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort.split(",")[1]), sort.split(",")[0]));
        Page<ProductionOrderView> productionOrders = productionOrderService.getProductionOrders(
                status, startDate, endDate, productName, pageable);
        return ResponseEntity.ok(productionOrders);
    }

    @PostMapping("/createProductionOrder")
    public ResponseEntity<?> createProductionOrder(HttpServletRequest request, @Valid @RequestBody ProductionOrderDto dto) {
        try{
            ProductionOrder productionOrder = productionOrderService.createProductionOrder(dto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_PRODUCTION_ORDER", "Tạo đơn sản xuất cho: "+ productionOrder.getProductWarehouse().getProduct().getName()+ " bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(productionOrder);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateProductionOrder(HttpServletRequest request,@PathVariable long id , @Valid @RequestBody ProductionOrderDto dto) {
        try {
            ProductionOrder updatedProductionOrder = productionOrderService.updateProductionOrder(id, dto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_PRODUCTION_ORDER", "Cập nhật đơn sản xuất cho: " + updatedProductionOrder.getProductWarehouse().getProduct().getName() + " bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedProductionOrder);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteProductionOrder(HttpServletRequest request, @RequestParam Long id) {
        try {
            ProductionOrder deletedProductionOrder = productionOrderService.deleteProductionOrder(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DELETE_PRODUCTION_ORDER", "Xóa đơn sản xuất cho: " + deletedProductionOrder.getProductWarehouse().getProduct().getName() + " bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(deletedProductionOrder);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<?> confirmProductionOrder(HttpServletRequest request, @PathVariable Long id) {
        try {
            productionOrderService.confirmProductionOrder(id);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CONFIRM_PRODUCTION_ORDER", "Xác nhận đơn sản xuất bởi người dùng: " + username);
            return ResponseEntity.ok("Đơn sản xuất đã được xác nhận thành công!");
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/confirm-done/{id}")
    public ResponseEntity<?> confirmProductionOrderDone(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody ImportProductionDto dto,
            @RequestParam Double defectiveQuantity,
            @RequestParam String defectiveReason) {
        try {
            productionOrderService.ConfirmProductionOrderDone(id, dto, defectiveQuantity, defectiveReason);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DONE_PRODUCTION_ORDER", "Xác nhận đơn sản xuất hoàn thành bởi người dùng: " + username);
            return ResponseEntity.ok("Đơn sản xuất đã được hoàn thành!");
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
