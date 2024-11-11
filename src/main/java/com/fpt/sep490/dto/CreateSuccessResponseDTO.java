package com.fpt.sep490.dto;

import java.time.LocalDateTime;

public record CreateSuccessResponseDTO(String status, String message, EmployeeResponseDTO data, LocalDateTime timestamp) {
}
