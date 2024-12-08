package com.fpt.sep490.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopCategoryResponseDTO {
    private List<TopCategoryDto> topCategories;
    private double totalAmount;
}
