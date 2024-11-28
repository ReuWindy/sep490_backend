package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Warehouse;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.UserActivityService;
import com.fpt.sep490.service.WarehouseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final SimpMessagingTemplate messagingTemplate;

    public WarehouseController(WarehouseService warehouseService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, SimpMessagingTemplate messagingTemplate) {
        this.warehouseService = warehouseService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouse();
        if (!warehouses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(warehouses);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable int id) {
        try {
            Warehouse warehouse = warehouseService.getWarehouseById(id);
            return ResponseEntity.status(HttpStatus.OK).body(warehouse);
        } catch (Exception e) {
            ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<?> getWarehouseByName(@PathVariable String name) {
        try {
            Warehouse warehouse = warehouseService.getWarehouseByName(name);
            return ResponseEntity.status(HttpStatus.OK).body(warehouse);
        } catch (Exception e) {
            ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createWarehouse(HttpServletRequest request, @RequestBody Warehouse warehouse) {
        try {
            Warehouse createdWarehouse = warehouseService.createWarehouse(warehouse);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_WAREHOUSE", "Created warehouse: " + createdWarehouse.getName() + " bởi " + username);
            messagingTemplate.convertAndSend("/topic/warehouses", "Kho hàng " + warehouse.getName() + " đã được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWarehouse);
        } catch (Exception e) {
            ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateWarehouse(HttpServletRequest request, @RequestBody Warehouse warehouse) {
        try {
            Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouse);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_WAREHOUSE", "Updated warehouse: " + updatedWarehouse.getName() + " bởi " + username);
            messagingTemplate.convertAndSend("/topic/warehouses", "Kho hàng " + warehouse.getName() + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedWarehouse);
        } catch (Exception e) {
            ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}