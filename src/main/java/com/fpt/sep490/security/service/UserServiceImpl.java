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
        try {
            User u = userRepository.findByUsername(username);
            u.setUpdateAt(new Date());
            u.setDob(userDto.getDob());
            u.setPhone(userDto.getPhone());
            u.setEmail(userDto.getEmail());
            u.setAddress(userDto.getAddress());
            u.setImage(userDto.getImage());
            u.setFullName(userDto.getName());
        }catch (Exception e) {
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình cập nhật hồ sơ người dùng!");
        }
        return null;
    }

    @Override
    public User updateUserProfile(String token, UserDto userDto) {
        try {
            String username = jwtTokenManager.getUsernameFromToken(token);
            User u = userRepository.findByUsername(userDto.getUsername());
            if (username.equals(u.getUsername())) {
                if (userDto.getImage() != null) {
                    u.setImage(userDto.getImage());
                }
                if (userDto.getAddress() != null) {
                    u.setAddress(userDto.getAddress());
                }
                userRepository.save(u);
                return u;
            }
        }catch (Exception e){
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình cập nhật hồ sơ người dùng!");
        }
        return null;
    }

    @Override
    public User changePassword(String token, PasswordRequest request) {
        try {
            String username = jwtTokenManager.getUsernameFromToken(token);

            User u = userRepository.findByUsername(username);
            if (u == null) {
                throw new RuntimeException("Lỗi: Không tìm thấy người dùng!");
            }
            String newPassword = request.getNewpass();
            if (!UserValidationService.isStrongPassword(newPassword)) {
                throw new RuntimeException("Lỗi: Mật khẩu mới không tạo đúng theo format!");
            }
            u.setPassword(bCryptPasswordEncoder.encode(request.getNewpass()));
            userRepository.save(u);
            return u;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình cập nhật mật khẩu mới! " + e.getMessage());
        }
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
            if (!user.getPhone().matches("^[0-9]+$")) {
                throw new RuntimeException("Lỗi: Số điện thoại chỉ bao gồm số từ 0 đến 9!");
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Lỗi: Mật khẩu không thể để trống!");
            }

            Price standardPrice = priceRepository.findById(1l).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy bảng giá cơ bản!"));
            user.setName(registrationRequest.getName());
            user.setSupporter(false);
            user.setContracts(new HashSet<>());
            user.setPrice(standardPrice);
            try {
                customerRepository.save(user);
            }catch (Exception e){
                throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình đăng ký! " + e.getMessage());
            }
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

            // find employee role by id
            EmployeeRole employeeRole = employeeRoleRepository.findById(createUserRequest.getEmployeeRoleId()).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy chức vụ phù hợp của nhân viên!"));

            // set attributes of salary detail
            SalaryDetail salaryDetail = new SalaryDetail();
            if (employeeRole.getRoleName().equalsIgnoreCase("PORTER_EMPLOYEE")) {
                salaryDetail.setSalaryType(SalaryType.DAILY);
                salaryDetail.setDailyWage(0);
            } else {
                salaryDetail.setSalaryType(SalaryType.MONTHLY);
                if(createUserRequest.getDailyWage()<0){
                    throw new RuntimeException("Lỗi : Lương của nhân viên phải là số dương!");
                }
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
            if (!employee.getPhone().matches("^[0-9]+$")) {
                throw new RuntimeException("Lỗi: Số điện thoại chỉ bao gồm số từ 0 đến 9!");
            }
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
            if(employee.getBankName().trim().isEmpty() || employee.getBankName() ==null){
                throw new RuntimeException("Lỗi: Tên ngân hàng  của nhân viên không được để trống!");
            }
            employee.setBankNumber(createUserRequest.getBankNumber());
            if (!employee.getBankNumber().matches("^[0-9]+$")) {
                throw new RuntimeException("Lỗi: Số tài khoản của nhân viên chỉ bao gồm số từ 0 đến 9!");
            }
            employee.setRole(role);
            try {
                salaryDetailRepository.save(salaryDetail);
                roleRepository.save(role);
                employeeRepository.save(employee);
            }catch (Exception e){
                throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình tạo nhân viên mới! "+e.getMessage());
            }
        } else {
            final Customer user = userMapper.convertToCustomer(registrationRequest);
            user.setUsername(registrationRequest.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
            user.setPhone(registrationRequest.getPhone());
            user.setEmail(registrationRequest.getEmail());
            user.setAddress(registrationRequest.getAddress());
            user.setActive(registrationRequest.isActive());
            user.setDob(registrationRequest.getDob());
            user.setGender(registrationRequest.isGender());
            user.setFullName(registrationRequest.getName());
            user.setImage(createUserRequest.getImage());
            user.setCreateAt(new Date());
            user.setUserType(UserType.ROLE_CUSTOMER);
            user.setActive(true);
            if (!user.getPhone().matches("^[0-9]+$")) {
                throw new RuntimeException("Lỗi: Số điện thoại chỉ bao gồm số từ 0 đến 9!");
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Lỗi: Mật khẩu không thể để trống!");
            }
            Price standardPrice = priceRepository.findById(1l).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy bảng giá cơ bản!"));
            user.setName(registrationRequest.getName());
            user.setSupporter(false);
            user.setContracts(new HashSet<>());
            user.setPrice(standardPrice);
            try {
                customerRepository.save(user);
            }catch (Exception e){
                throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình tạo khách hàng mới! " + e.getMessage());
            }

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
            if(u==null){
                throw new RuntimeException("Lỗi: Không tìm thấy người dùng!");
            }
            RandomPasswordGenerator rpg = new RandomPasswordGenerator();
            String pass = rpg.generateRandomPassword();
            u.setPassword(bCryptPasswordEncoder.encode(pass));
            userRepository.save(u);
            String bodyEmail = "Mật khẩu mới cho tài khoản " + u.getUsername() + " là: " + pass;
            sendMail.sendMailRender(u.getEmail(), "PASSWORD RECOVER", bodyEmail);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình gửi thư ! "+e.getMessage());
        }
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhone(phoneNumber);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}