package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchDto;
import com.fpt.sep490.model.Batch;

import java.util.Date;
import java.util.List;

public interface BatchService {
    List<Batch> getAllBatches();
    Batch getBatchById(int id);
    Batch createBatch(BatchDto batchDto);
    Batch updateBatch(Long batchId, BatchDto batchDto);
    Batch getBatchByBatchCode(String code);
    Batch getBatchBySupplierName(String supplierName);
    void deleteBatch(Long batchId);
    void deleteBatchWithProduct(Long batchId);
}


