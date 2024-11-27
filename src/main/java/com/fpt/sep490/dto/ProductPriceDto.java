package com.fpt.sep490.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceDto {
    @NotNull(message = "Vui lòng nhập đơn giá")
    @Min(value = 0, message = "Đơn giá phải lớn hơn 0")
    private double unitPrice;
    @NotNull(message = "Không tìm thấy sản phẩm")
    private Long productId;
    @NotNull(message = "Không tìm thấy bảng giá")
    private Long priceId;
}
