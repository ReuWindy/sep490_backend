package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerDto;
import com.fpt.sep490.dto.CustomerOrderSummaryDTO;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.User;
import com.fpt.sep490.model.UserType;
import com.fpt.sep490.repository.OrderRepository;
import com.fpt.sep490.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public CustomerServiceImpl(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<User> users = userRepository.findAllByUserTypeAndActive(UserType.ROLE_CUSTOMER, true);
        return users.stream().map(user -> {
            if (user instanceof Customer customer) {
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
            if (user instanceof Customer customer) {
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
    public CustomerOrderSummaryDTO getCustomerOrderSummaryById(long customerId) {
        Object[] result = orderRepository.getOrderSummaryByCustomerId(customerId);

        if (result != null && result.length > 1) {
            Number totalOrders = (Number) result[0];
            Number totalRemainingDeposit = (Number) result[1];

            if (totalOrders != null && totalRemainingDeposit != null) {
                return CustomerOrderSummaryDTO.builder()
                        .totalOrders(totalOrders.intValue())
                        .totalRemainingDeposit(totalRemainingDeposit.doubleValue())
                        .build();
            }
        }

        throw new RuntimeException("Không tìm thấy các giá trị này");
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return null;
    }

    @Override
    public User updateCustomer(User user) {
        User existingCustomer = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        User existingPhone = userRepository.findUserByPhone(user.getPhone());
        User existingEmail = userRepository.findUserByEmail(user.getEmail());

        if (existingCustomer == null) {
            throw new RuntimeException("Không tìm thấy khách hàng");
        }
        if (existingEmail.getId() != user.getId()){
            throw new RuntimeException("Đã có tài khoản được đăng ký bằng địa chỉ email này");
        }
        if (existingPhone.getId() != user.getId()){
            throw new RuntimeException("Đã có tài khoản được đăng ký bằng số điện thoại này");
        }
        if (user.getFullName().isBlank()) {
            throw new RuntimeException("Tên khách hàng không được bỏ trống");
        }
        if (user.getEmail().isBlank()) {
            throw new RuntimeException("Địa chỉ email không được bỏ trống");
        }
        String email = user.getEmail();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email).matches()) {
            throw new RuntimeException("Địa chỉ email không hợp lệ");
        }
        if (user.getPhone().isBlank()) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }
        String phoneNumber = user.getPhone();
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
        try {
            userRepository.save(existingCustomer);
            return existingCustomer;
        }catch (Exception e){
            throw new RuntimeException("Xảy ra lỗi trong quá trình cập nhật thông tin khách hàng!");
        }
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
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
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
}