package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.model.Warehouse;
import com.fpt.sep490.service.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private  final SupplierService supplierService;

    public SupplierController(SupplierService supplierService){
        this.supplierService = supplierService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSupplier(){
        List<Supplier> suppliers = supplierService.getAllSupplier();
        if(!suppliers.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(suppliers);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable int id){
        Supplier supplier = supplierService.getSupplierById(id);
        if (supplier != null) {
            return ResponseEntity.status(HttpStatus.OK).body(supplier);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getSupplierByName(@PathVariable String name) {
        Supplier supplier = supplierService.getSupplierByName(name);
        if (supplier != null) {
            return ResponseEntity.status(HttpStatus.OK).body(supplier);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createSupplier")
    public ResponseEntity<?> createSupplier(@RequestBody Supplier supplier) {
        Supplier createdSupplier = supplierService.createSupplier(supplier);
        if (createdSupplier != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateSupplier")
    public ResponseEntity<?> updateSupplier(@RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.updateSupplier(supplier);
        if (updatedSupplier != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedSupplier);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<PagedModel<EntityModel<Supplier>>> getAllSuppliersByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<Supplier> pagedResourcesAssembler) {
        Page<Supplier> supplierPage = supplierService.getSupplierByFilter(name, email, phoneNumber, pageNumber, pageSize);

        PagedModel<EntityModel<Supplier>> pagedModel = pagedResourcesAssembler.toModel(supplierPage);

        return ResponseEntity.ok(pagedModel);
    }
}
