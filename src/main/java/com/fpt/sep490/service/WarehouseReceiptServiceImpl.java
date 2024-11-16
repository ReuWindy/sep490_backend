package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.WarehouseReceipt;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.WarehouseReceiptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public WarehouseReceipt deleteReceiptDocument(long receiptId) {
        WarehouseReceipt warehouseReceipt = warehouseReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Receipt not found!!"));
        if (warehouseReceipt.getBatch().getBatchProducts().stream()
                .anyMatch(batchProduct -> batchProduct.isAdded())) {
            throw new IllegalStateException("Cannot delete WarehouseReceipt because some BatchProducts are marked as added.");
        } else {
            warehouseReceiptRepository.delete(warehouseReceipt);
        }
        return warehouseReceipt;
    }

    @Override
    public List<WarehouseReceipt> getAllWarehouseReceipts() {
        return warehouseReceiptRepository.findAll();
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
        batch.setWarehouseReceipt(receipt);
        batchRepository.save(batch);
        return receipt;
    }

    @Override
    public WarehouseReceipt createImportWarehouseReceiptByBatch(Batch batch) {
        WarehouseReceipt receipt = new WarehouseReceipt();
        receipt.setReceiptDate(new Date());
        receipt.setReceiptType(ReceiptType.IMPORT);
        receipt.setDocument("N/A");
        receipt.setBatch(batch);

        warehouseReceiptRepository.save(receipt);
        batch.setWarehouseReceipt(receipt);
        batchRepository.save(batch);
        return receipt;
    }

    @Override
    public WarehouseReceipt createExportWarehouseReceipt(String batchCode) {
        Batch batch = batchRepository.findByBatchCode(batchCode);
        if (batch == null) {
            throw new RuntimeException("Batch Not Found!!");
        }

        WarehouseReceipt receipt = new WarehouseReceipt();
        receipt.setReceiptDate(new Date());
        receipt.setReceiptType(ReceiptType.EXPORT);
        receipt.setDocument("N/A");
        receipt.setBatch(batch);
        receipt.setReceiptReason("Sản xuất");

        warehouseReceiptRepository.save(receipt);
        return receipt;
    }

    @Override
    public Page<WarehouseReceiptDto> getWarehouseReceipts(Date startDate, Date endDate, ReceiptType receiptType, String username, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<WarehouseReceipt> specification = WarehouseReceiptSpecification.hasType(receiptType)
                    .and(WarehouseReceiptSpecification.hasUsername(username))
                    .and(WarehouseReceiptSpecification.isReceiptDateBetween(startDate, endDate));

            Page<WarehouseReceipt> warehouseReceiptPage = warehouseReceiptRepository.findAll(specification, pageable);

            List<WarehouseReceiptDto> dtos = warehouseReceiptPage.getContent().stream()
                    .map(WarehouseReceiptDto::toDto)
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, warehouseReceiptPage.getTotalElements());
        }
        catch (Exception e) {
            return null;
        }
    }
}
