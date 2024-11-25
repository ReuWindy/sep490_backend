package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ProductionFinishDto {
    private String description;
    @NotNull(message = "Không được để trống")
    @DecimalMin(value = "0.0", message = "Phải lớn hơn hoặc bằng 0")
    private double realQuantity;
    private double defectQuantity;

    @NotNull(message = "Không được để trống")
    @DecimalMin(value = "0.0", message = "Phải lớn hơn hoặc bằng 0")
    private Long id;
}
