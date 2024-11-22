package com.fpt.sep490.repository;

import com.fpt.sep490.model.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    @EntityGraph(attributePaths = {"finishedProducts"})
    Page<ProductionOrder> findAll(Specification<ProductionOrder> spec, Pageable pageable);

    @Query("SELECT DISTINCT po FROM ProductionOrder po LEFT JOIN FETCH po.finishedProducts")
    Page<ProductionOrder> findAllWithFinishedProducts(Specification<ProductionOrder> spec, Pageable pageable);
}
