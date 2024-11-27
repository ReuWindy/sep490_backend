package com.fpt.sep490.dto;

import lombok.Data;

import java.util.List;

@Data
public class TopCategoryResponseDTO {
    private List<TopCategoryDto> topCategories;
    private double totalAmount;

    public TopCategoryResponseDTO(List<TopCategoryDto> topCategories, double totalAmount) {
    }
}
