package com.fpt.sep490.repository;

import com.fpt.sep490.model.ExpenseVoucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseVoucherRepository extends JpaRepository<ExpenseVoucher, Long> {
    Page<ExpenseVoucher> findAll(Specification<ExpenseVoucher> specification, Pageable pageable);
}
