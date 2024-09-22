package com.fpt.sep490.model;

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

    private String batchCode;  // Mã của lô hàng
    private int numberOfBags;
    private double totalWeight;
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private Date importDate;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "warehouse_receipt_id")
    private WarehouseReceipt warehouseReceipt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BatchProduct> batchProducts;

    private boolean isDamaged;
}
