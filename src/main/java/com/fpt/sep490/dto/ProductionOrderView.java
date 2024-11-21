package com.fpt.sep490.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ProductionOrderView {
    private String productName;
    private double quantity;
    private Date productionDate;
    private Date completionDate;
    private List<FinishedProductDetail> finishedProducts;
    private double defectiveQuantity;
    private String defectiveReason;
    private String status;

    @Data
    @Builder
    public static class FinishedProductDetail {
        private String productName;
        private double proportion;
    }
}
