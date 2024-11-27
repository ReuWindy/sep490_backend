package com.fpt.sep490.api.Service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.AdminOrderDto;
import com.fpt.sep490.dto.OrderDetailDto;
import com.fpt.sep490.model.Order;
import com.fpt.sep490.model.OrderDetail;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.repository.OrderRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
import com.fpt.sep490.service.OrderServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateOrderByAdminTests {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductWareHouseRepository productWareHouseRepository;
    @Mock
    private AdminOrderDto adminOrderDto;
    @Mock
    private ProductWarehouse productWarehouse;
    @Mock
    private Product product;
    @Mock
    private Order order;
    @Mock
    private OrderDetail orderDetail;
    @Mock
    private Validator validator;
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private Set<OrderDetail> orderDetails;

    @BeforeEach
    void setUp() {
        // Mocking Order
        order = new Order();
        order.setId(1L);
        order.setStatus(StatusEnum.PENDING);

        // Mocking OrderDetail
        orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setProductUnit("unit");
        orderDetail.setWeightPerUnit(10.0);
        orderDetail.setQuantity(100);

        orderDetails = new HashSet<>();
        orderDetails.add(orderDetail);
        order.setOrderDetails(orderDetails);

        // Mocking Product
        product = new Product();
        product.setId(1L);
        product.setProductCode("product_code");

        // Mocking AdminOrderDto
        adminOrderDto = new AdminOrderDto();
        adminOrderDto.setStatus(StatusEnum.IN_PROCESS);

        // Mocking ProductWarehouse
        productWarehouse = new ProductWarehouse();
        productWarehouse.setQuantity(100);
        productWarehouse.setProduct(product);
        productWarehouse.setUnit("unit");
        productWarehouse.setWeightPerUnit(10.0);

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void OrderService_UpdateOrder_SuccessWithStatusInProcess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productWareHouseRepository.findByProductIdAndUnitAndWeightPerUnit(anyLong(), anyString(), anyDouble()))
                .thenReturn(List.of(productWarehouse));

        Order result = orderService.updateOrderByAdmin(1L, adminOrderDto);

        assertEquals(StatusEnum.IN_PROCESS, result.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    public void OrderService_UpdateOrder_FailWhenNotEnoughStock() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        productWarehouse.setQuantity(50);  // Mock insufficient stock

        when(productWareHouseRepository.findByProductIdAndUnitAndWeightPerUnit(anyLong(), anyString(), anyDouble()))
                .thenReturn(List.of(productWarehouse));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderByAdmin(1L, adminOrderDto);
        });

        assertEquals("Not enough stock available for product ID: " + order.getOrderDetails().iterator().next().getId(), exception.getMessage());
    }

    @Test
    public void OrderService_UpdateOrder_SuccessStatusChange() {
        adminOrderDto.setStatus(StatusEnum.CANCELED); // Same status as before
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.updateOrderByAdmin(1L, adminOrderDto);

        assertEquals(StatusEnum.CANCELED, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void OrderService_UpdateOrder_FailWhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderByAdmin(1L, adminOrderDto);
        });

        assertEquals("Order Not Found", exception.getMessage());
    }

    @Test
    public void OrderService_UpdateOrder_FailWhenStatusIsNull() {
        adminOrderDto.setStatus(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderByAdmin(1L, adminOrderDto);
        });

        assertEquals("Can not Update Order Status !", exception.getMessage());
    }

    @Test
    public void OrderService_UpdateOrder_FailWhenCanNotFindProductInWarehouse() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productWareHouseRepository.findByProductIdAndUnitAndWeightPerUnit(anyLong(), anyString(), anyDouble()))
                .thenReturn(Collections.emptyList()); // No product found in warehouse

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderByAdmin(1L, adminOrderDto);
        });

        assertEquals("Can not find suitable product !", exception.getMessage());
    }

    @Test
    public void OrderService_UpdateOrder_FailWhenRequiredQuantityIsNegative() {
        orderDetail.setQuantity(-10); // Negative quantity
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderByAdmin(1L, adminOrderDto);
        });

        assertEquals("Số lượng sản phẩm phải là số dương", exception.getMessage());
    }

    @Test
    public void OrderService_UpdateOrder_SuccessWithMultipleWarehouses() {
        // Simulate 2 warehouses with enough stock
        ProductWarehouse warehouse1 = new ProductWarehouse();
        warehouse1.setQuantity(80);
        warehouse1.setProduct(product);
        warehouse1.setUnit("unit");
        warehouse1.setWeightPerUnit(10.0);

        ProductWarehouse warehouse2 = new ProductWarehouse();
        warehouse2.setQuantity(90);
        warehouse2.setProduct(product);
        warehouse2.setUnit("unit");
        warehouse2.setWeightPerUnit(10.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productWareHouseRepository.findByProductIdAndUnitAndWeightPerUnit(anyLong(), anyString(), anyDouble()))
                .thenReturn(List.of(warehouse1, warehouse2));

        Order result = orderService.updateOrderByAdmin(1L, adminOrderDto);

        assertEquals(StatusEnum.IN_PROCESS, result.getStatus());
        verify(orderRepository).save(order);
    }

}
