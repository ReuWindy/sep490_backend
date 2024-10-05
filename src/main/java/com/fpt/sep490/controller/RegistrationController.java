package com.fpt.sep490.controller;

import com.fpt.sep490.security.dto.RegistrationRequest;
import com.fpt.sep490.security.dto.RegistrationResponse;
import com.fpt.sep490.security.service.UserService;
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

    private final UserService userService;

    @PostMapping
    public ResponseEntity<RegistrationResponse> registrationRequest(@Valid @RequestBody RegistrationRequest registrationRequest) {

        final RegistrationResponse registrationResponse = userService.registration(registrationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(registrationResponse);
    }

}

