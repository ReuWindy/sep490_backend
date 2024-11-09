package com.fpt.sep490.repository;

import com.fpt.sep490.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Set<Transaction> findByReceiptVoucher_Id(int receiptId);
}
