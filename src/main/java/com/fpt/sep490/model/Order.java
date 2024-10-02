package com.fpt.sep490.model;

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

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Date orderDate;
    private double totalAmount;
    private double deposit;  // Số tiền đặt cọc
    private double remainingAmount;  // Số tiền còn lại cần thanh toán
    @Enumerated(EnumType.STRING)
    private StatusEnum status;  // Trạng thái đơn hàng (pending, completed, cancelled)

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails;
}
