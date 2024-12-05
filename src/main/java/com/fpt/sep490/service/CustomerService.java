package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.dto.CustomerOrderSummaryDTO;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {
    List<CustomerDto> getAllCustomers();

    User getCustomerById(int id);

    Customer createCustomer(Customer customer);

    User updateCustomer(User user);

    User deleteCustomer(Long id);

    User enableCustomer(Long id);

    Page<User> getCustomerByFilter(String fullName, String email, String phone, int pageNumber, int pageSize);

    CustomerOrderSummaryDTO getCustomerOrderSummaryById(long customerId);
}