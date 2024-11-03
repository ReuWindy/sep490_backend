package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.controller.BatchController;
import com.fpt.sep490.dto.*;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.utils.RandomOrderCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final OrderActivityRepository orderActivityRepository;
    private final DiscountRepository discountRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, ContractRepository contractRepository,CustomerRepository customerRepository,ProductRepository productRepository,ProductPriceRepository productPriceRepository,OrderActivityRepository orderActivityRepository,DiscountRepository discountRepository){
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.contractRepository = contractRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.productPriceRepository = productPriceRepository;
        this.orderActivityRepository = orderActivityRepository;
        this.discountRepository = discountRepository;
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

    @Override
    public Page<OrderDto> getOrderHistoryByCustomerId(long customerId, String orderCode, String status, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize);
        OrderSpecification orderSpecification = new OrderSpecification();
        Specification<Order> spec = Specification.where(OrderSpecification.hasCustomerId(customerId));
        if(orderCode != null && !orderCode.isEmpty()){
            spec = spec.and(OrderSpecification.hasOrderCode(orderCode));
        }
        if(status != null && !status.isEmpty()){
            spec = spec.and(OrderSpecification.hasStatus(status));
        }
        Page<Order> orders = orderRepository.findAll(spec,pageable);
        return orders.map(this::convertToDTO);
    }

    @Override
    public Order createAdminOrder(AdminOrderDto adminOrderDto) {
        Customer customer = customerRepository.findById(adminOrderDto.getCustomerId()).orElseThrow(()->new RuntimeException("Customer Not Found !"));
        Order order = Order.builder()
                .orderCode(RandomOrderCodeGenerator.generateOrderCode())
                .customer(customer)
                .orderDate(new Date())
                .deposit(adminOrderDto.getDeposit())
                .remainingAmount(adminOrderDto.getRemainingAmount())
                .status(StatusEnum.PENDING)
                .build();
        double totalAmount = 0.0;
        Set<OrderDetail> orderDetails = new HashSet<>();
        for(var detailDto : adminOrderDto.getOrderDetails()){
            Product product = productRepository.findById(detailDto.getProductId()).orElseThrow(()->new RuntimeException("Product not found"));
            double discountPercentage = (detailDto.getDiscount() != null ? detailDto.getDiscount() : 0.0);
            double discountUnitPrice = detailDto.getUnitPrice() - discountPercentage;
            double totalPrice = discountUnitPrice * detailDto.getQuantity() * detailDto.getWeightPerUnit();

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(detailDto.getQuantity())
                    .productUnit(detailDto.getProductUnit())
                    .weightPerUnit(detailDto.getWeightPerUnit())
                    .unitPrice(discountUnitPrice)
                    .discount(detailDto.getDiscount() != null ? detailDto.getDiscount() : 0.0)
                    .totalPrice(totalPrice)
                    .build();
            orderDetails.add(orderDetail);
            totalAmount += totalPrice;
        }
        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        logOrderActivity(order,"CREATED","Created Order",customer.getName());
        return order;
    }

    @Override
    public Order createCustomerOrder(CustomerOrderDto customerOrderDto) {
        Customer customer = customerRepository.findById(customerOrderDto.getCustomerId()).orElseThrow(()->new RuntimeException("Customer Not Found !"));
        Order order = Order.builder()
                .orderCode(RandomOrderCodeGenerator.generateOrderCode())
                .customer(customer)
                .orderDate(new Date())
                .deposit(0.0)
                .remainingAmount(0.0)
                .status(StatusEnum.PENDING)
                .build();
        double totalAmount = 0.0;
        Set<OrderDetail> orderDetails = new HashSet<>();
        for(var detailDto : customerOrderDto.getOrderDetails()){
            Product product = productRepository.findById(detailDto.getProductId()).orElseThrow(()->new RuntimeException("Product not found"));
            DiscountDto discountDto = discountRepository.getByProductId(detailDto.getProductId());
            LocalDateTime today = LocalDateTime.now();
            double discountUnit = 0.0;
            if(discountDto != null){
                if(!today.isBefore(discountDto.getStartDate()) && !today.isAfter(discountDto.getEndDate())){
                    discountUnit = discountDto.getAmountPerUnit();
                }
            }
            double customUnitPrice = getCustomUnitPrice(customer, product, detailDto.getUnitPrice());
            double discountUnitPrice = customUnitPrice - discountUnit;
            double totalPrice = discountUnitPrice * ( detailDto.getQuantity() * detailDto.getWeightPerUnit() );

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(detailDto.getQuantity())
                    .productUnit(detailDto.getProductUnit())
                    .weightPerUnit(detailDto.getWeightPerUnit())
                    .unitPrice(discountUnitPrice)
                    .discount(discountUnit)
                    .totalPrice(totalPrice)
                    .build();
            orderDetails.add(orderDetail);
            totalAmount += totalPrice;
        }
        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        logOrderActivity(order,"CREATED","Created Order",customer.getName());
        return order;
    }

    @Override
    public Page<Order> getAdminOrder(String name, String status, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize);
        OrderSpecification spec = new OrderSpecification();
        Specification<Order> specification = spec.hasNameOrHasStatus(name,status);
        Page<Order> orders = orderRepository.findAll(specification,pageable);
        return orders;
    }

    @Override
    public Order updateOrderByAdmin(long orderId, AdminOrderDto adminOrderDto) {
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order Not Found"));
        StatusEnum status = adminOrderDto.getStatus();
        if(status.equals(StatusEnum.COMPLETED)){
            updatedOrder.setStatus(status);
        }else if(status.equals(StatusEnum.IN_PROCESS)) {
            updatedOrder.setDeposit(adminOrderDto.getDeposit());
            updatedOrder.setStatus(status);
            double updatedTotalAmount = 0.0;
            for (OrderDetailDto detailDto : adminOrderDto.getOrderDetails()) {
                for (OrderDetail updatedDetail : updatedOrder.getOrderDetails()) {
                    if (detailDto.getProductId().equals(updatedDetail.getId())) {
                        updatedDetail.setQuantity(detailDto.getQuantity());
                        double updatedPrice = updatedDetail.getUnitPrice() * detailDto.getQuantity();
                        updatedDetail.setTotalPrice(updatedPrice);
                        updatedTotalAmount += updatedPrice;
                        break;
                    }
                }
            }
            updatedOrder.setTotalAmount(updatedTotalAmount);
            double remainAmount = updatedTotalAmount - adminOrderDto.getDeposit();
            updatedOrder.setRemainingAmount(remainAmount);
        }else {
            updatedOrder.setStatus(status);
        }
        orderRepository.save(updatedOrder);
        return updatedOrder;
    }

    private OrderDto convertToDTO(Order order) {
        OrderDto orderDTO = new OrderDto();
        orderDTO.setId(order.getId());
        orderDTO.setOrderCode(order.getOrderCode());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setDeposit(order.getDeposit());
        orderDTO.setRemainingAmount(order.getRemainingAmount());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCustomer(order.getCustomer());
        Set<OrderDetailDto> orderDetailDtos = order.getOrderDetails().stream()
                .map(orderDetail -> new OrderDetailDto(
                        orderDetail.getProduct().getId(),
                        orderDetail.getProduct().getName(),
                        orderDetail.getProduct().getDescription(),
                        orderDetail.getQuantity(),
                        orderDetail.getUnitPrice(),
                        orderDetail.getWeightPerUnit(),
                        orderDetail.getProductUnit(),
                        orderDetail.getDiscount(),
                        orderDetail.getTotalPrice()
                ))
                .collect(Collectors.toSet());
        orderDTO.setOrderDetails(orderDetailDtos);
        return orderDTO;
    }

    private OrderDetailDto convertToOrderDetailDTO(OrderDetail orderDetail) {
        OrderDetailDto detailDTO = new OrderDetailDto();
        detailDTO.setProductId(orderDetail.getId());
        detailDTO.setName(orderDetail.getProduct().getName());
        detailDTO.setDescription(orderDetail.getProduct().getDescription());
        detailDTO.setQuantity(orderDetail.getQuantity());
        detailDTO.setUnitPrice(orderDetail.getUnitPrice());
        detailDTO.setProductUnit(orderDetail.getProductUnit());
        detailDTO.setWeightPerUnit(orderDetail.getWeightPerUnit());
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

    private double getCustomUnitPrice(Customer customer, Product product, double defaultPrice){
            if(customer.getPrice() != null){
                Optional<ProductPrice> productPriceOpt = productPriceRepository.findByPriceIdAndProductId(customer.getPrice().getId(), product.getId());
                if(productPriceOpt.isPresent()){
                    return productPriceOpt.get().getUnit_price();
                }
            }
        return defaultPrice;
    }
    private void logOrderActivity(Order order, String activityType, String description, String userPerform) {
        OrderActivity activity = OrderActivity.builder()
                .order(order)
                .activityType(activityType)
                .description(description)
                .timestamp(new Date())
                .userPerform(userPerform)
                .build();
        orderActivityRepository.save(activity);
    }
}
