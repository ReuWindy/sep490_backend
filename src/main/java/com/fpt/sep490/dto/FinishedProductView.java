package com.fpt.sep490.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class FinishedProductView {
    private String productName;
    private List<FinishedProductDetail> finishedProducts;

    @Data
    @Builder
    public static class FinishedProductDetail {
        private String finishedProductName;
        private double proportion;
    }
}