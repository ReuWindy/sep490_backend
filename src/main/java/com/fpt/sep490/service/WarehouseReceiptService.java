package com.fpt.sep490.service;

import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.model.WarehouseReceipt;

public interface WarehouseReceiptService {
    WarehouseReceipt createWarehouseReceipt(WarehouseReceiptDto receiptDto, String batchCode);
    WarehouseReceipt createImportWarehouseReceipt(String batchCode);
    WarehouseReceipt updateReceiptDocument(long receiptId, String document);
}
