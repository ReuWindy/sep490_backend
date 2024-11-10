package com.fpt.sep490.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fpt.sep490.Enum.EmployeeRole;
import com.fpt.sep490.Enum.SalaryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employees")
public class Employee extends User {

    @Column(name = "employee_code", unique = true)
    private String employeeCode;

    @Column(name = "employee_join_date")
    private Date joinDate;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_number")
    private String bankNumber;

    @Column(name = "daily_wage")
    private double dailyWage;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Role role;

    @Column(name = "employee_role")
    @Enumerated(EnumType.STRING)
    private EmployeeRole employeeRole;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<DayActive> dayActives;

    public double calculateSalary(int daysWorked) {
        if (role != null && role.getSalaryDetail() != null) {
            SalaryDetail salaryDetail = role.getSalaryDetail();
            if (salaryDetail.getSalaryType() == SalaryType.DAILY) {
                return salaryDetail.getDailyWage() * daysWorked;
            } else if (salaryDetail.getSalaryType() == SalaryType.MONTHLY) {
                return salaryDetail.getMonthlySalary();
            }
        }
        return 0.0;
    }
}
