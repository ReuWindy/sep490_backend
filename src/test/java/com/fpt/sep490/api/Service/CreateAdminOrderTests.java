//package com.fpt.sep490.api.Service;
//
//
//import com.fpt.sep490.Enum.StatusEnum;
//import com.fpt.sep490.dto.AdminOrderDto;
//import com.fpt.sep490.dto.CustomerOrderDto;
//import com.fpt.sep490.dto.DiscountDto;
//import com.fpt.sep490.dto.OrderDetailDto;
//import com.fpt.sep490.model.*;
//import com.fpt.sep490.repository.*;
//import com.fpt.sep490.service.OrderServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class CreateAdminOrderTests {
//
//    @Mock
//    private CustomerRepository customerRepository;
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock
//    private OrderDetailRepository orderDetailRepository;
//    @Mock
//    private ProductRepository productRepository;
//    @Mock
//    private DiscountRepository discountRepository;
//    @Mock
//    private ProductPriceRepository productPriceRepository;
//    @Mock
//    private OrderActivityRepository orderActivityRepository;
//    @Mock
//    private ProductWareHouseRepository productWareHouseRepository;
//    @Mock
//    private ReceiptVoucherRepository receiptVoucherRepository;
//    @Mock
//    private Customer customer;
//    @Mock
//    private Product product;
//    @Mock
//    private ProductPrice productPrice;
//    @Mock
//    private AdminOrderDto adminOrderDto;
//    @Mock
//    private DiscountDto discountDto;
//    @Mock
//    private ProductWarehouse productWarehouse;
//    @Mock
//    private OrderActivity orderActivity;
//    @InjectMocks
//    private OrderServiceImpl orderService;
//
//    @BeforeEach
//    public void setUp() {
//        // Mock Customer
//        Price price = new Price(1L, "Test Price", new HashSet<>(), new HashSet<>());
//        customer = new Customer("Test Customer", false, new HashSet<>(), price);
//        customer.setId(1L);
//
//        // Mock Product
//        product = new Product(1L, "Sample Product", "Sample Product Description", 120.0, "Sample_Product.png",
//                "PROD-1234-123456789", null, null, null, new Date(), new Date(), false, Set.of(), Set.of(), 110.0);
//
//        productWarehouse = new ProductWarehouse();
//        productWarehouse.setId(1L);
//        productWarehouse.setProduct(product);
//        productWarehouse.setWeightPerUnit(20.0);
//        productWarehouse.setUnit("BAO");
//        productWarehouse.setQuantity(100);
//
//
//        // Mock ProductPrice
//        productPrice = new ProductPrice(1L, 90.0, product, price);
//
//        // Set relationships
//        Set<ProductPrice> productPrices = new HashSet<>();
//        productPrices.add(productPrice);
//        price.setProductPrices(productPrices);
//
//        Set<Customer> customers = new HashSet<>();
//        customers.add(customer);
//        price.setCustomers(customers);
//
//        // Mock AdminOrderDto
//        adminOrderDto = new AdminOrderDto(1L, StatusEnum.PENDING, 0.0, 100.0, 200.0, createOrderDetails());
//
//        // Mock DiscountDto
//        discountDto = new DiscountDto("10% for all products", 5.0, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
//
//        // Mock OrderActivity
//        orderActivity = new OrderActivity(1L, null, "CREATED", "Created Order", new Date(), customer.getName());
//
//    }
//
//
//    @Test
//    public void OrderService_CreateAdminOrder_CreateOrderSuccess(){
//
//        // Mock the repository calls
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(productWareHouseRepository.findByProductIdAndWeightPerUnitAndUnit(
//                eq(product.getId()),
//                eq(20.0),
//                eq("BAO"))).thenReturn(Optional.of(productWarehouse));
//
//        // Act : Call the service method under test
//        Order createdOrder = orderService.createAdminOrder(adminOrderDto);
//        //Set the orderActivity object
//        orderActivity.setOrder(createdOrder);
//
//        // Assert : Verified the result
//        assertNotNull(createdOrder);
//        assertEquals(100.0, createdOrder.getDeposit());
//        assertEquals(200.0, createdOrder.getRemainingAmount());
//        assertEquals(StatusEnum.PENDING, createdOrder.getStatus());
//
//        // Verify interactions with repositories
//        verify(orderRepository).save(any(Order.class));
//        verify(orderDetailRepository).saveAll(anySet());
//
//    }
//
//    @Test
//    public void OrderService_CreateAdminOrder_CustomerNotFound(){
//        // Arrange : Set up date
//        AdminOrderDto adminOrderDto = new AdminOrderDto();
//        adminOrderDto.setCustomerId(999L);
//
//        // Mock the repository call
//        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // Act and Assert: Throw exception when trying to create an order with a non-existent customer
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            orderService.createAdminOrder(adminOrderDto);
//        });
//
//        // Assert: Check if the exception message matches the expected message
//        assertEquals("Không tìm thấy khách hàng!", exception.getMessage());
//    }
//
//    @Test
//    public void OrderService_CreateAdminOrder_ProductNotFound(){
//        // Arrange: Đảm bảo khách hàng tồn tại
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            orderService.createAdminOrder(adminOrderDto);
//        });
//
//        // Assert
//        assertEquals("Không tìm thấy sản phẩm tương ứng trong kho!", exception.getMessage());
//    }
//
//    @Test
//    public void OrderService_createAdminOrder_OrderCreateWithDiscount() {
//        // Arrange
//        adminOrderDto.getOrderDetails().get(0).setDiscount(10.0); // Set 10% discount
//        double expectedDiscountedUnitPrice = 90.0; // 100 - 10
//
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(productWareHouseRepository.findByProductIdAndWeightPerUnitAndUnit(
//                eq(product.getId()),
//                eq(20.0),
//                eq("BAO"))).thenReturn(Optional.of(productWarehouse));
//
//        // Act
//        Order createdOrder = orderService.createAdminOrder(adminOrderDto);
//
//        // Assert
//        OrderDetail orderDetail = createdOrder.getOrderDetails().iterator().next();
//        assertEquals(expectedDiscountedUnitPrice, orderDetail.getUnitPrice());
//        assertEquals(expectedDiscountedUnitPrice * orderDetail.getQuantity() * orderDetail.getWeightPerUnit(),
//                orderDetail.getTotalPrice());
//    }
//
//    @Test
//    public void OrderService_createAdminOrder_OrderCreateWithNoDiscount() {
//        // Arrange
//        adminOrderDto.getOrderDetails().get(0).setDiscount(0.0); // No discount
//        double expectedUnitPrice = 100.0; // Original price
//
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(productWareHouseRepository.findByProductIdAndWeightPerUnitAndUnit(
//                eq(product.getId()),
//                eq(20.0),
//                eq("BAO"))).thenReturn(Optional.of(productWarehouse));
//
//        // Act
//        Order createdOrder = orderService.createAdminOrder(adminOrderDto);
//
//        // Assert
//        OrderDetail orderDetail = createdOrder.getOrderDetails().iterator().next();
//        assertEquals(expectedUnitPrice, orderDetail.getUnitPrice());
//        assertEquals(expectedUnitPrice * orderDetail.getQuantity() * orderDetail.getWeightPerUnit(),
//                orderDetail.getTotalPrice());
//    }
//
//    private List<OrderDetailDto> createOrderDetails() {
//        List<OrderDetailDto> orderDetails = new ArrayList<>();
//        orderDetails.add(new OrderDetailDto(1L,"test product","test product description", 10, 100.0, 20.0, "BAO", 5.0, 0.0));
//        return orderDetails;
//    }
//}
