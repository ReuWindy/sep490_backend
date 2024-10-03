package com.fpt.sep490.repository;

import com.fpt.sep490.model.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {
    List<SupplierProduct> findBySupplierId(Long supplierId);
    List<SupplierProduct> findByPreviousSupplierId(Long previousSupplierId);
}
