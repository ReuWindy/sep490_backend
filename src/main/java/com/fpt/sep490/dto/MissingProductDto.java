package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MissingProductDto {
    private long id;
    private String name;
    private String productCode;
    private String unit;
    private double weightPerUnit;
    private Integer missingQuantity;
    private double importPrice;
    private Long categoryId;
    private String categoryName;
    private String supplierName;
    private Long supplierId;
}
