package com.fpt.sep490.service;

import com.fpt.sep490.dto.*;
import com.fpt.sep490.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderService {

    List<OrderDto> getOrderHistoryByCustomerId(long customerId);

    List<OrderDetailDto> getOrderHistoryDetailByOrderId(long orderId);

    ContractDto getContractDetailByContractId(long contractId);
    Page<OrderDto> getOrderHistoryByCustomerId(long customerId,String orderCode, String orderStatus, int pageNumber, int pageSize);
    Order createAdminOrder(AdminOrderDto adminOrderDto);

    Order createCustomerOrder(CustomerOrderDto customerOrderDto);

    Page<Order> getAdminOrder(String name, String status, int pageNumber, int pageSize);
}
