package com.fpt.sep490.dto;

import com.fpt.sep490.model.Contract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private Date dateOfBirth;
    private boolean gender;
    private Set<Contract> contracts;
}
