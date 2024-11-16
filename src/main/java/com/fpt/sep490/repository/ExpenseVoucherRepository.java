package com.fpt.sep490.repository;

import com.fpt.sep490.model.ExpenseVoucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ExpenseVoucherRepository extends JpaRepository<ExpenseVoucher, Long> {
    Page<ExpenseVoucher> findAll(Specification<ExpenseVoucher> specification, Pageable pageable);

    @Query("SELECT DAY(ev.expenseDate) AS day, SUM(ev.totalAmount) AS totalAmount " +
            "FROM ExpenseVoucher ev " +
            "WHERE MONTH(ev.expenseDate) = MONTH(:date) AND YEAR(ev.expenseDate) = YEAR(:date) " +
            "GROUP BY DAY(ev.expenseDate) " +
            "ORDER BY DAY(ev.expenseDate)")
    List<Object[]> findDailyExpenseByMonth(Date date);

    @Query("SELECT FUNCTION('WEEK', ev.expenseDate) AS week, SUM(ev.totalAmount) AS totalAmount " +
            "FROM ExpenseVoucher ev " +
            "WHERE YEAR(ev.expenseDate) = YEAR(:date) " +
            "GROUP BY FUNCTION('WEEK', ev.expenseDate) " +
            "ORDER BY FUNCTION('WEEK', ev.expenseDate)")
    List<Object[]> findWeeklyExpenseByYear(Date date);

    @Query("SELECT MONTH(ev.expenseDate) AS month, SUM(ev.totalAmount) AS totalAmount " +
            "FROM ExpenseVoucher ev " +
            "WHERE YEAR(ev.expenseDate) = YEAR(:date) " +
            "GROUP BY MONTH(ev.expenseDate) " +
            "ORDER BY MONTH(ev.expenseDate)")
    List<Object[]> findMonthlyExpenseByYear(Date date);
}
