package com.fpt.sep490.repository;

import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findAll(Specification<Supplier> specification, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Customer c SET c.price.id = :defaultPriceId WHERE c.price.id = :priceId")
    void updatePriceIdForCustomers(@Param("priceId") Long priceId, @Param("defaultPriceId") Long defaultPriceId);
}