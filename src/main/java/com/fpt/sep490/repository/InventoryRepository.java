package com.fpt.sep490.repository;

import com.fpt.sep490.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Page<Inventory> findAll(Specification<Inventory> specification, Pageable pageable);

}