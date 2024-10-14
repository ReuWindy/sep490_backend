package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchProductDto;
import com.fpt.sep490.model.BatchProduct;

public interface BatchProductService {
    BatchProduct createBatchProduct(BatchProductDto batchProductDto, String batchId);
}
