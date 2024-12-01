package com.fpt.sep490.dto;

import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class ProductionOrderView {
    private Long id;
    private String productionCode;
    private String productName;
    private double quantity;
    private String status;
    private Date productionDate;
    private Date completionDate;
    private List<FinishedProductDetail> finishedProducts;
    private User creator;
    private String unit;
    private double weightPerUnit;
    private String note;
    private Long productId;
    private Boolean active;

    @Data
    @Builder
    public static class FinishedProductDetail {
        private Long productId;
        private String productCode;
        private String productName;
        private double quantity;
        private double defectQuantity;
        private double realQuantity;
        private String note;
        private Long id;
        private double proportion;
        private Set<ProductWarehouse> productWarehouses;
    }
}
