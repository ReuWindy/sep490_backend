package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class importProductDto {
    private String name;
    private String description;
    private double importPrice;
    private String image;
    private int quantity;
    //private double totalWeight;
    private double weightPerUnit;
    private String unit;
    private String categoryId;
    private Long supplierId;
    private Long unitOfMeasureId;
    private Long warehouseId;
    //private String productUnit;
}
