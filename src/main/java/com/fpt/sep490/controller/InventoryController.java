package com.fpt.sep490.controller;

import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Inventory;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.InventoryService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final SimpMessagingTemplate messagingTemplate;

    public InventoryController(InventoryService inventoryService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, SimpMessagingTemplate messagingTemplate) {
        this.inventoryService = inventoryService;
        this.userActivityService = userActivityService;
        this.jwtTokenManager = jwtTokenManager;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/getAll")
    public ResponseEntity<PagedModel<EntityModel<InventoryDto>>> getAdminInventoryPage(
            @RequestParam(required = false) String inventoryCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<InventoryDto> pagedResourcesAssembler
    ) {
        Page<InventoryDto> inventoryPage = inventoryService.getInventoryByFilter(inventoryCode, startDate, endDate, pageNumber, pageSize);
        PagedModel<EntityModel<InventoryDto>> pagedModel = pagedResourcesAssembler.toModel(inventoryPage);

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/createInventory")
    public ResponseEntity<?> createInventory(HttpServletRequest request, @RequestBody InventoryDto inventoryDto) {
        try {
            Inventory createdInventory = inventoryService.createInventory(inventoryDto);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_RECEIPT", "Create inventory: " + createdInventory.getId() + " by " + username);
            messagingTemplate.convertAndSend("/topic/inventories", "Phiếu kiểm kho với id " + createdInventory.getId() + " đã được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(createdInventory);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Xảy ra lỗi trong quá trình tạo phiếu kiểm kho", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInventory(HttpServletRequest request, @PathVariable long id) {
        Inventory inventory = inventoryService.deleteInventory(id);
        String token = jwtTokenManager.resolveToken(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        userActivityService.logAndNotifyAdmin(username, "DELETE_RECEIPT", "Delete inventory: " + inventory.getId() + " by " + username);
        messagingTemplate.convertAndSend("/topic/inventories", "Phiếu kiểm kho với id " + inventory.getId() + " đã được ẩn bởi người dùng: " + username);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted receipt");
    }

    @GetMapping("/getInventory/{inventoryId}")
    public ResponseEntity<?> getInventoryById(@PathVariable long inventoryId) {
        Inventory inventory = inventoryService.getInventoryById(inventoryId);
        if (inventory != null) {
            return ResponseEntity.status(HttpStatus.OK).body(inventory);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Get Inventory Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/confirm-add-to-inventory/{inventoryId}")
    public ResponseEntity<?> confirmAndAddToInventory(HttpServletRequest request,
                                                      @PathVariable Long inventoryId,
                                                      @RequestBody InventoryDto inventoryDto) {
        try {
            String message = inventoryService.confirmAndAddSelectedProductToInventory(inventoryId, inventoryDto);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CONFIRM_ADD_TO_INVENTORY", "Xác nhận lưu phiếu kiểm kho bởi :" + username);
            messagingTemplate.convertAndSend("/topic/inventories", "Người dùng: " + username + " đã xác nhận lưu phiếu kiểm kho: "+ inventoryId );
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (RuntimeException e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}