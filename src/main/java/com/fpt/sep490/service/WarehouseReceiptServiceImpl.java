package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.WarehouseReceipt;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.WarehouseReceiptRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WarehouseReceiptServiceImpl implements WarehouseReceiptService {

    private final BatchRepository batchRepository;
    private final WarehouseReceiptRepository warehouseReceiptRepository;

    public WarehouseReceiptServiceImpl(BatchRepository batchRepository, WarehouseReceiptRepository warehouseReceiptRepository) {
        this.batchRepository = batchRepository;
        this.warehouseReceiptRepository = warehouseReceiptRepository;
    }

    @Override
    public WarehouseReceipt createWarehouseReceipt(WarehouseReceiptDto receiptDto, String batchCode) {
        WarehouseReceipt receipt = new WarehouseReceipt();
        receipt.setReceiptDate(new Date());
        receipt.setReceiptType(ReceiptType.valueOf(receiptDto.getReceiptType()));
        Batch batch = batchRepository.findByBatchCode(batchCode);
        receipt.setBatch(batch);
        warehouseReceiptRepository.save(receipt);
        return receipt;
    }

    @Override
    public WarehouseReceipt updateReceiptDocument(long receiptId, String document) {
        WarehouseReceipt warehouseReceipt = warehouseReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Receipt not found!!"));
        warehouseReceipt.setDocument(document);
        return warehouseReceipt;
    }

    @Override
    public WarehouseReceipt createImportWarehouseReceipt(String batchCode) {
        Batch batch = batchRepository.findByBatchCode(batchCode);
        if (batch == null) {
            throw new RuntimeException("Batch Not Found!!");
        }

        WarehouseReceipt receipt = new WarehouseReceipt();
        receipt.setReceiptDate(new Date());
        receipt.setReceiptType(ReceiptType.IMPORT);
        receipt.setDocument("N/A");
        receipt.setBatch(batch);

        warehouseReceiptRepository.save(receipt);
        return receipt;
    }
}
