package com.fpt.sep490.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DiscountDto {
    private String description;
    private double amountPerUnit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
