package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.*;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.utils.RandomIncomeCodeGenerator;
import com.fpt.sep490.utils.RandomOrderCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final OrderActivityRepository orderActivityRepository;
    private final DiscountRepository discountRepository;
    private final ReceiptVoucherRepository receiptVoucherRepository;
    private final ProductWareHouseRepository productWareHouseRepository;
    private final WarehouseReceiptRepository warehouseReceiptRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, ContractRepository contractRepository, CustomerRepository customerRepository, ProductRepository productRepository, ProductPriceRepository productPriceRepository, OrderActivityRepository orderActivityRepository, DiscountRepository discountRepository, ReceiptVoucherRepository receiptVoucherRepository, ProductWareHouseRepository productWareHouseRepository, WarehouseReceiptRepository warehouseReceiptRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.contractRepository = contractRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.productPriceRepository = productPriceRepository;
        this.orderActivityRepository = orderActivityRepository;
        this.discountRepository = discountRepository;
        this.receiptVoucherRepository = receiptVoucherRepository;
        this.productWareHouseRepository = productWareHouseRepository;
        this.warehouseReceiptRepository = warehouseReceiptRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<OrderDto> getOrderHistoryByCustomerId(long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderByOrderId(long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng này!"));
        return convertToDTO(order);
    }

    @Override
    public ContractDto getContractDetailByContractId(long contractId) {
        Contract contract = contractRepository.findById(contractId).orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng của đơn hàng này!"));
        if (contract != null) {
            return convertToContractDTO(contract);
        }
        return null;
    }

    @Override
    public Page<Order> getOrderHistoryByCustomerId(long customerId, String orderCode, String status, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Order.desc("orderDate")));
        Specification<Order> spec = Specification.where(OrderSpecification.hasCustomerId(customerId));
        if (orderCode != null && !orderCode.isEmpty()) {
            spec = spec.and(OrderSpecification.hasOrderCode(orderCode));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and(OrderSpecification.hasStatus(status));
        }
        return orderRepository.findAll(spec, pageable);
    }
    @Transactional
    @Override
    public Order createAdminOrder(AdminOrderDto adminOrderDto) {
        Customer customer = customerRepository.findById(adminOrderDto.getCustomerId()).orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
        Order order = Order.builder()
                .orderCode(RandomOrderCodeGenerator.generateOrderCode())
                .customer(customer)
                .orderDate(new Date())
                .deposit(adminOrderDto.getDeposit())
                .orderPhone(adminOrderDto.getOrderPhone())
                .orderAddress(adminOrderDto.getOrderAddress())
                .remainingAmount(adminOrderDto.getTotalAmount())
                .status(StatusEnum.PENDING)
                .build();
        double totalAmount = 0.0;
        Set<OrderDetail> orderDetails = new HashSet<>();
        for (var detailDto : adminOrderDto.getOrderDetails()) {
            // Product product = productRepository.findById(detailDto.getProductId()).orElseThrow(()->new RuntimeException("Không tìm thấy sản phẩm!"));
            ProductWarehouse product = productWareHouseRepository.findByProductIdAndWeightPerUnitAndUnit(
                    detailDto.getProductId(),
                    detailDto.getWeightPerUnit(),
                    detailDto.getProductUnit()
            ).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm tương ứng trong kho!"));
            if (product.getQuantity() < detailDto.getQuantity()) {
                throw new RuntimeException("Số lượng sản phẩm trong kho không đủ!");
            }
            double discount = (detailDto.getDiscount() != null ? detailDto.getDiscount() : 0.0);
            double customerUnitPrice = getCustomUnitPrice(customer, product.getProduct(), detailDto.getUnitPrice());
            double discountUnitPrice = customerUnitPrice - discount;
            double totalPrice = discountUnitPrice * detailDto.getQuantity() * detailDto.getWeightPerUnit();

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product.getProduct())
                    .quantity(detailDto.getQuantity())
                    .productUnit(detailDto.getProductUnit())
                    .weightPerUnit(detailDto.getWeightPerUnit())
                    .unitPrice(discountUnitPrice)
                    .discount(discount)
                    .totalPrice(totalPrice)
                    .build();
            orderDetails.add(orderDetail);
            totalAmount += totalPrice;
        }
        order.setTotalAmount(totalAmount);
        order.setRemainingAmount(totalAmount);
        order.setOrderDetails(orderDetails);
        try {
            orderRepository.save(order);
            orderDetailRepository.saveAll(orderDetails);
        } catch (Exception e) {
            throw new RuntimeException("Xảy ra lỗi trong quá trình tạo đơn hàng!");
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, 1);
        logOrderActivity(order, customer.getName());
        return order;
    }

    @Transactional
    @Override
    public Order createCustomerOrder(CustomerOrderDto customerOrderDto) {
        Customer customer = customerRepository.findById(customerOrderDto.getCustomerId()).orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
        Order order = Order.builder()
                .orderCode(RandomOrderCodeGenerator.generateOrderCode())
                .customer(customer)
                .orderDate(new Date())
                .deposit(0.0)
                .orderAddress(customerOrderDto.getOrderAddress())
                .orderPhone(customerOrderDto.getOrderPhone())
                .remainingAmount(0.0)
                .status(StatusEnum.PENDING)
                .build();
        double totalAmount = 0.0;
        Set<OrderDetail> orderDetails = new HashSet<>();
        for (var detailDto : customerOrderDto.getOrderDetails()) {
            Product product = productRepository.findById(detailDto.getProductId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
            DiscountDto discountDto = discountRepository.getByProductId(detailDto.getProductId());
            LocalDateTime today = LocalDateTime.now();
            double discountUnit = 0.0;
            if (discountDto != null) {
                if (!today.isBefore(discountDto.getStartDate()) && !today.isAfter(discountDto.getEndDate())) {
                    discountUnit = discountDto.getAmountPerUnit();
                }
            }
            double customUnitPrice = getCustomUnitPrice(customer, product, detailDto.getUnitPrice());
            double discountUnitPrice = customUnitPrice - discountUnit;
            double totalPrice = discountUnitPrice * (detailDto.getQuantity() * detailDto.getWeightPerUnit());

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
        order.setRemainingAmount(totalAmount);
        order.setOrderDetails(orderDetails);
        try {
            orderRepository.save(order);
            orderDetailRepository.saveAll(orderDetails);
        } catch (Exception e) {
            throw new RuntimeException("Xảy ra lỗi trong quá trình tạo đơn hàng!");
        }
        logOrderActivity(order, customer.getName());
        return order;
    }

    @Override
    public Page<Order> getAdminOrder(String name, String status, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        Specification<Order> specification = OrderSpecification.hasNameOrHasStatus(name, status);
        return orderRepository.findAll(specification, pageable);
    }

    @Transactional
    @Override
    public Order updateOrderByAdmin(long orderId, AdminOrderDto adminOrderDto, String username) {
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("không tìm thấy đơn hàng!"));
        StatusEnum status = adminOrderDto.getStatus();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        if (status != null) {

            if(status == StatusEnum.CONFIRMED){
                validateProductQuantity(updatedOrder);
            }
            if (status == StatusEnum.IN_PROCESS) {
                processOrder(updatedOrder);
            }
            updatedOrder.setStatus(status);
            updatedOrder.setCreateBy(user.getFullName());
        } else {
            throw new RuntimeException("Xảy ra lỗi không cập nhật trạng thái đơn hàng!");
        }
        return updatedOrder;
    }

    @Transactional
    @Override
    public Order updateOrderDetailByAdmin(long orderId, AdminOrderDto adminOrderDto) {
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("không tìm thấy đơn hàng!"));
        double newDeposit = adminOrderDto.getDeposit();
        if(newDeposit < 0 ){
            throw new RuntimeException("Số tiền cọc không được âm!");
        }
        double updatedTotalAmount = 0.0;
            for (OrderDetailDto detailDto : adminOrderDto.getOrderDetails()) {
                boolean found = false;
                for (OrderDetail updatedDetail : updatedOrder.getOrderDetails()) {
                    if (detailDto.getProductId().equals(updatedDetail.getProduct().getId()) &&
                        detailDto.getProductUnit().equals(updatedDetail.getProductUnit()) &&
                        detailDto.getWeightPerUnit() == updatedDetail.getWeightPerUnit() ) {
                        found = true;
                        if(detailDto.getQuantity() < 0){
                            throw new RuntimeException("Số lượng sản phẩm không được âm: Product ID " + detailDto.getProductId());
                        }
                        updatedDetail.setQuantity(detailDto.getQuantity());
                        double updatedPrice = updatedDetail.getUnitPrice() * detailDto.getQuantity() * detailDto.getWeightPerUnit();
                        updatedDetail.setTotalPrice(updatedPrice);
                        updatedTotalAmount += updatedPrice;
                        if(detailDto.getQuantity() == 0){
                            updatedOrder.getOrderDetails().remove(updatedDetail);
                        }
                        break;
                    }
                }
                if(!found){
                    Product product = productRepository.findById(detailDto.getProductId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + detailDto.getProductId()));

                    if (detailDto.getQuantity() <= 0) {
                        throw new RuntimeException("Số lượng sản phẩm không được âm hoặc bằng 0 khi thêm mới: Product ID " + detailDto.getProductId());
                    }

                    OrderDetail newDetail = new OrderDetail();
                    newDetail.setOrder(updatedOrder);
                    newDetail.setProduct(product);
                    newDetail.setQuantity(detailDto.getQuantity());
                    newDetail.setUnitPrice(product.getPrice());
                    double totalPrice = product.getPrice() * detailDto.getQuantity() * detailDto.getWeightPerUnit();
                    newDetail.setTotalPrice(totalPrice);
                    updatedOrder.getOrderDetails().add(newDetail);
                    updatedTotalAmount += totalPrice;
                }
            }
        updatedOrder.setTotalAmount(updatedTotalAmount);
        if (newDeposit > updatedTotalAmount) {
            throw new RuntimeException("Số tiền cọc không được lớn hơn tổng giá trị đơn hàng!");
        }
        updatedOrder.setOrderPhone(adminOrderDto.getOrderPhone());
        updatedOrder.setOrderAddress(adminOrderDto.getOrderAddress());
        updatedOrder.setDeposit(newDeposit);
        updatedOrder.setRemainingAmount(updatedTotalAmount - newDeposit);
        try{
            orderRepository.save(updatedOrder);
            return updatedOrder;
        }catch (Exception e){
            throw new RuntimeException("Xảy ra lỗi trong quá trình cập nhật chi tiết đơn hàng:" + e.getMessage());
        }
    }

    @Override
    public DailyOrderResponseDTO getDailyReport(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<Order> orders = orderRepository.findAllByOrderDate(localDate);
        int numberOfOrders = orders.size();
        double totalPrice = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        DailyOrderResponseDTO response = new DailyOrderResponseDTO();
        response.setNumber(numberOfOrders);
        response.setTotalPrice(totalPrice);
        return response;
    }

    @Override
    public List<TopSaleProductDto> getTopSellingProducts(Date date, String type) {
        List<Object[]> results;
        Pageable pageable = PageRequest.of(0, 10);

        results = switch (type.toLowerCase()) {
            case "day" -> orderDetailRepository.findTopSellingProductsByDay(date, pageable);
            case "week" -> orderDetailRepository.findTopSellingProductsByWeek(date, pageable);
            case "month" -> orderDetailRepository.findTopSellingProductsByMonth(date, pageable);
            case "year" -> orderDetailRepository.findTopSellingProductsByYear(date, pageable);
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };

        List<TopSaleProductDto> topProducts = new ArrayList<>();
        for (Object[] result : results) {
            TopSaleProductDto dto = new TopSaleProductDto();
            dto.setProductName((String) result[0]);
            dto.setQuantitySold(((Number) result[1]).intValue());
            topProducts.add(dto);
        }
        return topProducts;
    }

    @Override
    public OrderWeightStatisticsView getOrderWeightStatistics(String timeFilter) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        DateTimeFormatter formatter = switch (timeFilter.toLowerCase()) {
            case "week" -> {
                startDate = now.minusDays(6);
                yield DateTimeFormatter.ofPattern("yyyy-MM-dd");
            }
            case "month" -> {
                startDate = now.minusDays(29);
                yield DateTimeFormatter.ofPattern("yyyy-MM-dd");
            }
            case "year" -> {
                startDate = now.minusMonths(11).withDayOfMonth(1);
                yield DateTimeFormatter.ofPattern("yyyy-MM");
            }
            default -> throw new IllegalArgumentException("Bộ lọc không hợp lệ: " + timeFilter);
        };

        List<StatusEnum> statuses = List.of(StatusEnum.COMPLETE, StatusEnum.IN_PROCESS);

        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderStatusAndDateBetween(
                statuses,
                java.sql.Date.valueOf(startDate.toLocalDate()),
                java.sql.Date.valueOf(now.toLocalDate().plusDays(1))
        );

        Map<String, Double> weightMap = orderDetails.stream()
                .collect(Collectors.groupingBy(
                        od -> od.getOrder().getOrderDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .format(formatter),
                        Collectors.summingDouble(od -> od.getQuantity() * od.getWeightPerUnit())
                ));

        List<OrderWeightStatisticsView.WeightDetail> details = weightMap.entrySet().stream()
                .map(entry -> OrderWeightStatisticsView.WeightDetail.builder()
                        .timePeriod(entry.getKey())
                        .weight(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(OrderWeightStatisticsView.WeightDetail::getTimePeriod))
                .collect(Collectors.toList());

        double totalWeight = details.stream()
                .mapToDouble(OrderWeightStatisticsView.WeightDetail::getWeight)
                .sum();

        return OrderWeightStatisticsView.builder()
                .totalWeight(totalWeight)
                .details(details)
                .build();
    }

    @Override
    public Order updateOrderByCustomer(long orderId, AdminOrderDto adminOrderDto) {
        Order updatedOrder = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("không tìm thấy đơn hàng!"));
        try{
            if(updatedOrder.getStatus() == null){
                throw new RuntimeException("Lỗi: trạng thái của đơn hàng không thể để trống!");
            }
            updatedOrder.setStatus(adminOrderDto.getStatus());
            if (adminOrderDto.getStatus() == StatusEnum.COMPLETE) {
                ReceiptVoucher receiptVoucher = new ReceiptVoucher();
                receiptVoucher.setOrder(updatedOrder);
                receiptVoucher.setTotalAmount(updatedOrder.getTotalAmount());
                receiptVoucher.setPaidAmount(0);
                receiptVoucher.setRemainAmount(updatedOrder.getTotalAmount());
                receiptVoucher.setReceiptDate(new Date());
                LocalDate currentDate = LocalDate.now();
                LocalDate dueDateLocal = currentDate.plusMonths(1);
                Date dueDate = Date.from(dueDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
                receiptVoucher.setDueDate(dueDate);
                receiptVoucher.setReceiptCode(RandomIncomeCodeGenerator.generateIncomeCode());
                receiptVoucherRepository.save(receiptVoucher);
            }
            orderRepository.save(updatedOrder);
            return updatedOrder;
        }catch (Exception e){
            throw new RuntimeException("Lỗi: xảy ra lỗi trong quá trình cập nhật đơn hàng !");
        }
    }

    @Override
    public List<InvoiceSummaryDto> getInvoiceSummary() {
        List<Object[]> results = orderRepository.findIncomeSummary();

        return results.stream()
                .map(row -> new InvoiceSummaryDto(
                        ((Number) row[0]).intValue(),  // month
                        ((Number) row[1]).longValue(),  // totalReceipt
                        ((Number) row[2]).doubleValue(), // totalPaid
                        ((Number) row[3]).doubleValue()  // totalRemain
                ))
                .toList();
    }

    private OrderDto convertToDTO(Order order) {
        OrderDto orderDTO = new OrderDto();
        orderDTO.setId(order.getId());
        orderDTO.setOrderCode(order.getOrderCode());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setOrderPhone(order.getOrderPhone());
        orderDTO.setOrderAddress(order.getOrderAddress());
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
        orderDTO.setReceiptVoucher(order.getReceiptVoucher());
        return orderDTO;
    }

    private ContractDto convertToContractDTO(Contract contract) {
        ContractDto contractDto = new ContractDto();
        contractDto.setContractNumber(contract.getContractNumber());
        contractDto.setAmount(contract.getAmount());
        contractDto.setCustomerName(contract.getCustomer().getName());
        contractDto.setPdfFilePath(contract.getPdfFilePath());
        contractDto.setImageFilePath(contract.getImageFilePath());
        contractDto.setConfirmationDate(contract.getConfirmationDate());
        return contractDto;
    }

    private double getCustomUnitPrice(Customer customer, Product product, double defaultPrice) {
        if (customer.getPrice() != null) {
            Optional<ProductPrice> productPriceOpt = productPriceRepository.findByPriceIdAndProductId(customer.getPrice().getId(), product.getId());
            if (productPriceOpt.isPresent()) {
                return productPriceOpt.get().getUnit_price();
            }
        }
        return defaultPrice;
    }

    private void logOrderActivity(Order order, String userPerform) {
        OrderActivity activity = OrderActivity.builder()
                .order(order)
                .activityType("CREATED")
                .description("Created Order")
                .timestamp(new Date())
                .userPerform(userPerform)
                .build();
        orderActivityRepository.save(activity);
    }
    private void validateProductQuantity(Order order){
        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for(OrderDetail orderDetail : orderDetails){
            long productId = orderDetail.getProduct().getId();
            String unit = orderDetail.getProductUnit();
            double weightPerUnit = orderDetail.getWeightPerUnit();
            int requiredQuantity = orderDetail.getQuantity();
            if(requiredQuantity <0){
                throw new RuntimeException("Số lượng sản phẩm phải là số dương");
            }
            List<ProductWarehouse> productWarehouses = productWareHouseRepository.findByProductIdAndUnitAndWeightPerUnit(
                    productId, unit, weightPerUnit
            );
            if(productWarehouses.isEmpty()){
                throw new RuntimeException("Không tìm thấy sản phẩm phù hợp trong kho!");
            }
            int availableQuantity = productWarehouses.stream().mapToInt(ProductWarehouse::getQuantity).sum();
            if(availableQuantity < requiredQuantity){
                throw new RuntimeException("Không đủ hàng có sẵn cho sản phẩm có id: " + productId);
            }
        }
    }

    private void processOrder(Order order){
        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            long productId = orderDetail.getProduct().getId();
            String unit = orderDetail.getProductUnit();
            double weightPerUnit = orderDetail.getWeightPerUnit();
            int requiredQuantity = orderDetail.getQuantity();
            if (requiredQuantity < 0) {
                throw new RuntimeException("Số lượng sản phẩm phải là số dương");
            }
            List<ProductWarehouse> warehouses = productWareHouseRepository.findByProductIdAndUnitAndWeightPerUnit(
                    productId, unit, weightPerUnit
            );
            if (warehouses.isEmpty()) {
                throw new RuntimeException("Không tìm thấy sản phẩm phù hợp trong kho!");
            }
            for (ProductWarehouse warehouse : warehouses) {
                int availableQuantity = warehouse.getQuantity();
                if (requiredQuantity <= 0) break;

                if (availableQuantity >= requiredQuantity) {
                    warehouse.setQuantity(availableQuantity - requiredQuantity);
                    productWareHouseRepository.save(warehouse);
                    requiredQuantity = 0;
                } else {
                    requiredQuantity -= availableQuantity;
                    warehouse.setQuantity(0);
                    productWareHouseRepository.save(warehouse);
                }
            }
        }

        WarehouseReceipt warehouseReceipt = new WarehouseReceipt();
        warehouseReceipt.setOrder(order);
        warehouseReceipt.setReceiptType(ReceiptType.EXPORT);
        warehouseReceipt.setReceiptDate(new Date());
        warehouseReceipt.setReceiptReason("Xuất kho để bán");
        warehouseReceiptRepository.save(warehouseReceipt);
        orderRepository.save(order);
    }
}
