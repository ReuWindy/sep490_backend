package com.fpt.sep490.model;


import com.fpt.sep490.Enum.SalaryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    @Column(unique = true)
    private String employeeCode;


    private Date joinDate;
    private String bankName;
    private String bankNumber;
    private boolean isActive;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
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
