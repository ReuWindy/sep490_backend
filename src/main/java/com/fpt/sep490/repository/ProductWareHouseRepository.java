package com.fpt.sep490.repository;

import com.fpt.sep490.model.Product;
import com.fpt.sep490.model.ProductWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductWareHouseRepository extends JpaRepository<ProductWarehouse, Long> {
    List<ProductWarehouse> findByProductId(long productId);

    @Query("SELECT pw.product FROM ProductWarehouse pw WHERE pw.warehouse.id = :warehouseId")
    List<Product> findProductsByWarehouseId(@Param("warehouseId") Long warehouseId);

    Optional<ProductWarehouse> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
