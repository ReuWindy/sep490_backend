package com.fpt.sep490.service;

import com.fpt.sep490.dto.ExpenseVoucherDto;
import com.fpt.sep490.model.ExpenseVoucher;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface ExpenseVoucherService {
    Page<ExpenseVoucherDto> getExpenseVoucherPagination(Date startDate, Date endDate, int pageNumber, int pageSize);
    ExpenseVoucher createExpense(ExpenseVoucherDto expenseVoucherDto);
    ExpenseVoucher updateExpense(ExpenseVoucherDto expenseVoucherDto);
    ExpenseVoucher deleteExpense(ExpenseVoucherDto expenseVoucherDto, Long id);
}
