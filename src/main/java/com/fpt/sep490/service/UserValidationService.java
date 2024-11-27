package com.fpt.sep490.service;

import com.fpt.sep490.repository.UserRepository;
import com.fpt.sep490.security.dto.RegistrationRequest;
import com.fpt.sep490.security.dto.RegistrationResponse;
import com.fpt.sep490.utils.ExceptionMessageAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService {
    private static final String EMAIL_ALREADY_EXISTS = "email_already_exists";
    private static final String PASSWORD_INVALID = "password_invalid";
    private static final String USERNAME_ALREADY_EXISTS = "username_already_exists";
    private static final String PHONE_NUMBER_ALREADY_EXISTS = "phone_number_already_exists";

    private final UserRepository userRepository;

    private final ExceptionMessageAccessor exceptionMessageAccessor;

    public RegistrationResponse validateUser(RegistrationRequest registrationRequest) {
        Set<RegistrationResponse> registrationResponses = new HashSet<>();
        final String email = registrationRequest.getEmail();
        final String username = registrationRequest.getUsername();
        final String password = registrationRequest.getPassword();
        final String phone = registrationRequest.getPhone();

        if (!checkUsername(username)) {
            final String existsUsername = exceptionMessageAccessor.getMessage(null, USERNAME_ALREADY_EXISTS);
            registrationResponses.add(new RegistrationResponse(existsUsername));
        }

        if (!checkEmail(email)) {
            final String existsEmail = exceptionMessageAccessor.getMessage(null, EMAIL_ALREADY_EXISTS);
            registrationResponses.add(new RegistrationResponse(existsEmail));
        }

        if (!checkPassword(password)) {
            final String invalidPassword = exceptionMessageAccessor.getMessage(null, PASSWORD_INVALID);
            registrationResponses.add(new RegistrationResponse(invalidPassword));
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (RegistrationResponse registrationResponse : registrationResponses) {
            joiner.add(registrationResponse.getMessage());
        }
        String allError = joiner.toString();
        return new RegistrationResponse(allError);
    }

    private boolean checkUsername(String username) {

        final boolean existsByUsername = userRepository.existsByUsername(username);
        if (existsByUsername) {
            log.warn("{} is already being used!", username);
            return false;
        }
        return true;
    }

    private Boolean checkEmail(String email) {
        final boolean existsByEmail = userRepository.existsByEmail(email);
        if (existsByEmail) {
            log.warn("{} is already being used!", email);
            return false;
        }
        return true;
    }

    private Boolean checkPassword(String password) {
        boolean passwordIsOk = isStrongPassword(password);
        if (!passwordIsOk) {
            log.warn("{} is invalid format!", password);
            return false;
        }
        return true;
    }

    public static boolean isStrongPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>])(?=.*[^\\da-zA-Z]).{6,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
