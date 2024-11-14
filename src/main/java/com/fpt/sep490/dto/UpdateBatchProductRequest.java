package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class UpdateBatchProductRequest {
    private int quantity;
    private Double weightPerUnit;
    private String unit;
    private Double price;
    private String description;
}
