package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.TransactionDto;
import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl (TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }
    @Override
    public Transaction updateTransaction(TransactionDto transactionDto) {
        Transaction updatedTransaction = transactionRepository.findById(transactionDto.getId()).orElse(null);
        Date currentDate = new Date();
        long threeDaysAgoMillis = currentDate.getTime() - (3L * 24 * 60 * 60 * 1000);
        if((transactionDto.getTransactionDate().getTime() > threeDaysAgoMillis)) {
            if(updatedTransaction != null) {
                updatedTransaction.setAmount(transactionDto.getAmount());
                updatedTransaction.setTransactionDate(transactionDto.getTransactionDate());
                updatedTransaction.setPaymentMethod(transactionDto.getPaymentMethod());
                transactionRepository.save(updatedTransaction);
                return updatedTransaction;
            }
        }
        return null;
    }

    @Override
    public Set<TransactionDto> getTransactionByReceiptId(int receiptId) {
        Set<Transaction> transactions = transactionRepository.findByReceiptVoucher_Id(receiptId);
        return transactions.stream().map(this::convertToDto).collect(Collectors.toSet());
    }

    @Override
    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction createTransactionByAdmin(TransactionDto transactionDto) {
        Transaction createdTransaction = new Transaction();
        createdTransaction.setAmount(transactionDto.getAmount());
        createdTransaction.setTransactionDate(new Date());
        createdTransaction.setPaymentMethod(transactionDto.getPaymentMethod());
        createdTransaction.setStatus(StatusEnum.COMPLETED);
        transactionRepository.save(createdTransaction);
        return createdTransaction;
    }

    private TransactionDto convertToDto(Transaction transaction){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transactionDto.getId());
        transactionDto.setAmount(transactionDto.getAmount());
        transactionDto.setTransactionDate(transaction.getTransactionDate());
        transactionDto.setPaymentMethod(transaction.getPaymentMethod());
        return transactionDto;
    }
}
