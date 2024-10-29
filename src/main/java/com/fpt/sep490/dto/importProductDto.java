package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class importProductDto {

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Tên chỉ có thể bao gồm các ký tự chữ và số.")
    private String name;
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Chỉ có thể bao gồm các ký tự chữ và số.")
    private String description;
    @DecimalMin(value = "0", message = "Giá sản phẩm phải là số dương.")
    private double importPrice;
    private String image;
    @DecimalMin(value = "0", message = "Giá sản phẩm phải là số dương.")
    private int quantity;
    @DecimalMin(value = "0", message = "Giá sản phẩm phải là số dương.")
    private double weightPerUnit;
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Chỉ có thể bao gồm các ký tự chữ.")
    private String unit;
    private String categoryId;
    private Long supplierId;
    private Long unitOfMeasureId;
    private Long warehouseId;
}
