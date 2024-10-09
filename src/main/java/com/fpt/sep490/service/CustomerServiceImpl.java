package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.Employee;
import com.fpt.sep490.repository.CustomerRepository;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }
    @Override
    public List<CustomerDto> getAllCustomersWithContractPrice() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(this::convertToDTO).collect(Collectors.toList());
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
        Customer existingCustomer = customerRepository.findById(customer.getId()).orElse(null);
        if(existingCustomer != null){
            existingCustomer.setPhone(customer.getPhone());
            existingCustomer.setFullName(customer.getFullName());
            existingCustomer.setAddress(customer.getAddress());
            customerRepository.save(existingCustomer);
            return existingCustomer;
        }
        return null;
    }

    @Override
    public Customer deleteCustomer(int id) {
        return null;
    }

    private CustomerDto convertToDTO(Customer customer) {
        double totalContractValue = customer.getContracts().stream()
                .mapToDouble(contract -> contract.getAmount())
                .sum();

        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                totalContractValue
        );
    }

}
