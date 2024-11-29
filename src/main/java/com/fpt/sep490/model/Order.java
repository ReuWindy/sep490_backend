package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fpt.sep490.Enum.StatusEnum;
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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_code", unique = true)
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Date orderDate;
    private double totalAmount;
    private String createBy;
    private double deposit;  // Số tiền đặt cọc
    private double remainingAmount;  // Số tiền còn lại cần thanh toán
    @Enumerated(EnumType.STRING)
    private StatusEnum status;  // Trạng thái đơn hàng (pending, completed, cancelled)

    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "warehouse_receipt_id")
    private WarehouseReceipt warehouseReceipt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private ReceiptVoucher receiptVoucher;
}
