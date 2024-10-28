package com.fpt.sep490.dto;

import com.fpt.sep490.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPriceDto {
     private Long customerIds;
     private Long priceId;
}
