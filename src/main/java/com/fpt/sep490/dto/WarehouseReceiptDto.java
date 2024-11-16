package com.fpt.sep490.dto;

import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.WarehouseReceipt;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Data
public class WarehouseReceiptDto {
    private long id;
    private Date receiptDate;
    private String receiptType;
    private String document;
    private String batchCode;
    private String username;
    private String receiptReason;
    private Set<BatchProductDto> batchProductDtos;

    public static WarehouseReceiptDto toDto(WarehouseReceipt warehouseReceipt) {
        WarehouseReceiptDto dto = new WarehouseReceiptDto();
        dto.setId(warehouseReceipt.getId());
        dto.setReceiptDate(warehouseReceipt.getReceiptDate());
        dto.setReceiptType(String.valueOf(warehouseReceipt.getReceiptType()));
        dto.setBatchCode(warehouseReceipt.getBatch().getBatchCode());
        dto.setReceiptReason(warehouseReceipt.getReceiptReason());
        dto.setUsername(warehouseReceipt.getBatch().getBatchCreator().getUsername());
        Set<BatchProductDto> batchProductDtoSet = new HashSet<>();
        for (BatchProduct bp : warehouseReceipt.getBatch().getBatchProducts()) {
            BatchProductDto batchProductDto = new BatchProductDto();
            batchProductDto.setBatchId(bp.getId());
            batchProductDto.setProductId(bp.getProduct().getId());
            batchProductDto.setAdded(bp.isAdded());
            batchProductDtoSet.add(batchProductDto);
        }
        dto.setBatchProductDtos(batchProductDtoSet);
        return dto;
    }

    public static WarehouseReceiptDto fromEntity(ModelMapper modelMapper, WarehouseReceipt warehouseReceipt) {
        return modelMapper.map(warehouseReceipt, WarehouseReceiptDto.class);
    }
}
