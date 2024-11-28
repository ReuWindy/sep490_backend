package com.fpt.sep490.repository;

import com.fpt.sep490.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    Optional<ProductPrice> findByPriceIdAndProductId(Long priceId, Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ProductPrice pp WHERE pp.price.id = :priceId")
    void deleteByPriceId(@Param("priceId") Long priceId);
}
