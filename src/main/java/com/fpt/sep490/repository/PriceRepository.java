package com.fpt.sep490.repository;

import com.fpt.sep490.model.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Page<Price> findAll(Specification<Price> specification, Pageable pageable);

    Price findByName(String name);
}