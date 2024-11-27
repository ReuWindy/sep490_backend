package com.fpt.sep490.repository;

import com.fpt.sep490.dto.DiscountDto;
import com.fpt.sep490.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("SELECT d from Discount d WHERE d.isActive = true AND d.endDate < :dateParam")
    List<Discount> findActiveDiscountsWithEndDateBefore(@Param("dateParam") LocalDateTime dateParam);

    @Query("""
                select new com.fpt.sep490.dto.DiscountDto(
                          d.description,
                          d.amountPerUnit,
                          d.startDate,
                          d.endDate)
                from Discount d
                join d.supplierProduct sp
                join sp.product p
                where p.id = :productId
            """)
    DiscountDto getByProductId(@Param("productId") long productId);
}