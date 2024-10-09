package com.fpt.sep490.model;

import com.fpt.sep490.Enum.ReceiptType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

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

    private LocalDateTime receiptDate;
    @Enumerated(EnumType.STRING)
    private ReceiptType receiptType;
    private String document;

    @OneToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;
}
