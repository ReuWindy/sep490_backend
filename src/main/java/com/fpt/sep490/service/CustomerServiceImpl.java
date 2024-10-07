package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }
    @Override
    public List<CustomerDto> getAllCustomersWithContractPrice() {
        return customerRepository.findAllCustomerWithContractPrice();
    }

    @Override
    public Customer getCustomerById(int id) {
        Optional<Customer> customer = customerRepository.findById((long) id);
        return customer.orElse(null);
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return null;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return null;
    }

    @Override
    public Customer deleteCustomer(int id) {
        return null;
    }
}
