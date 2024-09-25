package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class DiscountDto {
    private long id;
    private long supplierProductId;
    private double discountPercentage;
}
