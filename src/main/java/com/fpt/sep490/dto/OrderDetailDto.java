package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    private Long productId;
    private String name;
    private String description;
    private int quantity;
    private double unitPrice;
    private Double discount = 0.0;
    private double totalPrice;
}
