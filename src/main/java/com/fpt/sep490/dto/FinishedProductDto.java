package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinishedProductDto {
    @NotNull
    private long productId;

    @NotNull(message = "Số lượng không được null")
    @DecimalMin(value = "0.0", message = "Số lượng phải lớn hơn hoặc bằng 0")
    private double quantity;

    @NotNull(message = "Tỉ lệ không được null")
    @DecimalMin(value = "0.0", message = "Tỉ lệ phải lớn hơn hoặc bằng 0")
    private double proportion;
}
