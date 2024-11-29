package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class importProductFromProductionDto {
    @NotBlank(message = "Vui lòng điền tên sản phẩm")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Tên chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String name;

    @NotNull(message = "Vui lòng điền số lượng")
    @DecimalMin(value = "0", message = "Số lượng phải là số nguyên dương.")
    private int quantity;

    @NotNull(message = "Vui lòng điền trọng lượng cho quy cách")
    @DecimalMin(value = "0", message = "Trọng lượng mỗi đơn vị phải là số dương.")
    private double weightPerUnit;

    @NotBlank(message = "Đơn vị không được để trống")
    @Pattern(regexp = "^[a-zA-Z\\p{L} ]+$", message = "Đơn vị chỉ có thể bao gồm các ký tự chữ.")
    private String unit;

    @NotBlank(message = "Vui lòng chọn danh mục")
    private String categoryId;
}
