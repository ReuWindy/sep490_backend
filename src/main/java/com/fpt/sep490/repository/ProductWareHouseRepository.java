package com.fpt.sep490.repository;

import com.fpt.sep490.model.Product;
import com.fpt.sep490.model.ProductWarehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductWareHouseRepository extends JpaRepository<ProductWarehouse, Long> {
    List<ProductWarehouse> findByProductId(long productId);
    @Query("SELECT pw.product FROM ProductWarehouse pw WHERE pw.warehouse.id = :warehouseId")
    List<Product> findProductsByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT pw FROM ProductWarehouse pw WHERE pw.warehouse.id = :warehouseId")
    List<ProductWarehouse> findProductWarehousesByWarehouseId(@Param("warehouseId") Long warehouseId);

    Page<ProductWarehouse> findAll(Specification<ProductWarehouse> specification,Pageable pageable);
    Optional<ProductWarehouse> findByProductAndUnitAndWeightPerUnitAndWarehouseId(Product product, String unit, double weightPerUnit, Long warehouseId);
    Optional<ProductWarehouse> findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(String productName, String unit, double weightPerUnit, Long warehouseId);

    Optional<ProductWarehouse> findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(long productId, long warehouseId, String unit, double weightPerUnit);
}
