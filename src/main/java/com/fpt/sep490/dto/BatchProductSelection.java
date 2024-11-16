package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BatchProductSelection {
    private long productId;

    @NotBlank(message = "Loại đóng gói không được để trống.")
    @Pattern(regexp = "^[a-zA-Z\\p{L} ]+$", message = "Loại đóng gói chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String unit;

    @NotNull(message = "Khối lượng đóng gói của đơn vị không được để trống.")
    @DecimalMin(value = "0", message = "Khối lượng đóng gói của đơn vị phải là số dương.")
    private double weighPerUnit;

    @NotNull(message = "Id nhà cung cấp không được để trống.")
    @DecimalMin(value = "0", message = "Id nhà cung cấp phải là số dương.")
    private int supplierId;

    private long warehouseId;
}
