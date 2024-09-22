package com.fpt.sep490.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchProductDto {
    private long productId;
    private int quantity;
    private double weight;
    private double price;
}
