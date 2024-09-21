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
@Table(name = "supplier_products")
public class SupplierProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double price;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "supplierProduct", cascade = CascadeType.ALL)
    private Set<Discount> discounts;

    @OneToMany(mappedBy = "supplierProduct", cascade = CascadeType.ALL)
    private Set<Promotion> promotions;
}
