package com.fpt.sep490.controller;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Contract;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.service.CustomerService;
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

    @GetMapping("/")
    public ResponseEntity<?> getAllCustomerWithContractPrice(){
        List<CustomerDto> customers = customerService.getAllCustomersWithContractPrice();
        if(!customers.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        }
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable int id){
        Customer customer = customerService.getCustomerById(id);
        if (customer != null) {
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createContract")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        return null;
    }

    @PostMapping("/updateContract")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer) {
       return null;
    }

}