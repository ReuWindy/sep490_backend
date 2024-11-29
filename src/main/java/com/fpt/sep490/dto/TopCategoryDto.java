package com.fpt.sep490.dto;

import lombok.Data;

@Data
public class TopCategoryDto {
    private String categoryName;
    private long totalOrders;
    private long totalQuantity;

    public TopCategoryDto(String name, long count, long sum) {
    }
}
