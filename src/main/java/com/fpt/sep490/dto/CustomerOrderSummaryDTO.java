package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderSummaryDTO {
    private int totalOrders;
    private double totalRemainingDeposit;
    private Date latestOrder;
}