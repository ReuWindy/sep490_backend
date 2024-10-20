package com.fpt.sep490.service;

import com.fpt.sep490.dto.OrderDetailDto;
import com.fpt.sep490.dto.OrderDto;
import com.fpt.sep490.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderService {

    List<OrderDto> getOrderHistoryByCustomerId(long customerId);

    List<OrderDetailDto> getOrderHistoryDetailByOrderId(long orderId);
}
