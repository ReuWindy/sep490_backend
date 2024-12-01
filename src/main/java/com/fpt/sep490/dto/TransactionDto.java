package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
    private long id;
    private long receiptVoucherId;
    private long orderId;
    private double amount;
    private Date transactionDate;
    private String paymentMethod;

    public static TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        return dto;
    }
}
