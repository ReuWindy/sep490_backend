package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.TransactionDto;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.repository.ReceiptVoucherRepository;
import com.fpt.sep490.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final ReceiptVoucherRepository receiptVoucherRepository;

    public TransactionServiceImpl (TransactionRepository transactionRepository,ReceiptVoucherRepository receiptVoucherRepository){
        this.transactionRepository = transactionRepository;
        this.receiptVoucherRepository = receiptVoucherRepository;
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
    public Set<TransactionDto> getTransactionByReceiptId(long receiptId) {
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
        ReceiptVoucher receiptVoucher = receiptVoucherRepository.findById(transactionDto.getReceiptVoucherId()).orElse(null);
        if (receiptVoucher != null) {
            createdTransaction.setReceiptVoucher(receiptVoucher);
            createdTransaction.setAmount(transactionDto.getAmount());
            createdTransaction.setTransactionDate(new Date());
            createdTransaction.setPaymentMethod(transactionDto.getPaymentMethod());
            createdTransaction.setStatus(StatusEnum.COMPLETED);
            transactionRepository.save(createdTransaction);
            return createdTransaction;
        }
        return null;
    }

    private TransactionDto convertToDto(Transaction transaction){
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setTransactionDate(transaction.getTransactionDate());
        transactionDto.setPaymentMethod(transaction.getPaymentMethod());
        return transactionDto;
    }
}
