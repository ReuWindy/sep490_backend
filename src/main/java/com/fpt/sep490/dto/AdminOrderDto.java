package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderDto {
    private long customerId;
    private double totalAmount;
    private double deposit;
    private double remainingAmount;
    private List<OrderDetailDto> orderDetails;

}
