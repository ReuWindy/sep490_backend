package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.dto.DeleteBatchProductRequest;
import com.fpt.sep490.dto.UpdateBatchProductRequest;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public BatchProduct createBatchProduct(BatchProductDto batchProductDto, String batchId) {
        return null;
    }

    @Override
    public List<BatchProduct> getBatchProductByProductId(Long id) {
        Optional<List<BatchProduct>> b = Optional.ofNullable(batchProductRepository.findByProductId(id));
        return b.orElse(null);
    }

    @Override
    public List<BatchProduct> getBatchProductByBatchCode(String batchCode) {
        Optional<List<BatchProduct>> b = Optional.ofNullable(batchProductRepository.findByBatchCode(batchCode));
        return b.orElse(null);
    }

    public List<BatchProduct> getBatchProductByBatchId(Long batchId) {
        return batchProductRepository.findAllByBatchId(batchId);
    }

    @Override
    public BatchProduct updateBatchProduct(UpdateBatchProductRequest request, Long batchProductId) {
        BatchProduct batchProduct = batchProductRepository.findById(batchProductId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy"));
        batchProduct.setDescription(request.getDescription());
        batchProduct.setWeightPerUnit(request.getWeightPerUnit());
        batchProduct.setPrice(request.getPrice());
        batchProduct.setQuantity(request.getQuantity());
        batchProduct.setUnit(request.getUnit());
        batchProductRepository.save(batchProduct);
        return batchProduct;
    }


    @Override
    public BatchProduct addMoreBatchProductToBatch(BatchProductDto batchProductDto) {
        BatchProduct batchProduct = new BatchProduct();
        batchProduct.setProduct(productRepository.findById(batchProductDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy")));
        batchProduct.setUnit(batchProductDto.getUnit());
        batchProduct.setDescription(batchProductDto.getDescription());
        batchProduct.setQuantity(batchProductDto.getQuantity());
        batchProduct.setWeight(batchProductDto.getWeight());
        batchProduct.setPrice(batchProductDto.getPrice());

        Batch batch = batchRepository.findById(batchProductDto.getBatchId()).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy"));
        batchProduct.setBatch(batch);
        return batchProductRepository.save(batchProduct);
    }

    @Override
    public List<BatchProduct> deleteBatchProducts(DeleteBatchProductRequest request) {
        List<BatchProduct> batchProducts = new ArrayList<>();

        for (Long id : request.getBatProductId()) {
            BatchProduct batchProduct = batchProductRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy"));
            batchProductRepository.delete(batchProduct);
            batchProducts.add(batchProduct);
        }
        return batchProducts;
    }
}
