package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(name = "customer_supporter")
    boolean isSupporter= false;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Contract> contracts = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "price_id")
    private Price price;
}
