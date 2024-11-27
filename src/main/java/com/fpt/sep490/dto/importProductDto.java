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
public class importProductDto {
    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Tên chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String name;

    private String description;

    @NotNull(message = "Giá nhập không được null")
    @DecimalMin(value = "0", message = "Giá nhập phải là số dương.")
    private double importPrice;

    private String image;

    @NotNull(message = "Số lượng không được null")
    @DecimalMin(value = "0", message = "Số lượng phải là số nguyên dương.")
    private int quantity;

    @NotNull(message = "Trọng lượng mỗi đơn vị không được null")
    @DecimalMin(value = "0", message = "Trọng lượng mỗi đơn vị phải là số dương.")
    private double weightPerUnit;

    @NotBlank(message = "Đơn vị không được để trống")
    @Pattern(regexp = "^[a-zA-Z\\p{L} ]+$", message = "Đơn vị chỉ có thể bao gồm các ký tự chữ.")
    private String unit;

    @NotBlank(message = "Id loại sản phẩm không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Id loại sản phẩm chỉ có thể bao gồm các ký tự chữ.")
    private String categoryId;

    @NotNull(message = "Id nhà cung cấp không được null")
    private Long supplierId;

    @NotNull(message = "Id đơn vị đo lường không được null")
    private Long unitOfMeasureId;

    @NotNull(message = "Id kho hàng không được null")
    @DecimalMin(value = "0", message = "Id kho hàng phải là số dương.")
    private Long warehouseId;
}
