package com.fpt.sep490.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private long id;
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    private String productCode;
    private String description;
    private double price;
    private String image;
    @NotNull(message = "Vui lòng chọn danh mục")
    private Long categoryId;
    private String categoryName;
    @NotNull(message = "Vui lòng chọn nhà cung cấp")
    private Long supplierId;
    private Long unitOfMeasureId;
    private Long warehouseId;
    private boolean isDeleted;
    private Set<UnitWeightPairs> unitWeightPairsList;
    private double customerPrice;
}
