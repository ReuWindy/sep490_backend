package com.fpt.sep490.controller;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.User;
import com.fpt.sep490.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController (CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomer(){
        List<User> customers = customerService.getAllCustomers();
        if(!customers.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/")
    public ResponseEntity<PagedModel<EntityModel<Customer>>> getAllCustomerByFilter(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<Customer> pagedResourcesAssembler){
        Page<Customer> customerPage = customerService.getCustomerByFilter(fullName, email, phone, pageNumber, pageSize);
        PagedModel<EntityModel<Customer>> pagedModel = pagedResourcesAssembler.toModel(customerPage);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable int id){
        User customer = customerService.getCustomerById(id);
        if (customer != null) {
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        return null;
    }

    @PostMapping("/updateCustomer")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer) {
        User existingCustomer = customerService.updateCustomer(customer);
        if(existingCustomer != null){
            return ResponseEntity.status(HttpStatus.OK).body(existingCustomer);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST,LocalDateTime.now());
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
