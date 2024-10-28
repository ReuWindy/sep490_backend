package com.fpt.sep490.repository;

import com.fpt.sep490.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    Optional<ProductPrice> findByPriceIdAndProductId(long priceId, long productId);
}
