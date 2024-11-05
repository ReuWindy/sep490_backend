package com.fpt.sep490.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Valid
public class ExportProductDto {
    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Tên chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String productName;

    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Tên chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String unit;

    @NotNull(message = "Trọng lượng mỗi đơn vị không được null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Trọng lượng mỗi đơn vị phải lớn hơn hoặc bằng 0")
    private double weightPerUnit;

    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private int quantity;

    @NotBlank(message = "Id loại sản phẩm không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Id loại sản phẩm chỉ có thể bao gồm các ký tự chữ và số.")
    private String categoryId;

    @NotNull(message = "Id nhà cung cấp không được null")
    private Long supplierId;

    @NotNull(message = "Id kho hàng không được null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Id kho hàng phải lớn hơn hoặc bằng 0")
    private double warehouseId;
}
