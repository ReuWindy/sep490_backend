package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private long id;
    private String name;
    private String productCode;
    private String description;
    private double price;
    private String image;
    private String categoryId;
    private String categoryName;
    private Long supplierId;
    private Long unitOfMeasureId;
    private Long warehouseId;
    private boolean isDeleted;
    private Set<UnitWeightPairs> unitWeightPairsList;
}
