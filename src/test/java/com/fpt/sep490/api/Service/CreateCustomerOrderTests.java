package com.fpt.sep490.api.Service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.CustomerOrderDto;
import com.fpt.sep490.dto.DiscountDto;
import com.fpt.sep490.dto.OrderDetailDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.service.OrderServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateCustomerOrderTests {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private OrderActivityRepository orderActivityRepository;
    @Mock
    private ProductPriceRepository productPriceRepository;
    @Mock
    private Price price;
    @Mock
    private Product product;
    @Mock
    private ProductPrice productPrice;
    @Mock
    private Customer customer;
    @Mock
    private DiscountDto discountDto;
    @Mock
    private CustomerOrderDto customerOrderDto;
    @Mock
    private Validator validator;
    @Mock
    private OrderActivity orderActivity;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    public void setUp() {
        // Mock dữ liệu cho Price, Customer và Product
         customerOrderDto = new CustomerOrderDto(1L, createOrderDetails());
        price = new Price(1L, "Standard Price", new HashSet<>(), new HashSet<>());
        product = new Product(
                1L, "Sample Product", "Product Description",
                120.0, "Sample_Image.png", "PROD-001", null, null, null,
                new Date(), new Date(), false, Set.of(), Set.of(), 110.0
        );

        productPrice = new ProductPrice(1L, 100.0, product, price);
        customer = new Customer("Test Customer", false, new HashSet<>(), price);
        discountDto = new DiscountDto("Summer Sale", 5.0, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5));

        price.getProductPrices().add(productPrice);
        price.getCustomers().add(customer);

         orderActivity = new OrderActivity(1L,null,"CREATED","Created Order",new Date(),customer.getName());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }


    @Test
    public void OrderService_CreateCustomerOrder_CreateOrderWithDisCount() {

        // Mock the repository calls
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(discountRepository.getByProductId(1L)).thenReturn(discountDto);

        // Act: call the service method under test
        Order createdOrder = orderService.createCustomerOrder(customerOrderDto);
        orderActivity.setOrder(createdOrder);

        // Assert : Verify the result
        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getOrderDetails().size());
        assertEquals(StatusEnum.PENDING, createdOrder.getStatus());
        assertTrue(createdOrder.getTotalAmount() > 0);

        // Verify interactions with repositories
        verify(orderRepository).save(any(Order.class));
        verify(orderDetailRepository).saveAll(anySet());
        verify(orderActivityRepository).save(any(OrderActivity.class));
    }

    @Test
    public void OrderService_CreateCustomerOrder_CreateOrderWithoutDisCount() {

        // Mock the repository calls
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(discountRepository.getByProductId(1L)).thenReturn(null);

        // Act: call the service method under test
        Order createdOrder = orderService.createCustomerOrder(customerOrderDto);
        orderActivity.setOrder(createdOrder);

        // Assert : Verify the result
        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getOrderDetails().size());
        assertEquals(StatusEnum.PENDING, createdOrder.getStatus());
        assertTrue(createdOrder.getTotalAmount() > 0);

        // Verify interactions with repositories
        verify(orderRepository).save(any(Order.class));
        verify(orderDetailRepository).saveAll(anySet());
        verify(orderActivityRepository).save(any(OrderActivity.class));
    }

    @Test
    public void createCustomerOrder_ProductNotFound_ShouldThrowException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createCustomerOrder(customerOrderDto));
        assertEquals("Không tìm thấy sản phẩm!", exception.getMessage());
    }

    @Test
    public void createCustomerOrder_CustomerNotFound_ShouldThrowException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createCustomerOrder(customerOrderDto));
        assertEquals("Không tìm thấy khách hàng!", exception.getMessage());
    }

    @Test
    public void createCustomerOrder_OrderDetailsDtoQuantityNegative(){
        customerOrderDto.getOrderDetails().get(0).setQuantity(-1);

        Set<ConstraintViolation<OrderDetailDto>> violations = validator.validate(customerOrderDto.getOrderDetails().get(0));

        assertEquals(1, violations.size());
        assertEquals("Số lượng phải là số dương.", violations.iterator().next().getMessage());
    }

    @Test
    public void createCustomerOrder_OrderDetailsDtoUnitPriceNegative(){
        customerOrderDto.getOrderDetails().get(0).setUnitPrice(-2);

        Set<ConstraintViolation<OrderDetailDto>> violations = validator.validate(customerOrderDto.getOrderDetails().get(0));

        assertEquals(1, violations.size());
        assertEquals("Giá nhập phải là số dương.", violations.iterator().next().getMessage());
    }

    private List<OrderDetailDto> createOrderDetails() {
        List<OrderDetailDto> orderDetails = new ArrayList<>();
        orderDetails.add(new OrderDetailDto(1L,"test product","test product description", 10, 100.0, 20.0, "BAO", 5.0, 0.0));
        return orderDetails;
    }
}
