package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderDto {
    private long customerId;
    private StatusEnum status;
    private double totalAmount;
    private double deposit;
    private double remainingAmount;
    private List<OrderDetailDto> orderDetails;
}
