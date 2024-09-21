package com.fpt.sep490.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
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
}
