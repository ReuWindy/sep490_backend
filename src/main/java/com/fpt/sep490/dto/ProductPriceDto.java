package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceDto {
    private double unitPrice;
    private Long productId;
    private Long priceId;
}
