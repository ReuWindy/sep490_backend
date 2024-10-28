package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class ExportProductDto {
    private String productName;
    private String unit;
    private double weightPerUnit;
    private int quantity;
    private String categoryId;
    private Long supplierId;
    private double warehouseId;
}
