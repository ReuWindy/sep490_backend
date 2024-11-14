package com.fpt.sep490.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fpt.sep490.Enum.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventories")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(name = "inventory_date")
    private Date inventoryDate;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<InventoryDetail> inventoryDetails;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
