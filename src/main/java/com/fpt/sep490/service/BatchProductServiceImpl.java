package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatProductViewDto;
import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.dto.DeleteBatchProductRequest;
import com.fpt.sep490.dto.UpdateBatchProductRequest;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<BatchProductDto> getBatchProductByProductId(Long id) {
        List<BatchProduct> b = batchProductRepository.findByProductId(id);
        if (b.isEmpty()) {
            throw new RuntimeException("Chưa có lịch sử nhập xuất");
        }
        List<BatchProductDto> batchProductDtos = new ArrayList<>();
        for (BatchProduct batchProduct : b) {
            if (batchProduct.isAdded()) {
                BatchProductDto batchProductDto = new BatchProductDto();
                batchProductDto.setProductId(batchProduct.getProduct().getId());
                batchProductDto.setBatchId(batchProduct.getBatch().getId());
                batchProductDto.setPrice(batchProduct.getPrice());
                batchProductDto.setWeightPerUnit(batchProduct.getWeightPerUnit());
                batchProductDto.setIsAdded(batchProduct.isAdded());
                batchProductDto.setUnit(batchProduct.getUnit());
                batchProductDto.setDescription(batchProduct.getDescription());
                batchProductDto.setQuantity(batchProduct.getQuantity());
                batchProductDto.setWeight(batchProduct.getWeight());
                batchProductDto.setBatchCode(batchProduct.getBatch().getBatchCode());
                batchProductDto.setReceiptType(String.valueOf(batchProduct.getBatch().getReceiptType()));
                batchProductDtos.add(batchProductDto);
            }
        }
        return batchProductDtos;
    }

    @Override
    public List<BatProductViewDto> getBatchProductByBatchCode(String batchCode) {
        List<BatchProduct> batchProducts = batchProductRepository.findByBatchCode(batchCode);
        return Optional.ofNullable(batchProducts)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private BatProductViewDto toDTO(BatchProduct batchProduct) {
        BatProductViewDto dto = new BatProductViewDto();
        dto.setAdded(batchProduct.isAdded());
        dto.setProductCode(batchProduct.getProduct().getProductCode());
        dto.setProductName(batchProduct.getProduct().getName());
        dto.setProductId(batchProduct.getProduct().getId());
        dto.setPrice(String.valueOf(batchProduct.getPrice()));
        dto.setUnit(batchProduct.getUnit());
        dto.setWeightPerUnit(batchProduct.getWeightPerUnit());
        dto.setQuantity(batchProduct.getQuantity());
        dto.setDescription(batchProduct.getDescription());
        dto.setCategoryId(Math.toIntExact(batchProduct.getProduct().getCategory().getId()));
        dto.setSupplierId(batchProduct.getProduct().getSupplier().getId());
        dto.setWarehouseId(batchProduct.getWarehouseId());
        return dto;
    }

    public List<BatchProduct> getBatchProductByBatchId(Long batchId) {
        return batchProductRepository.findAllByBatchId(batchId);
    }

    public List<BatchProduct> getBatchProductByBatchCodeV2(String batchCode) {
        if (batchCode == null || batchCode.isBlank()) {
            throw new IllegalArgumentException("Batch code cannot be null or empty");
        }

        List<BatchProduct> batchProducts = batchProductRepository.findByBatchCode(batchCode);
        if (batchProducts.isEmpty()) {
            throw new EntityNotFoundException("No BatchProduct found for batchCode: " + batchCode);
        }

        return batchProducts;
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
