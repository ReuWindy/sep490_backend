package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "day_active")
public class DayActive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "day_active")
    private Date dayActive;

    @Column(name = "mass")
    private Integer mass;

    @Column(name = "amount_per_mass")
    private Double amountPerMass;

    @Column(name = "note")
    private String note;

    @Column(name = "isSpend")
    private boolean isSpend = false;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private Employee employee;
}
