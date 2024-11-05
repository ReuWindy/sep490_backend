package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="batch_products")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BatchProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "is_added")
    boolean isAdded = false;

    @Column(name = "product_quantity")
    private int quantity;

    @Column(name = "product_price")
    private double price;

    @Column(name = "product_weight")
    private double weight;

    @Column(name = "weight_per_unit")
    private double weightPerUnit;

    @Column(name = "product_unit")
    private String unit;

    @Column(name = "warehouse_id")
    private long warehouseId;

    @Column(name = "batch_product_description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    @JsonBackReference
    private Batch batch;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;
}
