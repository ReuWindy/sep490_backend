package com.fpt.sep490.security.service;

import com.fpt.sep490.Enum.SalaryType;
import com.fpt.sep490.dto.PasswordRequest;
import com.fpt.sep490.dto.UserDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.security.dto.AuthenticatedUserDto;
import com.fpt.sep490.security.dto.CreateUserRequest;
import com.fpt.sep490.security.dto.RegistrationRequest;
import com.fpt.sep490.security.dto.RegistrationResponse;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.security.mapper.UserMapper;
import com.fpt.sep490.service.UserValidationService;
import com.fpt.sep490.utils.GeneralMessageAccessor;
import com.fpt.sep490.utils.RandomEmployeeCodeGenerator;
import com.fpt.sep490.utils.RandomPasswordGenerator;
import com.fpt.sep490.utils.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private static final String REGISTRATION_SUCCESSFUL = "registration_successful";
    private final JwtTokenManager jwtTokenManager;
    private final SendMail sendMail;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserValidationService userValidationService;
    private final GeneralMessageAccessor generalMessageAccessor;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SalaryDetailRepository salaryDetailRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PriceRepository priceRepository;


    public UserServiceImpl(JwtTokenManager jwtTokenManager, com.fpt.sep490.utils.SendMail sendMail, BCryptPasswordEncoder bCryptPasswordEncoder, UserValidationService userValidationService, GeneralMessageAccessor generalMessageAccessor, UserRepository userRepository, UserMapper userMapper, SalaryDetailRepository salaryDetailRepository, RoleRepository roleRepository, EmployeeRoleRepository employeeRoleRepository, EmployeeRepository employeeRepository, CustomerRepository customerRepository, PriceRepository priceRepository) {
        this.jwtTokenManager = jwtTokenManager;
        this.sendMail = sendMail;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userValidationService = userValidationService;
        this.generalMessageAccessor = generalMessageAccessor;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.salaryDetailRepository = salaryDetailRepository;
        this.roleRepository = roleRepository;
        this.employeeRoleRepository = employeeRoleRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.priceRepository = priceRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> listAllUser() {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUserByUserName(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            userRepository.delete(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User updateUserByUserName(String username, UserDto userDto) {
        return null;
    }

    @Override
    public User updateUserProfile(String token, UserDto userDto) {
        String username = jwtTokenManager.getUsernameFromToken(token);
        User u = userRepository.findByUsername(userDto.getUsername());
        if (username.equals(u.getUsername())) {
            u.setImage(userDto.getImage());
            u.setAddress(userDto.getAddress());
            userRepository.save(u);
            return u;
        }
        return null;
    }

    @Override
    public User changePassword(String token, PasswordRequest request) {
        String username = jwtTokenManager.getUsernameFromToken(token);

        User u = userRepository.findByUsername(username);
        if (u != null) {
            u.setPassword(bCryptPasswordEncoder.encode(request.getNewpass()));
            userRepository.save(u);
            return u;
        }
        return null;
    }

    @Override
    public RegistrationResponse registration(RegistrationRequest registrationRequest) {

        RegistrationResponse errorResponse = userValidationService.validateUser(registrationRequest);
        if (errorResponse.getMessage().isEmpty()) {
            final Customer user = UserMapper.INSTANCE.convertToCustomer(registrationRequest);

            user.setUsername(registrationRequest.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
            user.setPhone(registrationRequest.getPhone());
            user.setEmail(registrationRequest.getEmail());
            user.setAddress(registrationRequest.getAddress());
            user.setActive(registrationRequest.isActive());
            user.setDob(registrationRequest.getDob());
            user.setGender(registrationRequest.isGender());
            user.setFullName(registrationRequest.getName());
            user.setCreateAt(new Date());
            user.setUserType(UserType.ROLE_CUSTOMER);
            user.setActive(true);

            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }

            Price standardPrice = priceRepository.findById(1l).orElseThrow(() -> new RuntimeException("Lỗi thiết lập bảng giá!"));
            user.setName(registrationRequest.getName());
            user.setSupporter(false);
            user.setContracts(new HashSet<>());
            user.setPrice(standardPrice);

            customerRepository.save(user);

            final String username = registrationRequest.getUsername();
            final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, username);

            return new RegistrationResponse(registrationSuccessMessage);
        } else {
            return errorResponse;
        }
    }

    @Override
    public RegistrationResponse createUserByAdmin(RegistrationRequest registrationRequest, UserType userType, CreateUserRequest createUserRequest) {
        userValidationService.validateUser(registrationRequest);
        if (userType == UserType.ROLE_EMPLOYEE) {

            EmployeeRole employeeRole = employeeRoleRepository.findById(createUserRequest.getEmployeeRoleId()).orElseThrow(() -> new RuntimeException("Employee Role not found"));

            SalaryDetail salaryDetail = new SalaryDetail();
            if (employeeRole.getRoleName().equalsIgnoreCase("PORTER_EMPLOYEE")) {
                salaryDetail.setSalaryType(SalaryType.DAILY);
                salaryDetail.setDailyWage(0);
            } else {
                salaryDetail.setSalaryType(SalaryType.MONTHLY);
                salaryDetail.setDailyWage(createUserRequest.getDailyWage());
            }
            // set attributes of role
            Role role = new Role();
            role.setSalaryDetail(salaryDetail);
            role.setEmployeeRole(employeeRole);
            role.setDescription(createUserRequest.getDescription());

            // set attributes of employee
            Employee employee = new Employee();
            employee.setFullName(registrationRequest.getName());
            employee.setUsername(registrationRequest.getUsername());
            employee.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
            employee.setPhone(registrationRequest.getPhone());
            employee.setEmail(registrationRequest.getEmail());
            employee.setAddress(registrationRequest.getAddress());
            employee.setDob(createUserRequest.getDateOfBirth());
            employee.setGender(createUserRequest.isGender());
            employee.setUserType(userType);
            employee.setImage(createUserRequest.getImage());
            employee.setCreateAt(new Date());

            // set attributes of Employee
            employee.setEmployeeCode(RandomEmployeeCodeGenerator.generateEmployeeCode());
            employee.setJoinDate(new Date());
            employee.setBankName(createUserRequest.getBankName());
            employee.setBankNumber(createUserRequest.getBankNumber());
            employee.setRole(role);

            salaryDetailRepository.save(salaryDetail);
            roleRepository.save(role);
            employeeRepository.save(employee);
        } else {
            final Customer user = userMapper.convertToCustomer(registrationRequest);
            user.setUsername(createUserRequest.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
            user.setPhone(registrationRequest.getPhone());
            user.setEmail(registrationRequest.getEmail());
            user.setAddress(registrationRequest.getAddress());
            user.setActive(registrationRequest.isActive());
            user.setDob(registrationRequest.getDob());
            user.setGender(registrationRequest.isGender());
            user.setFullName(createUserRequest.getName());
            user.setImage(createUserRequest.getImage());
            user.setCreateAt(new Date());
            user.setUserType(userType);
            user.setActive(true);

            Price standardPrice = priceRepository.findById(1l).orElseThrow(() -> new RuntimeException("Standard Price Not Found!!"));
            user.setName(createUserRequest.getName());
            user.setSupporter(false);
            user.setContracts(new HashSet<>());
            user.setPrice(standardPrice);
            customerRepository.save(user);
         //   userRepository.save(user);
        }
        final String username = registrationRequest.getUsername();
        final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, username);
        return new RegistrationResponse(registrationSuccessMessage);
    }

    @Override
    public AuthenticatedUserDto findAuthenticatedUserByUsername(String username) {
        final User user = findByUsername(username);
        return UserMapper.INSTANCE.convertToAuthenticatedUserDto(user);
    }

    @Override
    public boolean sendPasswordToEmail(String email) {
        try {
            User u = userRepository.findUserByEmail(email);
            RandomPasswordGenerator rpg = new RandomPasswordGenerator();
            String pass = rpg.generateRandomPassword();
            u.setPassword(bCryptPasswordEncoder.encode(pass));
            userRepository.save(u);
            String bodyEmail = "Mật khẩu mới cho tài khoản " + u.getUsername() + " là: " + pass;
            sendMail.sendMailRender(u.getEmail(), "PASSWORD RECOVER", bodyEmail);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhone(phoneNumber);
    }

}
