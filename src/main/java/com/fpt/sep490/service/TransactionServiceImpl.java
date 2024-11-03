package com.fpt.sep490.service;

import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl (TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }
    @Override
    public Transaction updateTransaction(Transaction transaction) {
        return null;
    }

    @Override
    public Transaction getTransactionById(int id) {
        return null;
    }

    @Override
    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }
}
