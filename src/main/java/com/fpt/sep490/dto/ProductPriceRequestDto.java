package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceRequestDto {
    Set<ProductPriceDto> productPrice;
}
