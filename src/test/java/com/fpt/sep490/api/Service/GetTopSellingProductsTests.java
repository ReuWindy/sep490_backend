package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.TopSaleProductDto;
import com.fpt.sep490.repository.OrderDetailRepository;
import com.fpt.sep490.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetTopSellingProductsTests {
    @Mock
    private OrderDetailRepository orderDetailRepository;
    private Date date;
    private Pageable pageable;
    private List<Object[]> mockResults;
    @InjectMocks
    private OrderServiceImpl productService;

    @BeforeEach
    void setUp() {
        mockResults = new ArrayList<>();
        mockResults.add(new Object[]{"Product1", 50});
        mockResults.add(new Object[]{"Product2", 30});
        mockResults.add(new Object[]{"Product3", 100});
        mockResults.add(new Object[]{"Product4", 80});
        date = new Date();  // Set a mock date (can be changed for specific test scenarios)
        pageable = PageRequest.of(0, 10);  // Default pagination of 10 items
    }

    @Test
    public void ProductService_GetTopSellingProducts_GetTopSellingProductsByDay() {

        when(orderDetailRepository.findTopSellingProductsByDay(any(), eq(pageable))).thenReturn(mockResults);

        List<TopSaleProductDto> topSellingProducts = productService.getTopSellingProducts(date, "day");

        assertEquals(4, topSellingProducts.size());
        assertEquals("Product1", topSellingProducts.get(0).getProductName());
        assertEquals(50, topSellingProducts.get(0).getQuantitySold());
    }

    @Test
    public void ProductService_GetTopSellingProducts_GetTopSellingProductsByWeek() {

        when(orderDetailRepository.findTopSellingProductsByWeek(any(), eq(pageable))).thenReturn(mockResults);

        List<TopSaleProductDto> topSellingProducts = productService.getTopSellingProducts(date, "week");

        assertEquals(4, topSellingProducts.size());
        assertEquals("Product1", topSellingProducts.get(0).getProductName());
        assertEquals(50, topSellingProducts.get(0).getQuantitySold());
    }

    @Test
    public void ProductService_GetTopSellingProducts_GetTopSellingProductsByMonth() {

        when(orderDetailRepository.findTopSellingProductsByMonth(any(), eq(pageable))).thenReturn(mockResults);

        List<TopSaleProductDto> topSellingProducts = productService.getTopSellingProducts(date, "month");

        assertEquals(4, topSellingProducts.size());
        assertEquals("Product1", topSellingProducts.get(0).getProductName());
        assertEquals(50, topSellingProducts.get(0).getQuantitySold());
    }

    @Test
    public void ProductService_GetTopSellingProducts_GetTopSellingProductsByYear() {

        when(orderDetailRepository.findTopSellingProductsByYear(any(), eq(pageable))).thenReturn(mockResults);

        List<TopSaleProductDto> topSellingProducts = productService.getTopSellingProducts(date, "year");

        assertEquals(4, topSellingProducts.size());
        assertEquals("Product1", topSellingProducts.get(0).getProductName());
        assertEquals(50, topSellingProducts.get(0).getQuantitySold());
    }

    @Test
    public void ProductService_GetTopSellingProducts_InvalidType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.getTopSellingProducts(date, "invalidType");
        });

        assertEquals("Invalid type: invalidType", exception.getMessage());
    }

    @Test
    public void ProductService_GetTopSellingProducts_EmptyList() {
        List<Object[]> mockResults = new ArrayList<>();

        when(orderDetailRepository.findTopSellingProductsByDay(any(), eq(pageable))).thenReturn(mockResults);

        List<TopSaleProductDto> topSellingProducts = productService.getTopSellingProducts(date, "day");

        assertTrue(topSellingProducts.isEmpty());
    }

    @Test
    public void ProductService_GetTopSellingProducts_NullDate() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{"Product6", 150});

        when(orderDetailRepository.findTopSellingProductsByDay(any(), eq(pageable))).thenReturn(mockResults);

        List<TopSaleProductDto> topSellingProducts = productService.getTopSellingProducts(null, "day");

        assertEquals(1, topSellingProducts.size());
        assertEquals("Product6", topSellingProducts.get(0).getProductName());
    }

}
