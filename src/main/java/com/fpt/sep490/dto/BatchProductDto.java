package com.fpt.sep490.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchProductDto {
    private Integer quantity;
    private Double price;
    private Double weight;
    private String unit;
    private String description;
    private Long batchId;
    private Long productId;
    private Long discountId;
    private Boolean isAdded;
}
