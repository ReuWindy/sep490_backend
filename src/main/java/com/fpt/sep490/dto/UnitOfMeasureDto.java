package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class UnitOfMeasureDto {
    private long id;
    private String unitName;
    private double conversionFactor;
}
