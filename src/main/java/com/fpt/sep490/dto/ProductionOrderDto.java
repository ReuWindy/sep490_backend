package com.fpt.sep490.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductionOrderDto {
    private String productionCode;
    private String description;
    private Date productionDate;
    private long finishedProductId;
    private double finishedQuantity;
    private List<ProductionMaterialDto> materials;
}
