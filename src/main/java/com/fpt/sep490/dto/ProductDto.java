package com.fpt.sep490.dto;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class ProductDto {
    private long id;
    private String name;
    private String description;
    private double price;
    private String image;
    private String categoryId;
    private Long supplierId;
    private Long unitOfMeasureId;
    private Long warehouseId;
    private Long batchId;
    private String productUnit;
}
