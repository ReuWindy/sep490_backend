package com.fpt.sep490.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RevenueStatisticsView {
    private double totalRevenue;
    private List<RevenueDetail> details;

    @Data
    @Builder
    public static class RevenueDetail {
        private String timePeriod;
        private double revenue;
    }
}

