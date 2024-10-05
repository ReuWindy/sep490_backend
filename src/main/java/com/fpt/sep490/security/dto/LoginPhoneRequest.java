package com.fpt.sep490.security.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginPhoneRequest {
    @NotEmpty(message = "Token not empty!")
    private String idToken;

}
