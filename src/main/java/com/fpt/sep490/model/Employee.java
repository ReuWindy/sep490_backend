package com.fpt.sep490.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "employee_code", unique = true)
    private String employeeCode;

    @Column(name = "employee_join_date")
    private Date joinDate;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_number")
    private String bankNumber;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Role role;

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
