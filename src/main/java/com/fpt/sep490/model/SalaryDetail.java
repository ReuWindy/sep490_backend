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
@Table(name = "salary_details")
public class SalaryDetail {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;
    private double dailyWage;
    private int daysWorked;
    private double monthlySalary;

    public double calculateSalary(int daysWorked) {
        if (salaryType == SalaryType.DAILY) {
            return dailyWage * daysWorked;
        } else if (salaryType == SalaryType.MONTHLY) {
            return monthlySalary;
        }
        return 0;
    }
}
