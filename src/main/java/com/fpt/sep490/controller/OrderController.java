package com.fpt.sep490.controller;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.*;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Order;
import com.fpt.sep490.service.OrderService;
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

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PagedModel<EntityModel<Order>>> getCustomerOrderPage(
            @PathVariable long customerId,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<Order> pagedResourcesAssembler
    ){
        Page<Order> orderpage = orderService.getOrderHistoryByCustomerId(customerId,orderCode,status,pageNumber,pageSize);
        PagedModel<EntityModel<Order>> pagedModel = pagedResourcesAssembler.toModel(orderpage);
        return ResponseEntity.ok(pagedModel);
    }
    @GetMapping("/details/{orderId}")
    public ResponseEntity<?> getOrderByOrderId(@PathVariable long orderId){
        OrderDto order = orderService.getOrderByOrderId(orderId);
        if(order != null){
            return ResponseEntity.status(HttpStatus.OK).body(order);
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
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<Order> pagedResourcesAssembler
            ){
        Page<Order> orderpage = orderService.getAdminOrder(customerName,status,pageNumber,pageSize);
        PagedModel<EntityModel<Order>> pagedModel = pagedResourcesAssembler.toModel(orderpage);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/daily-report")
    public ResponseEntity<?> getDailyOrder(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
        DailyOrderResponseDTO report = orderService.getDailyReport(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/top-selling-products")
    public ResponseEntity<?> getTopSellingProducts(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam("type") String type) {
        List<TopSaleProductDto> topProducts = orderService.getTopSellingProducts(date, type);
        return ResponseEntity.ok(topProducts);
    }

    @PostMapping("/admin/CreateOrder")
    public ResponseEntity<?> createAdminOrder(@RequestBody AdminOrderDto adminOrderDto){
        Order createdAdminOrder = orderService.createAdminOrder(adminOrderDto);
        if(createdAdminOrder != null){
            return ResponseEntity.status(HttpStatus.OK).body(createdAdminOrder);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @PostMapping("/customer/CreateOrder")
    public ResponseEntity<?> createCustomerOrder(@RequestBody CustomerOrderDto customerOrderDto){
        Order createdCustomerOrder = orderService.createCustomerOrder(customerOrderDto);
        if(createdCustomerOrder != null){
            return ResponseEntity.status(HttpStatus.OK).body(createdCustomerOrder);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @PostMapping("/admin/UpdateOrder/{orderId}")
    public ResponseEntity<?> updateOrderByAdmin(@PathVariable long orderId, @RequestBody AdminOrderDto adminOrderDto){
        Order updatedOrder = orderService.updateOrderByAdmin(orderId, adminOrderDto);
        if(updatedOrder != null){
            return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Updated Failed", HttpStatus.BAD_REQUEST,LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/admin/UpdateOrderDetail/{orderId}")
    public ResponseEntity<?> updateOrderDetailByAdmin(@PathVariable long orderId, @RequestBody AdminOrderDto adminOrderDto){
        Order updatedOrder = orderService.updateOrderDetailByAdmin(orderId,adminOrderDto);
        if(updatedOrder != null){
            return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Updated Failed", HttpStatus.BAD_REQUEST,LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
