package com.fpt.sep490.repository;

import com.fpt.sep490.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    List<Order> findByCustomerId(long customerId);

    @Query("SELECT o FROM Order o WHERE DATE(o.orderDate) = :date")
    List<Order> findAllByOrderDate(@Param("date") LocalDate date);
}