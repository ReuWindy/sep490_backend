package com.fpt.sep490.service;

import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.model.WarehouseReceipt;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileNotFoundException;

public interface WarehouseReceiptService {
    WarehouseReceipt createWarehouseReceipt(WarehouseReceiptDto receiptDto, String batchCode);
    //void createImportPDF(WarehouseReceipt receipt, String dest) throws FileNotFoundException, DocumentException;
}
