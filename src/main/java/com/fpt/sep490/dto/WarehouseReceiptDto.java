package com.fpt.sep490.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarehouseReceiptDto {
    private long warehouseId;
    private LocalDateTime receiptDate;
    private String receiptType;
    private String document;
}
