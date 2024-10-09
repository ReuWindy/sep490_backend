package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CustomerDto {
    private long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private double contractPrice;
}
