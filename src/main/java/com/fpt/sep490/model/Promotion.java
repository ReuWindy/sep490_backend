package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "supplier_product_id")
    private SupplierProduct supplierProduct;

    private String promotionDetails;

    private double promotionValue;  // Giá trị khuyến mại (có thể là % hoặc giá trị cố định)

    private Date startDate;

    private Date endDate;
}
