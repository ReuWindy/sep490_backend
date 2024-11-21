package com.fpt.sep490.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderWeightStatisticsView {
    private double totalWeight;
    private List<WeightDetail> details;

    @Data
    @Builder
    public static class WeightDetail {
        private String timePeriod;
        private double weight;
    }
}

