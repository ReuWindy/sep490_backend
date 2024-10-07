package com.fpt.sep490.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerDto {
    private String fullName;
    private String email;
    private long phoneNumber;
    private String address;
    private BigDecimal contractPrice;
}
