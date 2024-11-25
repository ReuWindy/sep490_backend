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

    @NotNull(message = "Không được để trống")
    @DecimalMin(value = "0.0", message = "Phải lớn hơn hoặc bằng 0")
    private Long productWarehouseId;

    @NotNull(message = "Không được để trống")
    @DecimalMin(value = "0.0", message = "Phải lớn hơn hoặc bằng 0")
    private int quantity;
}
