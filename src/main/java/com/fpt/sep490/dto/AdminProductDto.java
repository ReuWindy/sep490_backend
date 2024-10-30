package com.fpt.sep490.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AdminProductDto {
    private int id;
    private String productCode;
    private String productName;
    private String batchCode;
    private Date importDate;
    private long price;
    private String productQuantity;
    private String supplierName;
}
