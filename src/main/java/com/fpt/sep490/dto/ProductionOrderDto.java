package com.fpt.sep490.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class ProductionOrderDto {
    private String description;
    private Date productionDate;

    @NotNull(message = "Số lượng không được null")
    @DecimalMin(value = "0.0", message = "Số lượng phải lớn hơn hoặc bằng 0")
    private double quantity;

    @NotNull(message = "Không được để trống")
    @DecimalMin(value = "0.0", message = "Phải lớn hơn hoặc bằng 0")
    private int productWarehouseId;
;}
