package com.fpt.sep490.api.Service;

import com.fpt.sep490.Enum.SalaryType;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.security.dto.CreateUserRequest;
import com.fpt.sep490.security.dto.RegistrationRequest;
import com.fpt.sep490.security.dto.RegistrationResponse;
import com.fpt.sep490.security.mapper.UserMapper;
import com.fpt.sep490.security.service.UserServiceImpl;
import com.fpt.sep490.service.UserValidationService;
import com.fpt.sep490.utils.GeneralMessageAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateUserByAdminTests {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeRoleRepository employeeRoleRepository;
    @Mock
    private SalaryDetailRepository salaryDetailRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PriceRepository priceRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private GeneralMessageAccessor generalMessageAccessor;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void UserService_CreateUserByAdmin_CreateCustomer(){
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "User Test",
                "User12345",
                "User123456@",
                "User123456@",
                "123456789",
                true,
                "user@gmail.com",
                "user_address",
                new Date(90,5,15),
                true);

        CreateUserRequest createUserRequest = new CreateUserRequest(
                "User Test",
                "user@gmail.com",
                "User12345",
                "User123456@",
                true,
                "123456789",
                "user_address",
                new Date(90, 5, 15),
                true,
                UserType.ROLE_CUSTOMER,
                1L,
                "Employee role description.",
                SalaryType.MONTHLY,
                100.0,
                "ABC Bank",
                "1234567890",
                "User.jpg"
        );
        Customer customer = new Customer();
        Price price = new Price(1L, "Standard Price", new HashSet<>(),new HashSet<>());
        UserType userType = UserType.ROLE_CUSTOMER;
        EmployeeRole employeeRole = new EmployeeRole(1L,"Role 1");

        when(bCryptPasswordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userMapper.convertToCustomer(registrationRequest)).thenReturn(customer);
        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        RegistrationResponse response = userService.createUserByAdmin(registrationRequest, userType, createUserRequest);


        assertNotNull(response);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository).save(any(Customer.class));
        verify(customerRepository).save(customerCaptor.capture());

        Customer capturedCustomer = customerCaptor.getValue();

        assertNotNull(capturedCustomer);
        assertEquals("User Test", capturedCustomer.getName());
        assertEquals("user@gmail.com", capturedCustomer.getEmail());
        assertEquals("User12345", capturedCustomer.getUsername());
        assertEquals("123456789", capturedCustomer.getPhone());
    }

    @Test
    public void UserService_CreateUserByAdmin_CreateEmployee(){
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "User Test",
                "User12345",
                "User123456@",
                "User123456@",
                "123456789",
                true,
                "user@gmail.com",
                "user_address",
                new Date(90,5,15),
                true);

        CreateUserRequest createUserRequest = new CreateUserRequest(
                "User Test",
                "user@gmail.com",
                "User12345",
                "User123456@",
                true,
                "123456789",
                "user_address",
                new Date(90, 5, 15),
                true,
                UserType.ROLE_EMPLOYEE,
                1L,
                "Employee role description.",
                SalaryType.MONTHLY,
                100.0,
                "ABC Bank",
                "1234567890",
                "User.jpg"
        );
        Employee employee = new Employee();
        UserType userType = UserType.ROLE_EMPLOYEE;
        EmployeeRole employeeRole = new EmployeeRole(1L,"Role 1");

        when(employeeRoleRepository.findById(1L)).thenReturn(Optional.of(employeeRole));
        when(bCryptPasswordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");


        RegistrationResponse response = userService.createUserByAdmin(registrationRequest, userType, createUserRequest);


        assertNotNull(response);

        verify(salaryDetailRepository).save(any(SalaryDetail.class));
        verify(roleRepository).save(any(Role.class));
        verify(employeeRepository).save(any(Employee.class));

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());

        Employee captureEmployee = employeeCaptor.getValue();

        assertNotNull(captureEmployee);
        assertEquals("ABC Bank", captureEmployee.getBankName());
        assertEquals("1234567890", captureEmployee.getBankNumber());
    }

}
