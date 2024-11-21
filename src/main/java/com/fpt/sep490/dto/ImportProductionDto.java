package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ImportProductionDto {

    @NotBlank(message = "Đơn vị không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Đơn vị chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String unit;

    @NotNull(message = "Khối lượng mỗi đơn vị không được để trống")
    @DecimalMin(value = "0.0", message = "Khối lượng mỗi đơn vị phải lớn hơn hoặc bằng 0")
    private double weightPerUnit;

    @NotNull(message = "Không được để trống")
    @DecimalMin(value = "0.0", message = "Phải lớn hơn hoặc bằng 0")
    private int warehouseId;
}
