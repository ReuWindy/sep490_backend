package com.fpt.sep490.repository;

import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch,Long> {
    Batch findByBatchCode(String name);
    Optional<Batch> findFirstBySupplier(Supplier supplier);
}
