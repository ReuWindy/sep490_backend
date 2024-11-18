package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class ProductionMaterialDto {
    private long productId;
    private String unit;
    private double weightPerUnit;
    private double proportion;
}
