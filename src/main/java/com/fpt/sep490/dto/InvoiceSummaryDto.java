package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceSummaryDto {
    private int month;
    private Long totalReceipt;
    private Double totalPaid;
    private Double totalRemain;
}
