package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fpt.sep490.Enum.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    @JsonBackReference
    private ReceiptVoucher receiptVoucher;

    private double amount;
    private Date transactionDate;
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
