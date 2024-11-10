package com.fpt.sep490.dto;

import com.fpt.sep490.model.ExpenseVoucher;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Data
public class ExpenseVoucherDto {
    private Long id;
    private String expenseCode;
    private Date expenseDate;
    private double totalAmount;
    private String note;
    private String type;
    private boolean isDeleted;

    private WarehouseReceiptDto warehouseReceiptDto;
    private EmployeeDTO employeeDTO;

    public static ExpenseVoucherDto toDto(ExpenseVoucher expenseVoucher) {
        ExpenseVoucherDto dto = new ExpenseVoucherDto();
        dto.setId(expenseVoucher.getId());
        dto.setExpenseCode(expenseVoucher.getExpenseCode());
        dto.setExpenseDate(expenseVoucher.getExpenseDate());
        dto.setTotalAmount(expenseVoucher.getTotalAmount());
        dto.setNote(expenseVoucher.getNote());
        dto.setType(expenseVoucher.getType());
        dto.setDeleted(expenseVoucher.isDeleted());
        WarehouseReceiptDto warehouseReceipt = new WarehouseReceiptDto();
        if (expenseVoucher.getWarehouseReceipt() != null) {
            warehouseReceipt.setReceiptDate(expenseVoucher.getWarehouseReceipt().getReceiptDate());
            warehouseReceipt.setId(expenseVoucher.getWarehouseReceipt().getId());
            warehouseReceipt.setBatchCode(expenseVoucher.getWarehouseReceipt().getBatch().getBatchCode());
            warehouseReceipt.setDocument(expenseVoucher.getWarehouseReceipt().getDocument());
        }
        EmployeeDTO employee = new EmployeeDTO();
        if (expenseVoucher.getExpensePayer() != null) {
            employee.setId(expenseVoucher.getExpensePayer().getId());
            employee.setDob(expenseVoucher.getExpensePayer().getDob());
            employee.setAddress(expenseVoucher.getExpensePayer().getAddress());
            employee.setEmail(expenseVoucher.getExpensePayer().getEmail());
            employee.setBankName(expenseVoucher.getExpensePayer().getBankName());
            employee.setBankNumber(expenseVoucher.getExpensePayer().getBankNumber());
            employee.setFullName(expenseVoucher.getExpensePayer().getFullName());
        }
        dto.setWarehouseReceiptDto(warehouseReceipt);
        dto.setEmployeeDTO(employee);
        return dto;
    }
}
