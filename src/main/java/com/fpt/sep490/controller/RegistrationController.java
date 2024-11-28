package com.fpt.sep490.controller;

import com.fpt.sep490.security.dto.RegistrationRequest;
import com.fpt.sep490.security.dto.RegistrationResponse;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.utils.GeneralMessageAccessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {
    private final GeneralMessageAccessor generalMessageAccessor;
    private static final String REGISTRATION_SUCCESSFUL = "registration_successful";
    private final UserService userService;

    @PostMapping
    public ResponseEntity<RegistrationResponse> registrationRequest(@Valid @RequestBody RegistrationRequest registrationRequest) {

        final RegistrationResponse registrationResponse = userService.registration(registrationRequest);
        final String username = registrationRequest.getUsername();
        final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, username);

        if (registrationResponse.getMessage().endsWith(registrationSuccessMessage)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registrationResponse);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationResponse);
    }
}