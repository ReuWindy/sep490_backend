package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private String orderCode;
    private Date orderDate;
    private double totalAmount;
    private double deposit;  // Số tiền đặt cọc
    private double remainingAmount;  // Số tiền còn lại cần thanh toán
    private StatusEnum status;  // Trạng thái đơn hàng (pending, completed, cancelled)
    private Customer customer;
    private Set<OrderDetailDto> orderDetails;
}
