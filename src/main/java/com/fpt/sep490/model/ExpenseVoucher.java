package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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
    private String expenseCode;

    @Column(name = "type")
    private String type;

    @Column(name = "expense_date")
    private Date expenseDate;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "note")
    private String note;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @OneToOne
    @JoinColumn(name = "warehouse_receipt_id", referencedColumnName = "id")
    private WarehouseReceipt warehouseReceipt;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @JsonManagedReference
    private Employee expensePayer;
}
