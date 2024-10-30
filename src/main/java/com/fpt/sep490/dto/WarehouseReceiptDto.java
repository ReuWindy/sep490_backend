package com.fpt.sep490.dto;

import com.fpt.sep490.model.WarehouseReceipt;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.Date;

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

    public static WarehouseReceiptDto toDto(WarehouseReceipt warehouseReceipt) {
        WarehouseReceiptDto dto = new WarehouseReceiptDto();
        dto.setId(warehouseReceipt.getId());
        dto.setReceiptDate(warehouseReceipt.getReceiptDate());
        dto.setReceiptType(String.valueOf(warehouseReceipt.getReceiptType()));
        dto.setBatchCode(warehouseReceipt.getBatch().getBatchCode());
        dto.setUsername(warehouseReceipt.getBatch().getBatchCreator().getUsername());
        return dto;
    }

    public static WarehouseReceiptDto fromEntity(ModelMapper modelMapper, WarehouseReceipt warehouseReceipt) {
        return modelMapper.map(warehouseReceipt, WarehouseReceiptDto.class);
    }
}
