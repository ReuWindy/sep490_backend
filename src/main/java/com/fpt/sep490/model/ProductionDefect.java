package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "production_defects")
public class ProductionDefect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "production_order_id")
    private ProductionOrder productionOrder;

    private double defectiveQuantity;

    private String defectReason;

    @Enumerated(EnumType.STRING)
    private DefectAction defectAction;
}
