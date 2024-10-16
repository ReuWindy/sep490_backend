package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {
    public List<User> getAllCustomers();
    User getCustomerById(int id);
    Customer createCustomer(Customer customer);
    User updateCustomer(User user);
    Customer deleteCustomer(int id);
    Page<Customer> getCustomerByFilter(String fullName, String email, String phone, int pageNumber, int pageSize );
}
