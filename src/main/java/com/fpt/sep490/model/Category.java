package com.fpt.sep490.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Tên chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} ]+$", message = "Mô tả chỉ có thể bao gồm các ký tự chữ, số và khoảng trắng.")
    private String description;

    private Boolean active;
}
