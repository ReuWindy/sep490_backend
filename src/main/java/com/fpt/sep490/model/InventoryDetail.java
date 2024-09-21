package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

// Dung cho viec kiem ke hoang hoa
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_details")
public class InventoryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double quantity;
    private String description;
}
