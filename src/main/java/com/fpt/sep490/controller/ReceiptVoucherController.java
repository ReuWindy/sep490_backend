package com.fpt.sep490.controller;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.ReceiptVoucherDto;
import com.fpt.sep490.dto.ReceiptVoucherExtendDto;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.ReceiptVoucherService;
import com.fpt.sep490.service.UserActivityService;
import com.fpt.sep490.service.WarehouseReceiptService;
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
@RequestMapping("/ReceiptVoucher")
public class ReceiptVoucherController {
    private final ReceiptVoucherService receiptVoucherService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final ModelMapper modelMapper;

    public ReceiptVoucherController(ReceiptVoucherService receiptVoucherService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, ModelMapper modelMapper) {
        this.receiptVoucherService = receiptVoucherService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<PagedModel<EntityModel<ReceiptVoucherDto>>> getReceiptVoucherByFilter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<ReceiptVoucherDto> pagedResourcesAssembler) {

        Page<ReceiptVoucherDto> receiptVoucherDtos = receiptVoucherService
                .getReceiptVoucherPagination(startDate, endDate, pageNumber, pageSize)
                .map(receiptVoucher -> modelMapper.map(receiptVoucher, ReceiptVoucherDto.class));

        PagedModel<EntityModel<ReceiptVoucherDto>> entityModels = pagedResourcesAssembler
                .toModel(receiptVoucherDtos);

        return ResponseEntity.ok().body(entityModels);
    }

    @PostMapping("/extend")
    public ResponseEntity<?> createBatchProduct(@RequestBody ReceiptVoucherExtendDto request) {
        ReceiptVoucher receiptVoucher = receiptVoucherService.extendReceipt(request.getId(), request.getNumber(), request.getType());
        if (receiptVoucher != null) {
            return ResponseEntity.ok(receiptVoucher);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Extend Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
