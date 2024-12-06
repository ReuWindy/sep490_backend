package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.ExpenseReportDto;
import com.fpt.sep490.repository.ExpenseVoucherRepository;
import com.fpt.sep490.service.ExpenseVoucherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetExpenseReportTests {
    @Mock
    private ExpenseVoucherRepository expenseVoucherRepository;
    @Mock
    private ExpenseReportDto expenseReportDto;
    @Mock
    private List<Object[]> mockResults;
    @InjectMocks
    private ExpenseVoucherServiceImpl expenseVoucherService;

    @BeforeEach
    void setUp() {
        // Tạo dữ liệu giả cho kiểm thử
        expenseReportDto = new ExpenseReportDto();
        expenseReportDto.setPeriod(1);
        expenseReportDto.setTotalAmount(1000.0);

        // Tạo danh sách kết quả giả
        mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, 1000.0});
        mockResults.add(new Object[]{2, 2000.0});
        mockResults.add(new Object[]{3, 3000.0});
        mockResults.add(new Object[]{4, 4000.0});

    }

    @Test
    public void ExpenseVoucherService_GetExpenseReport_GetExpenseReportByDay() {

        when(expenseVoucherRepository.findDailyExpenseByMonth(any())).thenReturn(mockResults);

        Date date = new Date();
        List<ExpenseReportDto> report = expenseVoucherService.getExpenseReport(date, "day");

        assertNotNull(report);
        assertEquals(4, report.size());
        assertEquals(1, report.get(0).getPeriod());
        assertEquals(1000.0, report.get(0).getTotalAmount());
    }

    // Kiểm thử với loại báo cáo "week"
    @Test
    public void ExpenseVoucherService_GetExpenseReport_GetExpenseReportByWeek() {

        when(expenseVoucherRepository.findDailyExpenseByWeek(any())).thenReturn(mockResults);

        Date date = new Date(); // Ngày hiện tại
        List<ExpenseReportDto> report = expenseVoucherService.getExpenseReport(date, "week");

        assertNotNull(report);
        assertEquals(4, report.size());
        assertEquals(2, report.get(1).getPeriod());
        assertEquals(2000.0, report.get(1).getTotalAmount());
    }


    @Test
    public void ExpenseVoucherService_GetExpenseReport_GetExpenseReportByMonth() {

        when(expenseVoucherRepository.findMonthlyExpenseByYear(any())).thenReturn(mockResults);

        Date date = new Date(); // Ngày hiện tại
        List<ExpenseReportDto> report = expenseVoucherService.getExpenseReport(date, "month");

        assertNotNull(report);
        assertEquals(4, report.size());
        assertEquals(3, report.get(2).getPeriod());
        assertEquals(3000.0, report.get(2).getTotalAmount());
    }

    @Test
    public void ExpenseVoucherService_GetExpenseReport_InvalidType() {
        Date date = new Date(); // Ngày hiện tại

        assertThrows(IllegalArgumentException.class, () -> {
            expenseVoucherService.getExpenseReport(date, "invalid_type");
        });
    }

    @Test
    public void ExpenseVoucherService_GetExpenseReport_EmptyList() {
        // Giả lập repository trả về danh sách trống
        when(expenseVoucherRepository.findDailyExpenseByMonth(any())).thenReturn(new ArrayList<>());

        Date date = new Date(); // Ngày hiện tại
        List<ExpenseReportDto> report = expenseVoucherService.getExpenseReport(date, "day");

        assertNotNull(report);
        assertTrue(report.isEmpty());
    }

}
