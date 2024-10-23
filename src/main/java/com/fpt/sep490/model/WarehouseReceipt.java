package com.fpt.sep490.model;

import com.fpt.sep490.Enum.ReceiptType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "warehouse_receipts")
public class WarehouseReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date receiptDate;
    @Enumerated(EnumType.STRING)
    private ReceiptType receiptType;
    private String document;

    @OneToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;
}
