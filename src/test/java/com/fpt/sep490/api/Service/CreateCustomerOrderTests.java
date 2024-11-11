package com.fpt.sep490.api.Service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.CustomerOrderDto;
import com.fpt.sep490.dto.DiscountDto;
import com.fpt.sep490.dto.OrderDetailDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ProductPriceRepository productPriceRepository;
    @Mock
    private OrderActivityRepository orderActivityRepository;
    @InjectMocks
    private OrderServiceImpl orderService;


    @Test
    public void OrderService_CreateCustomerOrder_CreateOrderWithDisCount() {
        Price price = new Price(1L,"Test Price",new HashSet<>(),new HashSet<>());
        Customer customer = new Customer("Test Customer", false, new HashSet<>(), price);
        customer.setId(1L);
        Product product = new Product(1L, "Sample Product","Sample Product Description", 120.0,"Sample_Product.png","PROD-1234-123456789",null,null,null,new Date(),new Date(),false,Set.of(), Set.of(),110.0);
        ProductPrice productPrice = new ProductPrice(1L,90.0 ,product,price);
        Set<ProductPrice> productPrices = new HashSet<>();
        Set<Customer> customers = new HashSet<>();
        customers.add(customer);
        productPrices.add(productPrice);
        price.setProductPrices(productPrices);
        price.setCustomers(customers);

        CustomerOrderDto customerOrderDto = new CustomerOrderDto(1L, createOrderDetails()); // Sample order DTO
        DiscountDto discountDto = new DiscountDto("10% for all products",5.0, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        OrderActivity orderActivity = new OrderActivity(1L,null,"CREATED","Created Order",new Date(),customer.getName());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(discountRepository.getByProductId(1L)).thenReturn(discountDto);

        Order createdOrder = orderService.createCustomerOrder(customerOrderDto);
        orderActivity.setOrder(createdOrder);

        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getOrderDetails().size());
        assertEquals(StatusEnum.PENDING, createdOrder.getStatus());
        assertTrue(createdOrder.getTotalAmount() > 0);

        verify(orderRepository).save(any(Order.class));
        verify(orderDetailRepository).saveAll(anySet());
        verify(orderActivityRepository).save(any(OrderActivity.class));
    }

    @Test
    public void OrderService_CreateCustomerOrder_CreateOrderWithoutDisCount() {
        Price price = new Price(1L,"Test Price",new HashSet<>(),new HashSet<>());
        Customer customer = new Customer("Test Customer", false, new HashSet<>(), price);
        customer.setId(1L);
        Product product = new Product(1L, "Sample Product","Sample Product Description", 120.0,"Sample_Product.png","PROD-1234-123456789",null,null,null,new Date(),new Date(),false,Set.of(), Set.of(),110.0);
        ProductPrice productPrice = new ProductPrice(1L,90.0 ,product,price);
        Set<ProductPrice> productPrices = new HashSet<>();
        Set<Customer> customers = new HashSet<>();
        customers.add(customer);
        productPrices.add(productPrice);
        price.setProductPrices(productPrices);
        price.setCustomers(customers);

        CustomerOrderDto customerOrderDto = new CustomerOrderDto(1L, createOrderDetails()); // Sample order DTO
        DiscountDto discountDto = new DiscountDto("10% for all products",5.0, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        OrderActivity orderActivity = new OrderActivity(1L,null,"CREATED","Created Order",new Date(),customer.getName());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(discountRepository.getByProductId(1L)).thenReturn(null);

        Order createdOrder = orderService.createCustomerOrder(customerOrderDto);
        orderActivity.setOrder(createdOrder);

        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getOrderDetails().size());
        assertEquals(StatusEnum.PENDING, createdOrder.getStatus());
        assertTrue(createdOrder.getTotalAmount() > 0);

        verify(orderRepository).save(any(Order.class));
        verify(orderDetailRepository).saveAll(anySet());
        verify(orderActivityRepository).save(any(OrderActivity.class));
    }


    private List<OrderDetailDto> createOrderDetails() {
        List<OrderDetailDto> orderDetails = new ArrayList<>();
        orderDetails.add(new OrderDetailDto(1L,"test product","test product description", 10, 100.0, 20.0, "BAO", 5.0, 0.0));
        return orderDetails;
    }
}
