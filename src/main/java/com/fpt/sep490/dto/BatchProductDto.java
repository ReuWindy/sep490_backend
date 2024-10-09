package com.fpt.sep490.dto;

import lombok.*;

@Data
public class BatchProductDto {
    private long id;
    private int quantity;
    private double price;
    private double weight;
    private String unit;
    private String description;
    private long batchId;
    private long productId;
    private long discountId;
}
