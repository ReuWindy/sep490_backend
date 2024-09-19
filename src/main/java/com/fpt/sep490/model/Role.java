package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    @Enumerated(EnumType.STRING)
    private EmployeeRole EmployeeRole;
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String description;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "salary_detail_id", referencedColumnName = "id")
    private SalaryDetail salaryDetail;


}
