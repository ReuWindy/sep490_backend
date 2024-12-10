package com.fpt.sep490.dto;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.WarehouseReceipt;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
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
    private Long orderId;
    private String orderCode;
    private String username;
    private String receiptReason;
    private Set<BatchProductDto> batchProductDtos;
    private String status;
    private Boolean active = true;
    private Boolean isPay;

    public static WarehouseReceiptDto toDto(WarehouseReceipt warehouseReceipt) {
        int count = 0;
        WarehouseReceiptDto dto = new WarehouseReceiptDto();

        dto.setId(warehouseReceipt.getId());
        dto.setReceiptDate(warehouseReceipt.getReceiptDate());
        dto.setReceiptType(String.valueOf(warehouseReceipt.getReceiptType()));
        dto.setReceiptReason(warehouseReceipt.getReceiptReason());
        if (warehouseReceipt.getReceiptType() == ReceiptType.IMPORT) {
            dto.setIsPay(warehouseReceipt.getIsPay());
        } else {
            dto.setIsPay(true);
        }
        if (warehouseReceipt.getBatch() != null && warehouseReceipt.getBatch().getBatchCreator() != null) {
            dto.setBatchCode(warehouseReceipt.getBatch().getBatchCode());
            dto.setUsername(warehouseReceipt.getBatch().getBatchCreator().getUsername());
        } else if (warehouseReceipt.getOrder() != null) {
            dto.setOrderId(warehouseReceipt.getOrder().getId());
            dto.setOrderCode(warehouseReceipt.getOrder().getOrderCode());
            dto.setUsername(warehouseReceipt.getOrder().getCreateBy());
        } else {
            dto.setUsername(null);
        }

        Set<BatchProductDto> batchProductDtoSet = new HashSet<>();
        if (warehouseReceipt.getBatch() != null && warehouseReceipt.getBatch().getBatchProducts() != null) {
            for (BatchProduct bp : warehouseReceipt.getBatch().getBatchProducts()) {
                BatchProductDto batchProductDto = new BatchProductDto();

                batchProductDto.setBatchId(bp.getId());
                if (bp.getProduct() != null) {
                    batchProductDto.setProductId(bp.getProduct().getId());
                } else {
                    batchProductDto.setProductId(null);
                }
                batchProductDto.setIsAdded(bp.isAdded());
                if (batchProductDto.getIsAdded()) {
                    count++;
                }
                batchProductDtoSet.add(batchProductDto);
            }
        }

        if ((count == batchProductDtoSet.size() && !batchProductDtoSet.isEmpty()) || (warehouseReceipt.getOrder() != null && (Objects.equals(warehouseReceipt.getOrder().getStatus().toString(), "CONFIRMED") || Objects.equals(warehouseReceipt.getOrder().getStatus().toString(), "COMPLETED") || Objects.equals(warehouseReceipt.getOrder().getStatus().toString(), "COMPLETE")))) {
            dto.setStatus("Đã xác nhận");
        } else if ((count == 0 && !batchProductDtoSet.isEmpty()) || (warehouseReceipt.getOrder() != null && (Objects.equals(warehouseReceipt.getOrder().getStatus().toString(), "PENDING")))) {
            dto.setStatus("Chờ xác nhận");
        } else if (!batchProductDtoSet.isEmpty() || (warehouseReceipt.getOrder() != null && Objects.equals(warehouseReceipt.getOrder().getStatus().toString(), "IN_PROCESS"))) {
            dto.setStatus("Đang xử lý");
        } else if (warehouseReceipt.getOrder() != null && Objects.equals(warehouseReceipt.getOrder().getStatus().toString(), "CANCELED")) {
            dto.setStatus("Đã hủy");
        } else if (warehouseReceipt.getOrder() != null && Objects.equals(warehouseReceipt.getOrder().getStatus().toString(), "FAILED")) {
            dto.setStatus("Thất bại");
        } else {
            dto.setStatus("Không có sản phẩm");
        }

        dto.setBatchProductDtos(batchProductDtoSet);
        return dto;
    }


    public static WarehouseReceiptDto fromEntity(ModelMapper modelMapper, WarehouseReceipt warehouseReceipt) {
        return modelMapper.map(warehouseReceipt, WarehouseReceiptDto.class);
    }
}
