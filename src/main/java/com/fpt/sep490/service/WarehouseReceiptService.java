package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.model.WarehouseReceipt;

public interface WarehouseReceiptService {
    WarehouseReceipt createWarehouseReceipt(WarehouseReceiptDto receiptDto, String batchCode);
    WarehouseReceipt createWarehouseReceiptByBatchCode(String batchCode, ReceiptType receiptType);
}
