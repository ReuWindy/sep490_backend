package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private EmployeeRole employeeRole;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "salary_detail_id", referencedColumnName = "id")
    private SalaryDetail salaryDetail;
}

