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
    private Long id;
    private String fullName;
    private String image;
    private Date dob;
    private Boolean gender;
    private String email;
    private String phoneNumber;
    private String address;
    private Double contractPrice;
    private Set<Contract> contracts;
}
