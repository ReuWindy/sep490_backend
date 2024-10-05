package com.fpt.sep490.security.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class DeleteRequest {
    @NotEmpty(message = "{login_username_not_empty}")
    private String username;
}
