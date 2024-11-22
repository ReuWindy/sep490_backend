package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fpt.sep490.Enum.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "production_orders")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String productionCode;
    private String description;
    private double quantity;
    private Date productionDate;
    private Date completionDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "production_order_finished_product",
            joinColumns = @JoinColumn(name = "production_order_id"),
            inverseJoinColumns = @JoinColumn(name = "finished_product_id")
    )
    private Set<FinishedProduct> finishedProducts = new HashSet<>();

    private double defectiveQuantity;
    private String defectReason;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @ManyToOne
    @JoinColumn(name = "product_warehouse_id", nullable = false)
    @JsonBackReference
    private ProductWarehouse productWarehouse;
}

