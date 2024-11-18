package com.fpt.sep490.repository;

import com.fpt.sep490.model.FinishedProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinishedProductRepository extends JpaRepository<FinishedProduct, Long> {
    List<FinishedProduct> findByProductId(Long productId);

    Page<FinishedProduct> findAll(Specification<FinishedProduct> spec, Pageable pageable);
}
