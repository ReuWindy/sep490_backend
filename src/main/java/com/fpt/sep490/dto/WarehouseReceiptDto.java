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
    private String orderCode;
    private String username;
    private String receiptReason;
    private Set<BatchProductDto> batchProductDtos;
    private String status;

    public static WarehouseReceiptDto toDto(WarehouseReceipt warehouseReceipt) {
        int count = 0;
        WarehouseReceiptDto dto = new WarehouseReceiptDto();
        dto.setId(warehouseReceipt.getId());
        dto.setReceiptDate(warehouseReceipt.getReceiptDate());
        dto.setReceiptType(String.valueOf(warehouseReceipt.getReceiptType()));
        dto.setBatchCode(warehouseReceipt.getBatch().getBatchCode());
        dto.setOrderCode(warehouseReceipt.getOrder().getOrderCode());
        dto.setReceiptReason(warehouseReceipt.getReceiptReason());
        if (warehouseReceipt.getBatch() != null) {
            dto.setUsername(warehouseReceipt.getBatch().getBatchCreator().getUsername());
        } else if (warehouseReceipt.getOrder() != null) {
            dto.setUsername(warehouseReceipt.getOrder().getCreateBy());
        }
        Set<BatchProductDto> batchProductDtoSet = new HashSet<>();
        for (BatchProduct bp : warehouseReceipt.getBatch().getBatchProducts()) {
            BatchProductDto batchProductDto = new BatchProductDto();
            batchProductDto.setBatchId(bp.getId());
            batchProductDto.setProductId(bp.getProduct().getId());
            batchProductDto.setAdded(bp.isAdded());
            if (batchProductDto.isAdded()) {
                count++;
            }
            batchProductDtoSet.add(batchProductDto);
        }
        if (count == batchProductDtoSet.size()) {
            dto.setStatus("Đã xác nhận");
        } else if (count == 0) {
            dto.setStatus("Chờ xác nhận");
        } else {
            dto.setStatus("Đang xử lý");
        }
        dto.setBatchProductDtos(batchProductDtoSet);
        return dto;
    }

    public static WarehouseReceiptDto fromEntity(ModelMapper modelMapper, WarehouseReceipt warehouseReceipt) {
        return modelMapper.map(warehouseReceipt, WarehouseReceiptDto.class);
    }
}
