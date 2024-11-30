package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.service.BatchProductServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddMoreBatchProductToBatchTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private BatchProductRepository batchProductRepository;

    @InjectMocks
    private BatchProductServiceImpl batchProductService;

    @Test
    public void testAddMoreBatchProductToBatch_WhenValidData_ReturnsBatchProduct() {
        // Arrange
        BatchProductDto batchProductDto = new BatchProductDto();
        batchProductDto.setProductId(1L);
        batchProductDto.setBatchId(1L);
        batchProductDto.setUnit("Kg");
        batchProductDto.setDescription("Test Product");
        batchProductDto.setQuantity(10);
        batchProductDto.setWeight(20.0);
        batchProductDto.setPrice(100.0);

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Product1");

        Batch mockBatch = new Batch();
        mockBatch.setId(1L);
        mockBatch.setBatchCode("Batch1");

        BatchProduct mockBatchProduct = new BatchProduct();
        mockBatchProduct.setProduct(mockProduct);
        mockBatchProduct.setBatch(mockBatch);
        mockBatchProduct.setUnit("Kg");
        mockBatchProduct.setDescription("Test Product");
        mockBatchProduct.setQuantity(10);
        mockBatchProduct.setWeight(20.0);
        mockBatchProduct.setPrice(100.0);

        // Mock các repository
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(batchRepository.findById(1L)).thenReturn(Optional.of(mockBatch));
        when(batchProductRepository.save(any(BatchProduct.class))).thenReturn(mockBatchProduct);

        // Act
        BatchProduct result = batchProductService.addMoreBatchProductToBatch(batchProductDto);

        // Assert
        assertNotNull(result);
        assertEquals("Product1", result.getProduct().getName());
        assertEquals("Batch1", result.getBatch().getBatchCode());
        assertEquals(10, result.getQuantity());
        assertEquals(20.0, result.getWeight());
        assertEquals(100.0, result.getPrice());

        // Verify repository interactions
        verify(productRepository, times(1)).findById(1L);
        verify(batchRepository, times(1)).findById(1L);
        verify(batchProductRepository, times(1)).save(any(BatchProduct.class));
    }

    @Test
    public void testAddMoreBatchProductToBatch_WhenProductNotFound_ThrowsException() {
        // Arrange
        BatchProductDto batchProductDto = new BatchProductDto();
        batchProductDto.setProductId(1L);
        batchProductDto.setBatchId(1L);

        // Mock không tìm thấy Product
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            batchProductService.addMoreBatchProductToBatch(batchProductDto);
        });

        assertEquals("Lỗi: Không tìm thấy", exception.getMessage());

        // Verify repository interaction
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testAddMoreBatchProductToBatch_WhenBatchNotFound_ThrowsException() {
        // Arrange
        BatchProductDto batchProductDto = new BatchProductDto();
        batchProductDto.setProductId(1L);
        batchProductDto.setBatchId(1L);
        batchProductDto.setUnit("Kg");
        batchProductDto.setDescription("Test Product");
        batchProductDto.setQuantity(10);
        batchProductDto.setWeight(20.0);
        batchProductDto.setPrice(100.0);

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Product1");

        // Mock không tìm thấy Batch
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(batchRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            batchProductService.addMoreBatchProductToBatch(batchProductDto);
        });

        assertEquals("Lỗi: Không tìm thấy", exception.getMessage());

        // Verify repository interactions
        verify(productRepository, times(1)).findById(1L);
        verify(batchRepository, times(1)).findById(1L);
    }


}
