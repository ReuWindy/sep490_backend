package com.fpt.sep490.controller;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.ContractDto;
import com.fpt.sep490.dto.OrderDetailDto;
import com.fpt.sep490.dto.OrderDto;
import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Order;
import com.fpt.sep490.service.OrderService;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping("/history/{customerId}")
    public ResponseEntity<?> getOrderHistoryByCustomerId(@PathVariable long customerId){
        List<OrderDto> orders = orderService.getOrderHistoryByCustomerId(customerId);
        if(orders != null){
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/details/{orderId}")
    public ResponseEntity<?> getOrderHistoryDetailByOrderId(@PathVariable long orderId){
        List<OrderDetailDto> orderDetails = orderService.getOrderHistoryDetailByOrderId(orderId);
        if(orderDetails != null){
            return ResponseEntity.status(HttpStatus.OK).body(orderDetails);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<?> getContractDetailByContractId(@PathVariable long contractId){
        ContractDto contractDto = orderService.getContractDetailByContractId(contractId);
        if(contractDto != null){
            return ResponseEntity.status(HttpStatus.OK).body(contractDto);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<PagedModel<EntityModel<Order>>> getAdminOrderPage(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) StatusEnum statusEnum,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<Order> pagedResourcesAssembler
            ){
             return null;
    }
}
