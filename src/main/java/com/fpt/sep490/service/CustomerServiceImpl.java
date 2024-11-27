package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.User;
import com.fpt.sep490.model.UserType;
import com.fpt.sep490.repository.CustomerRepository;
import com.fpt.sep490.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<User> users = userRepository.findAllByUserType(UserType.ROLE_CUSTOMER);
        return users.stream().map(user -> {
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                return convertCustomerToDTO(customer);
            } else {
                return convertUserToCustomerDTO(user);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public User getCustomerById(int id) {
        Optional<User> customerOptional = userRepository.findById((long) id);
        if (customerOptional.isPresent()) {
            User user = customerOptional.get();
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                if (customer.getContracts() != null) {
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
        User existingCustomer = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        if (existingCustomer != null) {
            if (existingCustomer.getFullName().isBlank()) {
                throw new RuntimeException("Tên khách hàng không được bỏ trống");
            }
            if (existingCustomer.getPhone().isBlank()) {
                throw new RuntimeException("Số điện thoại không được bỏ trống");
            }
            if (existingCustomer.getEmail().isBlank()) {
                throw new RuntimeException("Địa chỉ email không được bỏ trống");
            }
            String email = existingCustomer.getEmail();
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

            Pattern pattern = Pattern.compile(emailRegex);
            if (!pattern.matcher(email).matches()) {
                throw new RuntimeException("Địa chỉ email không hợp lệ");
            }
            if (existingCustomer.getPhone().isBlank()) {
                throw new RuntimeException("Số điện thoại không được để trống");
            }
            String phoneNumber = existingCustomer.getPhone();
            String phoneNumberRegex = "^(\\+84|0)[3-9]{1}[0-9]{8}$";

            Pattern phonePattern = Pattern.compile(phoneNumberRegex);
            if (!phonePattern.matcher(phoneNumber).matches()) {
                throw new RuntimeException("Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10 hoặc 11 chữ số");
            }
            existingCustomer.setPhone(user.getPhone());
            existingCustomer.setFullName(user.getFullName());
            existingCustomer.setAddress(user.getAddress());
            existingCustomer.setEmail(user.getEmail());
            existingCustomer.setDob(user.getDob());
            existingCustomer.setGender(user.isGender());
            existingCustomer.setImage(user.getImage());
            existingCustomer.setUpdateAt(new Date());
            userRepository.save(existingCustomer);
            return existingCustomer;
        }
        return null;
    }

    @Override
    public User deleteCustomer(Long id) {
        User customerToDisable = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        customerToDisable.setActive(false);
        userRepository.save(customerToDisable);
        return customerToDisable;
    }

    @Override
    public User enableCustomer(Long id) {
        User customerToEnable = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        customerToEnable.setActive(true);
        userRepository.save(customerToEnable);
        return customerToEnable;
    }

    @Override
    public Page<User> getCustomerByFilter(String fullName, String email, String phone, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Specification<User> specification = CustomerSpecification.hasEmailOrNameOrPhoneNumber(fullName, phone, email);
        return userRepository.findAll(specification, pageable);
    }

    // Hàm chuyển đổi Customer sang CustomerDto
    private CustomerDto convertCustomerToDTO(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setDob(customer.getDob());
        dto.setImage(customer.getImage());
        dto.setGender(customer.isGender());
        dto.setPhoneNumber(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setContracts(customer.getContracts());
        return dto;
    }

    // Hàm chuyển đổi User sang CustomerDto (dành cho người chưa có hợp đồng)
    private CustomerDto convertUserToCustomerDTO(User user) {
        CustomerDto dto = new CustomerDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setDob(user.getDob());
        dto.setImage(user.getImage());
        dto.setGender(user.isGender());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setContracts(new HashSet<>());
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
