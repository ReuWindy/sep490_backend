package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDetailDto {
    private long productId;
    private int quantity;
    private String description;
    private String unit;
    private double weightPerUnit;
    private int systemQuantity;
    private int quantity_discrepancy;
}
