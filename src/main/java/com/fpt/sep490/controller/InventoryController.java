package com.fpt.sep490.controller;

import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Inventory;
import com.fpt.sep490.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @PostMapping("/createInventory")
    public ResponseEntity<?> createInventory(@RequestBody InventoryDto inventoryDto){
        Inventory createdInventory = inventoryService.createInventory(inventoryDto);
        if (createdInventory != null) {
            return ResponseEntity.status(HttpStatus.OK).body(createdInventory);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Inventory Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/getInventory/{inventoryId}")
    public ResponseEntity<?> getInventoryById (@PathVariable long inventoryId){
        Inventory inventory = inventoryService.getInventoryById(inventoryId);
        if (inventory != null) {
            return ResponseEntity.status(HttpStatus.OK).body(inventory);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Get Inventory Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
