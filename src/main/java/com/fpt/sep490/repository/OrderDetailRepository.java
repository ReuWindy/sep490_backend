package com.fpt.sep490.repository;

import com.fpt.sep490.model.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(long orderId);

    @Query("SELECT od.product.name AS productName, SUM(od.quantity) AS quantitySold " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE DATE(o.orderDate) = DATE(:date) " +
            "GROUP BY od.product.id " +
            "ORDER BY quantitySold DESC")
    List<Object[]> findTopSellingProductsByDay(Date date, Pageable pageable);

    // Thống kê theo tuần
    @Query("SELECT od.product.name AS productName, SUM(od.quantity) AS quantitySold " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE FUNCTION('YEARWEEK', o.orderDate, 1) = FUNCTION('YEARWEEK', :date, 1) " +
            "GROUP BY od.product.id " +
            "ORDER BY quantitySold DESC")
    List<Object[]> findTopSellingProductsByWeek(Date date, Pageable pageable);

    // Thống kê theo tháng
    @Query("SELECT od.product.name AS productName, SUM(od.quantity) AS quantitySold " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE MONTH(o.orderDate) = MONTH(:date) AND YEAR(o.orderDate) = YEAR(:date) " +
            "GROUP BY od.product.id " +
            "ORDER BY quantitySold DESC")
    List<Object[]> findTopSellingProductsByMonth(Date date, Pageable pageable);

    // Thống kê theo năm
    @Query("SELECT od.product.name AS productName, SUM(od.quantity) AS quantitySold " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE YEAR(o.orderDate) = YEAR(:date) " +
            "GROUP BY od.product.id " +
            "ORDER BY quantitySold DESC")
    List<Object[]> findTopSellingProductsByYear(Date date, Pageable pageable);
}
