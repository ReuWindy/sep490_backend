package com.fpt.sep490.service;

import com.fpt.sep490.dto.RevenueStatisticsView;
import com.fpt.sep490.dto.TransactionDto;
import com.fpt.sep490.model.Transaction;

import java.util.List;
import java.util.Set;

public interface TransactionService {
    Transaction updateTransaction(TransactionDto transactionDto);

    Set<TransactionDto> getTransactionByReceiptId(long receiptId);

    List<Transaction> getAllTransaction();

    Transaction createTransactionByAdmin(TransactionDto transactionDto);

    Transaction createTransactionByPayOS(TransactionDto transactionDto);

    RevenueStatisticsView getRevenueStatistics(String timeFilter);
}