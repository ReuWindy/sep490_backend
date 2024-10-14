package com.fpt.sep490.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AdminProductDto {
    private String productCode;
    private String productName;
    private String batchCode;
    private Date importDate;
    private String productQuantity;
}
