package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.CustomerRepository;
import com.fpt.sep490.repository.UserRepository;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, UserRepository userRepository){
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }
    @Override
    public List<User> getAllCustomers() {
        List<User> users = userRepository.findAllByUserType(UserType.ROLE_CUSTOMER);
        return users;
    }
    @Override
    public User getCustomerById(int id) {
        Optional<User> customerOptional = userRepository.findById((long) id);
        if(customerOptional.isPresent()){
            User user = customerOptional.get();
            if(user instanceof Customer){
                Customer customer = (Customer) user;
                if(customer.getContracts() != null){
                    ((Customer) user).setContracts(customer.getContracts());
                } else {
                    ((Customer) user).setContracts(new HashSet<>());
                }
            }
            return user;
        }
        return null;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return null;
    }

    @Override
    public User updateCustomer(User user) {
        User existingCustomer = userRepository.findById(user.getId()).orElse(null);
        if(existingCustomer != null){
            existingCustomer.setUsername(user.getUsername());
            existingCustomer.setDob(user.getDob());
            existingCustomer.setGender(user.isGender());
            existingCustomer.setPhone(user.getPhone());
            existingCustomer.setFullName(user.getFullName());
            existingCustomer.setAddress(user.getAddress());
            existingCustomer.setEmail(user.getEmail());
            existingCustomer.setImage(user.getImage());
            userRepository.save(existingCustomer);
            return existingCustomer;
        }
        return null;
    }

    @Override
    public Customer deleteCustomer(int id) {
        return null;
    }

    @Override
    public Page<User> getCustomerByFilter(String fullName, String email, String phone, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<User> specification = CustomerSpecification.hasEmailOrNameOrPhoneNumber(fullName, phone, email);
            return userRepository.findAll(specification, pageable);
        } catch (Exception e) {
            return null;
        }
    }

    // Hàm chuyển đổi Customer sang CustomerDto
    private CustomerDto convertCustomerToDTO(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setPhoneNumber(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        return dto;
    }

    // Hàm chuyển đổi User sang CustomerDto (dành cho người chưa có hợp đồng)
    private CustomerDto convertUserToCustomerDTO(User user) {
        CustomerDto dto = new CustomerDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        return dto;
    }

//    private CustomerDto convertToDTO(Customer customer) {
//        double totalContractValue = 0;
//        if(customer.getContracts() != null && !customer.getContracts().isEmpty()) {
//            totalContractValue = customer.getContracts().stream()
//                    .mapToDouble(contract -> contract.getAmount())
//                    .sum();
//        }
//
//        return new CustomerDto(
//                customer.getId(),
//                customer.getName(),
//                customer.getPhone(),
//                customer.getEmail(),
//                customer.getAddress(),
//                totalContractValue
//        );
//    }

}
