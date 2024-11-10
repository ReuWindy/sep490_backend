package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.model.ReceiptVoucher;
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
    private double amount;
    private Date transactionDate;
    private String paymentMethod;
}
