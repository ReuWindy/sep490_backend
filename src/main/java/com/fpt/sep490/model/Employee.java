package com.fpt.sep490.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employees")
public class Employee extends User {
    private String bankDetail;

    @Setter
    @Getter
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "employee_roles",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return super.getId();
    }

    public void setId(long id) {
        super.setId(id);
    }

    public double getSalary(int daysWorked) {
        return roles.stream()
                .map(role -> role.getSalaryDetail().calculateSalary(daysWorked))
                .reduce(0.0, Double::sum);
    }
}

