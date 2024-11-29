package com.fpt.sep490.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AdminProductDto {
    private int id;
    private String productCode;
    private String productName;
    private String batchCode;
    private Date importDate;
    private Date updateAt;
    private long price;
    private String productQuantity;
    private String supplierName;
    private String categoryName;
    private boolean active;
    private List<ProductWarehouseDto> productWarehouseDtos;
}
