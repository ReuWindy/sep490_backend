package com.fpt.sep490.controller;

import com.fpt.sep490.dto.ExpenseVoucherDto;
import com.fpt.sep490.dto.ReceiptVoucherExtendDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.ExpenseVoucher;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.ExpenseVoucherService;
import com.fpt.sep490.service.UserActivityService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/ExpenseVoucher")
public class ExpenseVoucherController {
    private final ExpenseVoucherService expenseVoucherService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final ModelMapper modelMapper;

    public ExpenseVoucherController(ExpenseVoucherService expenseVoucherService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, ModelMapper modelMapper) {
        this.expenseVoucherService = expenseVoucherService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<PagedModel<EntityModel<ExpenseVoucherDto>>> getExpenseVoucherByFilter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<ExpenseVoucherDto> pagedResourcesAssembler) {

        Page<ExpenseVoucherDto> expenseVoucherDtos = expenseVoucherService
                .getExpenseVoucherPagination(startDate, endDate, pageNumber, pageSize)
                .map(expenseVoucher -> modelMapper.map(expenseVoucher, ExpenseVoucherDto.class));

        PagedModel<EntityModel<ExpenseVoucherDto>> entityModels = pagedResourcesAssembler
                .toModel(expenseVoucherDtos);

        return ResponseEntity.ok().body(entityModels);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createExpenseVoucher(@RequestBody ExpenseVoucherDto request) {
        ExpenseVoucher expenseVoucher = expenseVoucherService.createExpense(request);
        if (expenseVoucher != null) {
            return ResponseEntity.ok(expenseVoucher);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateExpenseVoucher(@RequestBody ExpenseVoucherDto request) {
        ExpenseVoucher expenseVoucher = expenseVoucherService.updateExpense(request);
        if (expenseVoucher != null) {
            return ResponseEntity.ok(expenseVoucher);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteExpenseVoucher(@RequestBody ExpenseVoucherDto request) {
        ExpenseVoucher expenseVoucher = expenseVoucherService.deleteExpense(request);
        if (expenseVoucher != null) {
            return ResponseEntity.ok(expenseVoucher);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
