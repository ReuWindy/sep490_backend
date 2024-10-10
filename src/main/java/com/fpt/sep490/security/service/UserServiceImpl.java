package com.fpt.sep490.security.service;

import com.fpt.sep490.dto.PasswordRequest;
import com.fpt.sep490.dto.UserDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.security.dto.AuthenticatedUserDto;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    public UserServiceImpl(JwtTokenManager jwtTokenManager, com.fpt.sep490.utils.SendMail sendMail, BCryptPasswordEncoder bCryptPasswordEncoder, UserValidationService userValidationService, GeneralMessageAccessor generalMessageAccessor, UserRepository userRepository, UserMapper userMapper, SalaryDetailRepository salaryDetailRepository, RoleRepository roleRepository,EmployeeRoleRepository employeeRoleRepository, EmployeeRepository employeeRepository) {
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
        if(user!= null) {
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
        if(username.equals(u.getUsername())){
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
        if(u != null){
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
            final User user = userMapper.convertToUser(registrationRequest);

            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }

            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setFullName(registrationRequest.getName());
            user.setCreateAt(new Date());
            user.setActive(true);
            user.setUserType(UserType.ROLE_CUSTOMER);

            userRepository.save(user);

            final String username = registrationRequest.getUsername();
            final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, username);

            return new RegistrationResponse(registrationSuccessMessage);
        }else {
            return errorResponse;
        }
    }

    @Override
    public RegistrationResponse createUserByAdmin(RegistrationRequest registrationRequest, UserType userType, long employeeRoleId) {
        return null;
    }

    @Override
    public AuthenticatedUserDto findAuthenticatedUserByUsername(String username) {
        final User user = findByUsername(username);
        return userMapper.convertToAuthenticatedUserDto(user);
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
