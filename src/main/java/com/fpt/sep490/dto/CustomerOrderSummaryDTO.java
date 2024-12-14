package com.fpt.sep490.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CustomerOrderSummaryDTO {
    private int totalOrders;
    private double totalRemainingDeposit;
    private Date latestOrder;
}