package com.fpt.sep490.controller;

import com.fpt.sep490.dto.*;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Order;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.OrderService;
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
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderController(OrderService orderService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService, SimpMessagingTemplate messagingTemplate) {
        this.orderService = orderService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/history/{customerId}")
    public ResponseEntity<?> getOrderHistoryByCustomerId(@PathVariable long customerId) {
        List<OrderDto> orders = orderService.getOrderHistoryByCustomerId(customerId);
        if (orders != null) {
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Không tìm thấy danh sách lịch sử đơn hàng của khách hàng!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
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
    ) {
        Page<Order> orderpage = orderService.getOrderHistoryByCustomerId(customerId, orderCode, status, pageNumber, pageSize);
        PagedModel<EntityModel<Order>> pagedModel = pagedResourcesAssembler.toModel(orderpage);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/details/{orderId}")
    public ResponseEntity<?> getOrderByOrderId(@PathVariable long orderId) {
        OrderDto order = orderService.getOrderByOrderId(orderId);
        if (order != null) {
            return ResponseEntity.status(HttpStatus.OK).body(order);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Không tìm thấy đơn hàng này!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<?> getContractDetailByContractId(@PathVariable long contractId) {
        ContractDto contractDto = orderService.getContractDetailByContractId(contractId);
        if (contractDto != null) {
            return ResponseEntity.status(HttpStatus.OK).body(contractDto);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Không tìm thấy hợp đồng của đơn hàng này!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<PagedModel<EntityModel<Order>>> getAdminOrderPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            PagedResourcesAssembler<Order> pagedResourcesAssembler
    ) {
        Page<Order> orderpage = orderService.getAdminOrder(name, status, pageNumber, pageSize);
        PagedModel<EntityModel<Order>> pagedModel = pagedResourcesAssembler.toModel(orderpage);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/daily-report")
    public ResponseEntity<?> getDailyOrder(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
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

    @GetMapping("/weightStatistics")
    public ResponseEntity<OrderWeightStatisticsView> getOrderWeightStatistics(
            @RequestParam("timeFilter") String timeFilter) {
        OrderWeightStatisticsView statistics = orderService.getOrderWeightStatistics(timeFilter);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/invoiceSummary")
    public ResponseEntity<?> getInvoiceSummary(){
        List<InvoiceSummaryDto> invoiceSummary = orderService.getInvoiceSummary();
        return ResponseEntity.ok(invoiceSummary);
    }

    @PostMapping("/admin/CreateOrder")
    public ResponseEntity<?> createAdminOrder(HttpServletRequest request, @RequestBody AdminOrderDto adminOrderDto) {
        try{
            Order createdAdminOrder = orderService.createAdminOrder(adminOrderDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_ADMIN_ORDER", "Tạo danh mục: " + createdAdminOrder.getId() + " by " + username);
            messagingTemplate.convertAndSend("/topic/orders", "Đơn hàng " + createdAdminOrder.getId() + " đã được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(createdAdminOrder);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/customer/CreateOrder")
    public ResponseEntity<?> createCustomerOrder(HttpServletRequest request, @RequestBody CustomerOrderDto customerOrderDto) {
        try {
            Order createdCustomerOrder = orderService.createCustomerOrder(customerOrderDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_CUSTOMER_ORDER", "Tạo đơn hàng: " + createdCustomerOrder.getId() + " by " + username);
            messagingTemplate.convertAndSend("/topic/orders", "Đơn hàng " + createdCustomerOrder.getId() + " đã được tạo bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(createdCustomerOrder);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/admin/UpdateOrder/{orderId}")
    public ResponseEntity<?> updateOrderByAdmin(HttpServletRequest request, @PathVariable long orderId, @RequestBody AdminOrderDto adminOrderDto) {
        try{
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            Order updatedOrder = orderService.updateOrderByAdmin(orderId, adminOrderDto, username);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_ADMIN_ORDER", "Cập nhật đơn hàng: " + updatedOrder.getId() + " by " + username);
            messagingTemplate.convertAndSend("/topic/orders", "Đơn hàng " + updatedOrder.getId() + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/customer/UpdateOrder/{orderId}")
    public ResponseEntity<?> updateOrderByCustomer(HttpServletRequest request, @PathVariable long orderId, @RequestBody AdminOrderDto adminOrderDto){
        try{
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            Order updatedOrder = orderService.updateOrderByCustomer(orderId, adminOrderDto);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_CUSTOMER_ORDER","Cập nhật đơn hàng: " + updatedOrder.getId() + " by " + username);
            messagingTemplate.convertAndSend("/topic/orders", "Đơn hàng " + updatedOrder.getId() + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
        }catch (Exception e){
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/admin/UpdateOrderDetail/{orderId}")
    public ResponseEntity<?> updateOrderDetailByAdmin(HttpServletRequest request, @PathVariable long orderId, @RequestBody AdminOrderDto adminOrderDto) {
        try {
            Order updatedOrder = orderService.updateOrderDetailByAdmin(orderId, adminOrderDto);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_ADMIN_ORDER-DETAILS", "Cập nhật chi tiết đơn hàng: " + updatedOrder.getId() + " by " + username);
            messagingTemplate.convertAndSend("/topic/orders", "Chi tiết đơn hàng " + updatedOrder.getId() + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
        }catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}