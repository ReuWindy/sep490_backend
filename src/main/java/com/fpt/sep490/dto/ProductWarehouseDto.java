package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class ProductWarehouseDto {
    private long id;
    private long productId;
    private long warehouseId;
    private double quantity;
}
