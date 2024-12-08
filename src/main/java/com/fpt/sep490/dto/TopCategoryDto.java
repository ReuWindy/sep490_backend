package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopCategoryDto {
    private String categoryName;
    private long totalOrders;
    private long totalQuantity;
}
