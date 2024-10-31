package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private double discount;
    private String productUnit; // quy cách đóng gói
    private double weightPerUnit; // cân nặng mỗi sản phẩm được đóng gói
    private double unitPrice;  // Giá mỗi sản phẩm tại thời điểm đặt hàng ( tính theo kg )
    private double totalPrice;
}
