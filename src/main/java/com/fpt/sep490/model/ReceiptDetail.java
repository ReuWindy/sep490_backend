package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recipt_details")
public class ReceiptDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_receipt_id")
    private WarehouseReceipt warehouseReceipt;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;
}
