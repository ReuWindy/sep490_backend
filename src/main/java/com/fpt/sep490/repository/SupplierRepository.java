package com.fpt.sep490.repository;

import com.fpt.sep490.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {
    Optional<Supplier> findByName(String name);

    Page<Supplier> findAll(Specification<Supplier> specification, Pageable pageable);

    Optional<Supplier> findByEmail(String email);

    @Query("SELECT s.name, s.id FROM Supplier s")
    List<Object[]> getSupplierNameAndId();

    @Query("SELECT s.name FROM Supplier s")
    List<String> findAllSupplierNames();
}