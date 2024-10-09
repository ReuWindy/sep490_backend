package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Builder
@NoArgsConstructor
@Table(name = "customers")
public class Customer extends User {
    private String name;
    boolean isSupporter= false;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Contract> contracts = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;
}
