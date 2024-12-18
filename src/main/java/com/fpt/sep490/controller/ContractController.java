package com.fpt.sep490.controller;

import com.fpt.sep490.dto.ContractDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Contract;
import com.fpt.sep490.service.ContractService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/contracts")
public class ContractController {
    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllContracts() {
        List<Contract> contracts = contractService.getAllContracts();
        if (!contracts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(contracts);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContractById(@PathVariable int id) {
        Contract contract = contractService.getContractById(id);
        if (contract != null) {
            return ResponseEntity.status(HttpStatus.OK).body(contract);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/{contractNumber}")
    public ResponseEntity<?> getContractByName(@PathVariable String contractNumber) {
        Contract contract = contractService.getContractByContractNumber(contractNumber);
        if (contract != null) {
            return ResponseEntity.status(HttpStatus.OK).body(contract);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createContract")
    public ResponseEntity<?> createContract(@RequestBody ContractDto contract) {
        Contract createdContract = contractService.createContract(contract);
        if (createdContract != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdContract);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateContract")
    public ResponseEntity<?> updateContract(@RequestBody ContractDto contract) {
        Contract updatedContract = contractService.updateContract(contract);
        if (updatedContract != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedContract);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<PagedModel<EntityModel<Contract>>> getAdminContractPage(
            @RequestParam(required = false) String contractNumber,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<Contract> pagedResourcesAssembler
    ) {
        Page<Contract> contractPage = contractService.getContractByFilter(contractNumber, name, pageNumber, pageSize);
        PagedModel<EntityModel<Contract>> pagedModel = pagedResourcesAssembler.toModel(contractPage);

        return ResponseEntity.ok(pagedModel);
    }
}
