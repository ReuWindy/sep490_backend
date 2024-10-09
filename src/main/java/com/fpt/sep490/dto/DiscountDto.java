package com.fpt.sep490.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiscountDto {
    private String description;
    private double amountPerUnit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
