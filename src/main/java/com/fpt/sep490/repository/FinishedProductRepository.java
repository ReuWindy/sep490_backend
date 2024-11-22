package com.fpt.sep490.repository;

import com.fpt.sep490.model.FinishedProduct;
import com.fpt.sep490.model.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface FinishedProductRepository extends JpaRepository<FinishedProduct, Long> {
    List<FinishedProduct> findByProductId(Long productId);

    Page<FinishedProduct> findAll(Specification<FinishedProduct> spec, Pageable pageable);

    @Query("SELECT DISTINCT po FROM ProductionOrder po LEFT JOIN FETCH po.finishedProducts")
    Page<ProductionOrder> findAllWithFinishedProducts(Specification<ProductionOrder> spec, Pageable pageable);

}
