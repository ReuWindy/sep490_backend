package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class ProductWarehouseDto {
    private long id;
    private double price;
    private double weight;
    private String unit;
    private String batchCode;
    private String description;
    private long productId;
    private long warehouseId;
}
