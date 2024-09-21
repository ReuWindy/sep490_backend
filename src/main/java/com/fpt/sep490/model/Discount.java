package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "supplier_product_id")
    private SupplierProduct supplierProduct;  // Liên kết đến sản phẩm của nhà cung cấp

    private double discountPercentage;  // Phần trăm chiết khấu
}
