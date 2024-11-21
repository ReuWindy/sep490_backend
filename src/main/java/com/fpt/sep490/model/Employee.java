package com.fpt.sep490.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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


    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<DayActive> dayActives;

    @OneToMany(mappedBy = "expensePayer")
    @JsonBackReference
    private Set<ExpenseVoucher> expenseVouchers = new HashSet<>();
}
