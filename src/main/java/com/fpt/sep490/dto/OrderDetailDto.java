package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    private Long productId;

    @NotBlank(message = "Tên tin tức không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Tên tin tức chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String name;

    @NotBlank(message = "Mô tả tin tức không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Mô tả tin tức chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String description;

    @NotNull(message = "Số lượng không được null")
    @DecimalMin(value = "0", message = "Số lượng phải là số dương.")
    private int quantity;

    @NotNull(message = "Giá nhập không được null")
    @DecimalMin(value = "0", message = "Giá nhập phải là số dương.")
    private double unitPrice;


    @NotNull(message = "Khối lượng cho mỗi đơn vị không được null")
    @DecimalMin(value = "0", message = "Khối lượng cho mỗi đơn vị phải là số dương.")
    private double weightPerUnit;

    @NotBlank(message = "Đơn vị không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String productUnit;

    private Double discount = 0.0;

    @NotNull(message = "Giá không được null")
    @DecimalMin(value = "0", message = "Giá phải là số dương.")
    private double totalPrice;
}
