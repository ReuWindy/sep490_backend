package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "batchs")

public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "batch_code")
    private String batchCode;

    @Column(name = "import_date")
    private Date importDate;

    @Column(name = "batch_status")
    private String batchStatus;

    @OneToOne(mappedBy = "batch")
    @JsonBackReference
    @JoinColumn(name = "warehouse_receipt_id")
    private WarehouseReceipt warehouseReceipt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BatchProduct> batchProducts;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User batchCreator;

    @Column(name = "batch_type")
    @Enumerated(EnumType.STRING)
    private ReceiptType receiptType;
}
