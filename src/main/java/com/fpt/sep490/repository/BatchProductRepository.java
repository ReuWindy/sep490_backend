package com.fpt.sep490.repository;

import com.fpt.sep490.model.BatchProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchProductRepository extends JpaRepository<BatchProduct,Long> {
    List<BatchProduct> findByBatchId(Long batchId);
}
