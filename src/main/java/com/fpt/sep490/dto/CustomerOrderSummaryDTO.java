package com.fpt.sep490.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerOrderSummaryDTO {
    private int totalOrders;
    private double totalRemainingDeposit;
}