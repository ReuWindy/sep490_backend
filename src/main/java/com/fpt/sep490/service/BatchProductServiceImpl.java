package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class BatchProductServiceImpl implements BatchProductService {
    private final BatchProductRepository batchProductRepository;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;

    public BatchProductServiceImpl(BatchProductRepository batchProductRepository, ProductRepository productRepository, BatchRepository batchRepository) {
        this.batchProductRepository = batchProductRepository;
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
    }

    @Override
    public BatchProduct createBatchProduct(BatchProductDto batchProductDto, String batchCode) {
        BatchProduct batchProduct = new BatchProduct();
        batchProduct.setProduct(productRepository.findById(batchProductDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found")));
        batchProduct.setUnit(batchProductDto.getUnit());
        batchProduct.setDescription(batchProductDto.getDescription());
        batchProduct.setQuantity(batchProductDto.getQuantity());
        batchProduct.setWeight(batchProductDto.getWeight());
        batchProduct.setPrice(batchProductDto.getPrice());

        Batch batch = batchRepository.findByBatchCode(batchCode);
        batchProduct.setBatch(batch);
        return batchProductRepository.save(batchProduct);
    }
}
