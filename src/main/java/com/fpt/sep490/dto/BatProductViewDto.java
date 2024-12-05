package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class BatProductViewDto {
    private String productCode;
    private String productName;
    private String price;
    private String unit;
    private double weightPerUnit;
    private int quantity;
    private int categoryId;
    private long supplierId;
    private long warehouseId;
    private String description;
}