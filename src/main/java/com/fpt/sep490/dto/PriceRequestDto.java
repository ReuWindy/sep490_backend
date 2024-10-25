package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceRequestDto {
    private Long customerId;
    private String name;
    private double unitPrice;
    private List<Long> productIds;
}
