package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;
@Data
public class NewDto {
    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Tên chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String name;

    private String image;
    @NotBlank(message = "Loại tin tức không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Loại tin tức chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String type;

    @NotBlank(message = "Mô tả không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Mô tả chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String description;

    @NotBlank(message = "Nội dung không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Nội dung chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String content;

}
