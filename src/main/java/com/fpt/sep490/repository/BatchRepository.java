package com.fpt.sep490.repository;

import com.fpt.sep490.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    Batch findByBatchCode(String name);
}