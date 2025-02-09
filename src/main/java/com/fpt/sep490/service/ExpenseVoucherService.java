package com.fpt.sep490.service;

import com.fpt.sep490.dto.EmployeeSalaryDto;
import com.fpt.sep490.dto.ExpenseReportDto;
import com.fpt.sep490.dto.ExpenseVoucherDto;
import com.fpt.sep490.model.ExpenseVoucher;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface ExpenseVoucherService {
    Page<ExpenseVoucherDto> getExpenseVoucherPagination(Date startDate, Date endDate, int pageNumber, int pageSize, String expenseCode);

    ExpenseVoucher createExpense(ExpenseVoucherDto expenseVoucherDto);

    ExpenseVoucher createEmployeeExpense(List<EmployeeSalaryDto> employeeSalaryDto);

    ExpenseVoucher createEmployeeExpense(Long employeeId);

    ExpenseVoucher createSupplierExpense(Long id);

    ExpenseVoucher updateExpense(ExpenseVoucherDto expenseVoucherDto);

    ExpenseVoucher deleteExpense(Long id);

    List<ExpenseReportDto> getExpenseReport(Date date, String type);
}
