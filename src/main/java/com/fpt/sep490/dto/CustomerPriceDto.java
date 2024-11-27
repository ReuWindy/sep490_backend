package com.fpt.sep490.dto;

import com.fpt.sep490.model.Customer;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPriceDto {
     @NotNull(message = "Vui lòng chọn khách hàng")
     private List<Long> customerIds;
     @NotNull(message = "Vui lòng chọn bảng giá")
     private Long priceId;
}
