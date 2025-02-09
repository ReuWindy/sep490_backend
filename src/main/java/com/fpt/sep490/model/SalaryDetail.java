package com.fpt.sep490.model;

import com.fpt.sep490.Enum.SalaryType;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;
    private double dailyWage;
}
