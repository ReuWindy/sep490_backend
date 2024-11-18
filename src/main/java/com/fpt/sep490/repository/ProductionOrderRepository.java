package com.fpt.sep490.repository;

import com.fpt.sep490.model.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    Page<ProductionOrder> findAll(Specification<ProductionOrder> spec, Pageable pageable);
}
