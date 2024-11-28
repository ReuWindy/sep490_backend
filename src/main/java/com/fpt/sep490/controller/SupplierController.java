package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.SupplierService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierService supplierService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final SimpMessagingTemplate messagingTemplate;

    public SupplierController(SupplierService supplierService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, SimpMessagingTemplate messagingTemplate) {
        this.supplierService = supplierService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSupplier() {
        List<Supplier> suppliers = supplierService.getAllSupplier();
        if (!suppliers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(suppliers);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/all/active")
    public ResponseEntity<?> getAllActiveSupplier() {
        List<Supplier> suppliers = supplierService.getAllActiveSupplier();
        if (!suppliers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(suppliers);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable int id) {
        Supplier supplier = supplierService.getSupplierById(id);
        if (supplier != null) {
            return ResponseEntity.status(HttpStatus.OK).body(supplier);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/get/{name}")
    public ResponseEntity<?> getSupplierByName(@PathVariable String name) {
        Supplier supplier = supplierService.getSupplierByName(name);
        if (supplier != null) {
            return ResponseEntity.status(HttpStatus.OK).body(supplier);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createSupplier")
    public ResponseEntity<?> createSupplier(HttpServletRequest request, @RequestBody Supplier supplier) {
        try {
            Supplier createdSupplier = supplierService.createSupplier(supplier);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_SUPPLIER", "Tạo mới nhà cung cấp " + createdSupplier.getName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/suppliers", "Nhà cung cấp " + createdSupplier.getId() + " đã được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/updateSupplier")
    public ResponseEntity<?> updateSupplier(HttpServletRequest request, @RequestBody Supplier supplier) {
        try {
            Supplier updatedSupplier = supplierService.updateSupplier(supplier);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_SUPPLIER", "Cập nhật nhà cung cấp " + updatedSupplier.getName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/suppliers", "Nhà cung cấp " + updatedSupplier.getId() + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedSupplier);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/getByFilter")
    public ResponseEntity<PagedModel<EntityModel<Supplier>>> getAllSuppliersByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<Supplier> pagedResourcesAssembler) {
        Page<Supplier> supplierPage = supplierService.getSupplierByFilter(name, email, phoneNumber, status, pageNumber, pageSize);
        PagedModel<EntityModel<Supplier>> pagedModel = pagedResourcesAssembler.toModel(supplierPage);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/getAllNameActive/names")
    public ResponseEntity<?> getAllSupplierNames() {
        List<String> resultList = supplierService.getAllSupplierNames();
        if (!resultList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(resultList);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> disableSupplier(HttpServletRequest request, @PathVariable long id) {
        try {
            Supplier supplier = supplierService.disableSupplier(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DISABLE_SUPPLIER", "Ẩn nhà cung cấp " + supplier.getName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/suppliers", "Nhà cung cấp " + supplier.getName() + " đã được tạo ẩn người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(supplier);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/enable/{id}")
    public ResponseEntity<?> enableSupplier(HttpServletRequest request, @PathVariable long id) {
        try {
            Supplier supplier = supplierService.enableSupplier(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "ENABLE_SUPPLIER", "Kích hoạt nhà cung cấp " + supplier.getName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/suppliers", "Nhà cung cấp " + supplier.getName() + " đã được kích hoạt bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(supplier);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}