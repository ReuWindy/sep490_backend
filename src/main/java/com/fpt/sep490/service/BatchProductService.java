package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.dto.DeleteBatchProductRequest;
import com.fpt.sep490.dto.UpdateBatchProductRequest;
import com.fpt.sep490.model.BatchProduct;

import java.util.List;

public interface BatchProductService {
    BatchProduct createBatchProduct(BatchProductDto batchProductDto, String batchId);

    List<BatchProduct> getBatchProductByBatchId(Long batchId);

    BatchProduct updateBatchProduct(UpdateBatchProductRequest request, Long batchProductId);

    List<BatchProduct> deleteBatchProducts(DeleteBatchProductRequest request);
}
