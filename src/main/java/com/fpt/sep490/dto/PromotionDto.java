package com.fpt.sep490.dto;

import lombok.Data;

import java.util.Date;
@Data
public class PromotionDto {
    private long id;
    private long supplierProductId;
    private String promotionDetails;
    private double promotionValue;
    private Date startDate;
    private Date endDate;
}
