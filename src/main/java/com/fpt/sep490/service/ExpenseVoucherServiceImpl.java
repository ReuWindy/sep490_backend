package com.fpt.sep490.service;

import com.fpt.sep490.dto.ExpenseReportDto;
import com.fpt.sep490.dto.ExpenseVoucherDto;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.ExpenseVoucher;
import com.fpt.sep490.repository.ExpenseVoucherRepository;
import com.fpt.sep490.utils.RandomExpenseCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseVoucherServiceImpl implements ExpenseVoucherService {

    private final ExpenseVoucherRepository expenseVoucherRepository;

    public ExpenseVoucherServiceImpl(ExpenseVoucherRepository expenseVoucherRepository) {
        this.expenseVoucherRepository = expenseVoucherRepository;
    }

    @Override
    public Page<ExpenseVoucherDto> getExpenseVoucherPagination(Date startDate, Date endDate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<ExpenseVoucher> specification = ExpenseVoucherSpecification.isExpenseDateBetween(startDate, endDate);

            Page<ExpenseVoucher> expenseVoucherPage = expenseVoucherRepository.findAll(specification, pageable);

            List<ExpenseVoucherDto> dtos = expenseVoucherPage.getContent().stream()
                    .map(ExpenseVoucherDto::toDto)
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, expenseVoucherPage.getTotalElements());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ExpenseVoucher createExpense(ExpenseVoucherDto expenseVoucherDto) {
        ExpenseVoucher newExpenseVoucher = new ExpenseVoucher();
        newExpenseVoucher.setExpenseCode(RandomExpenseCodeGenerator.generateExpenseCode());
        newExpenseVoucher.setExpenseDate(new Date());
        newExpenseVoucher.setTotalAmount(expenseVoucherDto.getTotalAmount());
        newExpenseVoucher.setNote(expenseVoucherDto.getNote());
        newExpenseVoucher.setType(expenseVoucherDto.getType());
        return expenseVoucherRepository.save(newExpenseVoucher);
    }

    @Override
    public ExpenseVoucher updateExpense(ExpenseVoucherDto expenseVoucherDto) {
        ExpenseVoucher expenseVoucher = expenseVoucherRepository.findById(expenseVoucherDto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chi"));

        if (expenseVoucher.isDeleted()){
            throw new RuntimeException("Phiếu chi không tồn tại!");
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date datePlusThreeDays = calendar.getTime();

        if (expenseVoucher.getExpenseDate().after(datePlusThreeDays)) {
            throw new RuntimeException("Đã quá hạn sửa phiếu chi!");
        }
        expenseVoucher.setTotalAmount(expenseVoucherDto.getTotalAmount());
        expenseVoucher.setNote(expenseVoucherDto.getNote());
        expenseVoucher.setType(expenseVoucherDto.getType());
        return expenseVoucherRepository.save(expenseVoucher);
    }

    @Override
    public ExpenseVoucher deleteExpense(ExpenseVoucherDto expenseVoucherDto) {
        ExpenseVoucher expenseVoucher = expenseVoucherRepository.findById(expenseVoucherDto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chi"));

        if (expenseVoucher.isDeleted()){
            throw new RuntimeException("Phiếu chi không tồn tại!");
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date datePlusThreeDays = calendar.getTime();

        if (expenseVoucher.getExpenseDate().after(datePlusThreeDays)) {
            throw new RuntimeException("Đã quá hạn xóa phiếu chi!");
        }
        expenseVoucher.setDeleted(true);
        return expenseVoucherRepository.save(expenseVoucher);
    }

    @Override
    public List<ExpenseReportDto> getExpenseReport(Date date, String type) {
        List<Object[]> results;

        switch (type.toLowerCase()) {
            case "day":
                results = expenseVoucherRepository.findDailyExpenseByMonth(date);
                break;
            case "week":
                results = expenseVoucherRepository.findWeeklyExpenseByYear(date);
                break;
            case "month":
                results = expenseVoucherRepository.findMonthlyExpenseByYear(date);
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        // Chuyển đổi kết quả thành DTO
        List<ExpenseReportDto> report = new ArrayList<>();
        for (Object[] result : results) {
            ExpenseReportDto dto = new ExpenseReportDto();
            dto.setPeriod(((Number) result[0]).intValue());
            dto.setTotalAmount((Double) result[1]);
            report.add(dto);
        }

        return report;
    }
}
