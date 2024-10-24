package com.fpt.sep490.repository;

import com.fpt.sep490.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
}
