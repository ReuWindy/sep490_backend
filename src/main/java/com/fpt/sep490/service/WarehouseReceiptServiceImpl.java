package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.WarehouseReceipt;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.WarehouseReceiptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


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
        receipt.setReceiptDate(receiptDto.getReceiptDate());
        receipt.setReceiptType(ReceiptType.valueOf(receiptDto.getReceiptType()));
        Batch batch = batchRepository.findByBatchCode(batchCode);
        receipt.setBatch(batch);
        warehouseReceiptRepository.save(receipt);
        return receipt;
    }

    @Override
    public WarehouseReceipt createWarehouseReceiptByBatchCode(String batchCode, ReceiptType receiptType) {
        Batch batch = batchRepository.findByBatchCode(batchCode);
        if (batch == null) {
            throw new RuntimeException("Không tìm thấy Batch với mã: " + batchCode);
        }
        WarehouseReceipt receipt = new WarehouseReceipt();
        receipt.setReceiptDate(LocalDateTime.now());
        receipt.setReceiptType(receiptType);
        receipt.setDocument("Document nào đó");
        receipt.setBatch(batch);
        warehouseReceiptRepository.save(receipt);
        return receipt;
    }
}
