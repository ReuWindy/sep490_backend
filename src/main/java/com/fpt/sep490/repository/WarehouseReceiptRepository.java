package com.fpt.sep490.repository;

import com.fpt.sep490.model.WarehouseReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseReceiptRepository extends JpaRepository<WarehouseReceipt, Long> {
    @EntityGraph(attributePaths = {"batch"})
    Page<WarehouseReceipt> findAll(Specification<WarehouseReceipt> specification, Pageable pageable);
}
