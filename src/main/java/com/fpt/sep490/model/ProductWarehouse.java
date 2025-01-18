package com.fpt.sep490.model;


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
@Table(name = "product_warehouses")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ProductWarehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_quantity")
    private Integer quantity;

    @Column(name = "product_import_price")
    private Double importPrice;

    @Column(name = "product_sell_price")
    private Double sellPrice;

    @Column(name = "product_weight")
    private Double weight;

    @Column(name = "weight_per_unit")
    private Double weightPerUnit;

    @Column(name = "product_unit")
    private String unit;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
}
