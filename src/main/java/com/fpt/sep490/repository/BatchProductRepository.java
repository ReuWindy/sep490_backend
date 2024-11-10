package com.fpt.sep490.repository;

import com.fpt.sep490.model.BatchProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BatchProductRepository extends JpaRepository<BatchProduct, Long> {

    @Query("SELECT bp FROM BatchProduct bp " +
            "JOIN FETCH bp.batch b " +
            "JOIN FETCH bp.product p " +
            "WHERE p.id = :id")
    List<BatchProduct> findByProductId(@Param("id") Long id);

    @Query("SELECT bp FROM BatchProduct bp " +
            "JOIN FETCH bp.batch b " +
            "JOIN FETCH bp.product p " +
            "WHERE b.batchCode = :batchCode")
    List<BatchProduct> findByBatchCode(@Param("batchCode") String batchCode);

    List<BatchProduct> findAllByBatchId(Long batchId);
}
