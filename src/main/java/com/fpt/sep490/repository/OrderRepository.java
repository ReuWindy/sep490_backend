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

    @Query(value = """
            SELECT COUNT(o.id) > 0
            FROM orders o
            WHERE o.status IN ('IN_PROCESS', 'CONFIRMED', 'PENDING')
            """, nativeQuery = true)
    boolean existsOrderWithStatus();

    @Query(value = """
            SELECT 
                MONTH(o.order_date) AS month,
                COUNT(DISTINCT o.id) AS total_receipt,
                SUM(rv.total_paid_from_receipt) AS total_paid,
                SUM(rv.total_unpaid) AS total_remain
            FROM orders o
            JOIN (
                SELECT 
                    rv.order_id, 
                    SUM(rv.paid_amount) AS total_paid_from_receipt, 
                    SUM(rv.remain_amount) AS total_unpaid
                FROM receipt_voucher rv
                GROUP BY rv.order_id
            ) rv ON o.id = rv.order_id
            WHERE o.status IN ('COMPLETED', 'COMPLETE')
              AND o.order_date >= DATE_ADD(CURDATE(), INTERVAL -5 MONTH)
            GROUP BY MONTH(o.order_date)
            ORDER BY MONTH(o.order_date)
            """, nativeQuery = true)
    List<Object[]> findIncomeSummary();

    @Query(value = """
            SELECT
                COUNT(o.id) AS order_count,
                SUM(o.remaining_amount) AS total_remaining_amount,
                (SELECT o2.id 
                 FROM orders o2 
                 WHERE o2.customer_id = :customerId 
                 ORDER BY o2.order_date DESC 
                 LIMIT 1) AS latest_order_id,
                (SELECT o2.order_date 
                 FROM orders o2 
                 WHERE o2.customer_id = :customerId 
                 ORDER BY o2.order_date DESC 
                 LIMIT 1) AS latest_order_date
            FROM orders o
            WHERE o.customer_id = :customerId
            """, nativeQuery = true)
    List<Object[]> getOrderSummaryByCustomerId(@Param("customerId") long customerId);
}