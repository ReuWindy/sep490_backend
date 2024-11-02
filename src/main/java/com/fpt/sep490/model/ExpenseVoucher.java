package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "expense_voucher")
public class ExpenseVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expense_code")
    private String receiptCode;

    @Column(name = "expense_payer")
    private User expensePayer;

    @Column(name = "expense_date")
    private Date expenseDate;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "paid_amount")
    private double paidAmount;

    @Column(name = "remain_amount")
    private double remainAmount;

}
