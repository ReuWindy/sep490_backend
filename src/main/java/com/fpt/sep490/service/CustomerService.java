package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Customer;

import java.util.List;

public interface CustomerService {
    public List<CustomerDto> getAllCustomersWithContractPrice();
    Customer getCustomerById(int id);
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Customer deleteCustomer(int id);
}
