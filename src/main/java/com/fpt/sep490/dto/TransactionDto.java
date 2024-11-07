package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.model.Transaction;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Data
public class TransactionDto {
    private long id;
    private double amount;
    private Date transactionDate;
    private String paymentMethod;
    private StatusEnum status;

    public static TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setStatus(transaction.getStatus());
        return dto;
    }
}
