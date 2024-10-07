package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchDto {
    private String batchCode;
    private int numberOfBags;
    private double totalWeight;
    private double totalPrice;
    private Date importDate;
    private boolean isDamaged;
    private long supplierId;
    private long warehouseId;
    private List<BatchProductDto> batchProducts;
}