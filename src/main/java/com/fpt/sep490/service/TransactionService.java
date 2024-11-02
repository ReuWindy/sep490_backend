package com.fpt.sep490.service;

import com.fpt.sep490.model.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction updateTransaction(Transaction transaction);

    Transaction getTransactionById(int id);

    List<Transaction> getAllTransaction();
}
