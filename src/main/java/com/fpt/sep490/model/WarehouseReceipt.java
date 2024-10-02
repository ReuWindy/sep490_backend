package com.fpt.sep490.model;

import com.fpt.sep490.Enum.ReceiptType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
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

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    private Date receiptDate;
    @Enumerated(EnumType.STRING)
    private ReceiptType receiptType;

    @OneToMany(mappedBy = "warehouseReceipt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReceiptDetail> receiptDetails;  // Chi tiết hoá đơn

    @OneToMany(mappedBy = "warehouseReceipt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Batch> batches;

    private String document; // Đường dẫn lưu file hoá đơn (PDF, ảnh)
}
