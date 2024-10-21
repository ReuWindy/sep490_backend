package com.fpt.sep490.service;

import com.fpt.sep490.dto.ContractDto;
import com.fpt.sep490.dto.OrderDetailDto;
import com.fpt.sep490.dto.OrderDto;
import com.fpt.sep490.model.Contract;
import com.fpt.sep490.model.Order;
import com.fpt.sep490.model.OrderDetail;
import com.fpt.sep490.repository.ContractRepository;
import com.fpt.sep490.repository.OrderDetailRepository;
import com.fpt.sep490.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ContractRepository contractRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, ContractRepository contractRepository){
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.contractRepository = contractRepository;
    }
    @Override
    public List<OrderDto> getOrderHistoryByCustomerId(long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDetailDto> getOrderHistoryDetailByOrderId(long orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        return orderDetails.stream().map(this::convertToOrderDetailDTO).collect(Collectors.toList());
    }

    @Override
    public ContractDto getContractDetailByContractId(long contractId) {
        Contract contract = contractRepository.findById(contractId).orElse(null);
        if(contract != null){
            return convertToContractDTO(contract);
        }
        return null;
    }

    private OrderDto convertToDTO(Order order) {
        OrderDto orderDTO = new OrderDto();
        orderDTO.setId(order.getId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setDeposit(order.getDeposit());
        orderDTO.setRemainingAmount(order.getRemainingAmount());
        orderDTO.setContractId(order.getContract().getId());
        orderDTO.setStatus(order.getStatus());
        return orderDTO;
    }

    private OrderDetailDto convertToOrderDetailDTO(OrderDetail orderDetail) {
        OrderDetailDto detailDTO = new OrderDetailDto();
        detailDTO.setId(orderDetail.getId());
        detailDTO.setName(orderDetail.getSupplierProduct().getProduct().getName());
        detailDTO.setQuantity(orderDetail.getQuantity());
        detailDTO.setUnitPrice(orderDetail.getUnitPrice());
        detailDTO.setTotalPrice(orderDetail.getTotalPrice());
        return detailDTO;
    }

    private ContractDto convertToContractDTO(Contract contract){
        ContractDto contractDto = new ContractDto();
        contractDto.setContractNumber(contract.getContractNumber());
        contractDto.setAmount(contract.getAmount());
        contractDto.setCustomerName(contract.getCustomer().getName());
        contractDto.setPdfFilePath(contract.getPdfFilePath());
        contractDto.setImageFilePath(contract.getImageFilePath());
        contractDto.setConfirmationDate(contract.getConfirmationDate());
        return contractDto;
    }
}
