package com.fpt.sep490.dto;

import lombok.*;

@Data
public class BatchProductDto {
    private long productId;
    private int quantity;
    private double weight;
    private double price;
}
