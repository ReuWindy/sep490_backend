package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "unif_of_measure")
public class UnitOfMeasure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String unitName;  // Tên đơn vị đo lường (kg, bao, tấn, tạ)

    private double conversionFactor; // Hệ số chuyển đổi (ví dụ: 1 tấn = 1000 kg)
}
