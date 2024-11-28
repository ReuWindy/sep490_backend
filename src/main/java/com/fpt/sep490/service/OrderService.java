package com.fpt.sep490.service;

import com.fpt.sep490.dto.*;
import com.fpt.sep490.model.Order;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface OrderService {

    List<OrderDto> getOrderHistoryByCustomerId(long customerId);

    OrderDto getOrderByOrderId(long orderId);

    ContractDto getContractDetailByContractId(long contractId);
    Page<Order> getOrderHistoryByCustomerId(long customerId,String orderCode, String orderStatus, int pageNumber, int pageSize);
    Order createAdminOrder(AdminOrderDto adminOrderDto);

    Order createCustomerOrder(CustomerOrderDto customerOrderDto);

    Page<Order> getAdminOrder(String name, String status, int pageNumber, int pageSize);

    Order updateOrderByAdmin(long orderId, AdminOrderDto adminOrderDto, String username);

    Order updateOrderDetailByAdmin(long orderId, AdminOrderDto adminOrderDto);

    DailyOrderResponseDTO getDailyReport(Date date);

    List<TopSaleProductDto> getTopSellingProducts(Date date, String type);

    OrderWeightStatisticsView getOrderWeightStatistics(String timeFilter);
}
