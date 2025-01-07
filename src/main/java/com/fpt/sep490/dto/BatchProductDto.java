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
    private Double weightPerUnit;
    private String unit;
    private String batchCode;
    private String description;
    private String date;
    private Long batchId;
    private Long productId;
    private Long discountId;
    private Boolean isAdded;
    private String receiptType;
}
