package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitWeightPairs {
    private String productUnit;
    private Double weightPerUnit;
    private Integer quantity;
}
