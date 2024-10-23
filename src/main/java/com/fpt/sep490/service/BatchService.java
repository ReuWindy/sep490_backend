package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchDto;
import com.fpt.sep490.model.Batch;

import java.util.Date;
import java.util.List;

public interface BatchService {
    List<Batch> getAllBatches();
    Batch getBatchById(int id);
    Batch updateBatch(Long batchId, BatchDto batchDto);
    Batch getBatchByBatchCode(String code);
    Batch updateBatchStatus(Long batchId, String status);
}


