package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderDto {
    private long customerId;
    private List<OrderDetailDto> orderDetails;
}
