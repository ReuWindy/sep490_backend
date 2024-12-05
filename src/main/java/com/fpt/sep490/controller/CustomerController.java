package com.fpt.sep490.controller;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.dto.CustomerOrderSummaryDTO;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.User;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.CustomerService;
import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final UserActivityService userActivityService;
    private final JwtTokenManager jwtTokenManager;
    private final SimpMessagingTemplate messagingTemplate;


    public CustomerController(CustomerService customerService, UserActivityService userActivityService, JwtTokenManager jwtTokenManager, SimpMessagingTemplate messagingTemplate) {
        this.customerService = customerService;
        this.userActivityService = userActivityService;
        this.jwtTokenManager = jwtTokenManager;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomerWithContractPrice() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        if (!customers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/")
    public ResponseEntity<PagedModel<EntityModel<User>>> getAllCustomerByFilter(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<User> pagedResourcesAssembler) {
        Page<User> customerPage = customerService.getCustomerByFilter(fullName, email, phone, pageNumber, pageSize);
        PagedModel<EntityModel<User>> pagedModel = pagedResourcesAssembler.toModel(customerPage);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable int id) {
        User customer = customerService.getCustomerById(id);
        if (customer != null) {
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/{id}/order-summary")
    public ResponseEntity<CustomerOrderSummaryDTO> getCustomerOrderSummary(@PathVariable("id") long customerId) {
        CustomerOrderSummaryDTO summaryDTO = customerService.getCustomerOrderSummaryById(customerId);
        return ResponseEntity.ok(summaryDTO);
    }

    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        return null;
    }

    @PostMapping("/updateCustomer")
    public ResponseEntity<?> updateCustomer(HttpServletRequest request, @RequestBody User user) {
        try{
            User existingCustomer = customerService.updateCustomer(user);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_CUSTOMER", "Cập nhật khách hàng " + existingCustomer.getFullName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/customers", "Khách hàng với tên " + existingCustomer.getFullName() + " đã được cập nhật bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(existingCustomer);
        }catch( Exception e ) {
            final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> disableCustomer(HttpServletRequest request, @PathVariable long id) {
        try {
            User customer = customerService.deleteCustomer(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DISABLE_CUSTOMER", "Ẩn khách hàng " + customer.getFullName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/customers", "Khách hàng với tên " + customer.getFullName() + " đã được ẩn bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/enable/{id}")
    public ResponseEntity<?> enableCustomer(HttpServletRequest request, @PathVariable long id) {
        try {
            User customer = customerService.enableCustomer(id);
            String token = jwtTokenManager.resolveTokenFromCookie(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "ENABLE_CUSTOMER", "Khôi phục khách hàng " + customer.getFullName() + " bởi người dùng: " + username);
            messagingTemplate.convertAndSend("/topic/customers", "Khách hàng với tên " + customer.getFullName() + " đã được ẩn bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        } catch (Exception e) {
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
