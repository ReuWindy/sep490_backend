package com.fpt.sep490.repository;

import com.fpt.sep490.model.ReceiptVoucher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

public interface ReceiptVoucherRepository extends JpaRepository<ReceiptVoucher, Long> {
    Page<ReceiptVoucher> findAll(Specification<ReceiptVoucher> specification, Pageable pageable);
}
