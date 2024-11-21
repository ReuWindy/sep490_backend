package com.fpt.sep490.model;

import com.fpt.sep490.Enum.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "production_orders")
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String productionCode;
    private String description;
    private double quantity;
    private Date productionDate;
    private Date completionDate;

    @Transient
    private Set<FinishedProduct> finishedProducts; // Danh sách sản phẩm đầu ra

    private double defectiveQuantity;  // Số lượng sản phẩm bị hỏng
    private String defectReason;  // Lý do sản phẩm hỏng (lỗi máy móc, lỗi nguyên liệu...)

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @ManyToOne
    @JoinColumn(name = "product_warehouse_id", nullable = false)
    private ProductWarehouse productWarehouse;
}
