package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class BatProductViewDto {
    private String productCode;
    private String productName;
    private long productId;
    private String price;
    private String unit;
    private long id;
    private double weightPerUnit;
    private int quantity;
    private boolean added;
    private int categoryId;
    private String categoryName;
    private String supplierName;
    private String warehouseName;
    private long supplierId;
    private long warehouseId;
    private String description;
}