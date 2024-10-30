package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.model.WarehouseReceipt;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface WarehouseReceiptService {
    List<WarehouseReceipt> getAllWarehouseReceipts();
    WarehouseReceipt createWarehouseReceipt(WarehouseReceiptDto receiptDto, String batchCode);
    WarehouseReceipt createImportWarehouseReceipt(String batchCode);
    WarehouseReceipt createImportWarehouseReceiptByBatchId(long batchId);
    WarehouseReceipt createExportWarehouseReceipt(String batchCode);
    WarehouseReceipt updateReceiptDocument(long receiptId, String document);
    Page<WarehouseReceiptDto> getWarehouseReceipts(Date startDate, Date endDate, ReceiptType receiptType,String username, int pageNumber, int pageSize);
}
