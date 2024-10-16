package com.fpt.sep490.repository;

import com.fpt.sep490.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    Optional<Product> findByProductCode(String code);
    Page<Product> findAll(Specification<Product> specification, Pageable pageable);
}
