package com.fpt.sep490.repository;

import com.fpt.sep490.model.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Long> {
    UnitOfMeasure findByUnitName(String name);
}