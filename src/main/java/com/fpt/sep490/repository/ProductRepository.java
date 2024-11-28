package com.fpt.sep490.repository;

import com.fpt.sep490.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    Optional<Product> findByProductCode(String code);

    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    Product findByName(String name);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.batchProducts bp " +
            "JOIN FETCH bp.batch b " +
            "WHERE b.batchCode = :batchCode")
    List<Product> findByBatchCode(@Param("batchCode") String batchCode);


    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.id != :id AND p.name = :name AND p.category.id = :categoryId AND p.supplier.id = :supplierId")
    boolean existsByNameAndCategoryIdAndSupplierId(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId);

    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.category.id = :categoryId AND p.supplier.id = :supplierId")
    Optional<Product> findByNameAndCategoryIdAndSupplierId(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId
    );
}