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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        // Arrange : Set up data test
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

        // Mock the repository calls
        when(bCryptPasswordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userMapper.convertToCustomer(registrationRequest)).thenReturn(customer);
        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));

        // Act: call the service method under test
        RegistrationResponse response = userService.createUserByAdmin(registrationRequest, userType, createUserRequest);

        // Assert : Verify the result
        assertNotNull(response);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

        // Verify interactions with repositories
        verify(customerRepository).save(any(Customer.class));
        verify(customerRepository).save(customerCaptor.capture());

        Customer capturedCustomer = customerCaptor.getValue();

        // Assert : Verify the result
        assertNotNull(capturedCustomer);
        assertEquals("User Test", capturedCustomer.getName());
        assertEquals("user@gmail.com", capturedCustomer.getEmail());
        assertEquals("User12345", capturedCustomer.getUsername());
        assertEquals("123456789", capturedCustomer.getPhone());
    }

    @Test
    public void UserService_CreateUserByAdmin_CreateEmployee(){
        // Arrange : Set up the test data
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "User Test",
                "User12345",
                "User123456@",
                "User123456@",
                "0123456789",
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
                "0123456789",
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

        // Mock the repository calls
        when(employeeRoleRepository.findById(1L)).thenReturn(Optional.of(employeeRole));
        when(bCryptPasswordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");

        // Act: Call the method to test
        RegistrationResponse response = userService.createUserByAdmin(registrationRequest, userType, createUserRequest);

        // Assert: Verify the result
        assertNotNull(response);

        // Verify interactions with repositories
        verify(salaryDetailRepository).save(any(SalaryDetail.class));
        verify(roleRepository).save(any(Role.class));
        verify(employeeRepository).save(any(Employee.class));

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee captureEmployee = employeeCaptor.getValue();

        // Assert: Verify the result
        assertNotNull(captureEmployee);
        assertEquals("ABC Bank", captureEmployee.getBankName());
        assertEquals("1234567890", captureEmployee.getBankNumber());
    }

    @Test
    public void UserService_CreateUserByAdmin_InvalidRegistrationRequest() {
        // Arrange
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName(null); // Không hợp lệ

        // Mock validation service để ném ngoại lệ
        doThrow(new IllegalArgumentException("Invalid registration request"))
                .when(userValidationService).validateUser(registrationRequest);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUserByAdmin(registrationRequest, UserType.ROLE_EMPLOYEE, new CreateUserRequest()));
        assertEquals("Invalid registration request", exception.getMessage());
    }

    @Test
    public void UserService_CreateUserByAdmin_EmployeeRoleNotFound() {
        // Arrange
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmployeeRoleId(999L); // ID không tồn tại

        when(employeeRoleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.createUserByAdmin(new RegistrationRequest(), UserType.ROLE_EMPLOYEE, createUserRequest));
        assertEquals("Lỗi: Không tìm thấy chức vụ phù hợp của nhân viên!", exception.getMessage());
    }

    @Test
    public void UserService_CreateUserByAdmin_StandardPriceNotFound() {
        // Arrange : Set up data test
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

        // Mock the repository calls
        when(bCryptPasswordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userMapper.convertToCustomer(registrationRequest)).thenReturn(customer);
        when(priceRepository.findById(1L)).thenReturn(Optional.of(price));
        when(priceRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.createUserByAdmin(registrationRequest, UserType.ROLE_CUSTOMER, createUserRequest));
        assertEquals("Lỗi: Không tìm thấy bảng giá cơ bản!", exception.getMessage());
    }
}
