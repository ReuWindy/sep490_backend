package com.fpt.sep490.repository;

import com.fpt.sep490.model.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
}
