package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Warehouse;
import com.fpt.sep490.repository.WarehouseRepository;
import com.fpt.sep490.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService){
        this.warehouseService = warehouseService;
    }

    @GetMapping("All")
    public ResponseEntity<?> getAllWarehouses(){
        List<Warehouse> warehouses = warehouseService.getAllWarehouse();
        if(!warehouses.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(warehouses);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable int id){
        Warehouse warehouse = warehouseService.getWarehouseById(id);
        if (warehouse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(warehouse);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getWarehouseByName(@PathVariable String name) {
        Warehouse warehouse = warehouseService.getWarehouseByName(name);
        if (warehouse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(warehouse);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createWarehouse")
    public ResponseEntity<?> createWarehouse(@RequestBody Warehouse warehouse) {
        Warehouse createdWarehouse = warehouseService.createWarehouse(warehouse);
        if (createdWarehouse != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWarehouse);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateWarehouse")
    public ResponseEntity<?> updateWarehouse(@RequestBody Warehouse warehouse) {
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouse);
        if (updatedWarehouse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedWarehouse);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
