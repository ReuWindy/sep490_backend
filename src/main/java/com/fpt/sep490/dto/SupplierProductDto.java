package com.fpt.sep490.dto;

import java.util.List;

public class SupplierProductDto {
    private long id;
    private double price;
    private long supplierId;
    private long productId;
    private List<DiscountDto> discounts;
    private List<PromotionDto> promotions;
}
