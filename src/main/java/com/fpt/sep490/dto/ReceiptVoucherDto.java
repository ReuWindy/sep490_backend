package com.fpt.sep490.dto;

import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.model.Transaction;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
public class ReceiptVoucherDto {
    private Long id;
    private String receiptCode;
    private Date receiptDate;
    private Date dueDate;
    private double totalAmount;
    private double paidAmount;
    private double remainAmount;
    private List<TransactionDto> transactionDtoList;
    private OrderDto orderDto;

    public static ReceiptVoucherDto toDto(ReceiptVoucher receiptVoucher) {
        ReceiptVoucherDto dto = new ReceiptVoucherDto();
        dto.setId(receiptVoucher.getId());
        dto.setReceiptCode(receiptVoucher.getReceiptCode());
        dto.setReceiptDate(receiptVoucher.getReceiptDate());
        dto.setTotalAmount(receiptVoucher.getTotalAmount());
        dto.setPaidAmount(receiptVoucher.getPaidAmount());
        dto.setRemainAmount(receiptVoucher.getRemainAmount());
        dto.setDueDate(receiptVoucher.getDueDate());
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction t: receiptVoucher.getTransactions()) {
            TransactionDto transactionDto = TransactionDto.toDto(t);
            transactionDtos.add(transactionDto);
        }
        OrderDto order = new OrderDto();
        order.setId(receiptVoucher.getOrder().getId());
        order.setCustomer(receiptVoucher.getOrder().getCustomer());
        order.setOrderCode(receiptVoucher.getOrder().getOrderCode());
        dto.setOrderDto(order);
        dto.setTransactionDtoList(transactionDtos);
        return dto;
    }
}
