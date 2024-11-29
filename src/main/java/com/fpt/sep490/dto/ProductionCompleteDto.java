package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductionCompleteDto {

    @NotNull(message = "Id không được để trống")
    @DecimalMin(value = "0.0", message = "Id phải lớn hơn hoặc bằng 0")
    private Long productWarehouseId;

    @NotNull(message = "Số lượng không được để trống")
    @DecimalMin(value = "0.0", message = "Số lượng phải lớn hơn hoặc bằng 0")
    private int quantity;
}
