package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.ExpenseVoucherDto;
import com.fpt.sep490.model.ExpenseVoucher;
import com.fpt.sep490.repository.ExpenseVoucherRepository;
import com.fpt.sep490.service.ExpenseVoucherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteExpenseTests {
    @Mock
    private ExpenseVoucherRepository expenseVoucherRepository;
    @Mock
    private ExpenseVoucherDto expenseVoucherDto;
    @Mock
    private ExpenseVoucher expenseVoucher;
    @InjectMocks
    private ExpenseVoucherServiceImpl expenseVoucherService;

    @BeforeEach
    void setUp() {
        // Setup a valid ExpenseVoucherDto
        expenseVoucherDto = new ExpenseVoucherDto();
        expenseVoucherDto.setId(1L);
        expenseVoucherDto.setTotalAmount(5000);
        expenseVoucherDto.setNote("Test Expense");
        expenseVoucherDto.setType("Miscellaneous");

        // Setup a valid ExpenseVoucher
        expenseVoucher = new ExpenseVoucher();
        expenseVoucher.setId(1L);
        expenseVoucher.setTotalAmount(3000);
        expenseVoucher.setNote("Old Expense");
        expenseVoucher.setType("Miscellaneous");
        expenseVoucher.setExpenseDate(new Date());
        expenseVoucher.setDeleted(false);
    }

    @Test
    public void  ExpenseVoucherService_DeleteExpense_ExpenseNotFound() {
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            expenseVoucherService.deleteExpense( 1L);
        });
    }

    @Test
    public void  ExpenseVoucherService_DeleteExpense_ExpenseAlreadyDeleted() {
        expenseVoucher.setDeleted(true); // Giả lập phiếu chi đã bị xóa

        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));

        assertThrows(RuntimeException.class, () -> {
            expenseVoucherService.deleteExpense( 1L);
        });
    }

    @Test
    public void  ExpenseVoucherService_DeleteExpense_DeleteExpenseAfterDeadline() {
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));

        // Giả lập ngày phiếu chi đã quá 3 ngày
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3); // Phiếu chi đã quá hạn
        expenseVoucher.setExpenseDate(calendar.getTime());

        assertThrows(RuntimeException.class, () -> {
            expenseVoucherService.deleteExpense( 1L);
        });
    }

    @Test
    public void  ExpenseVoucherService_DeleteExpense_DeleteExpenseSuccessfully() {
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));
        when(expenseVoucherRepository.save(any())).thenReturn(expenseVoucher);

        ExpenseVoucher deletedExpense = expenseVoucherService.deleteExpense( 1L);

        assertNotNull(deletedExpense);
        assertTrue(deletedExpense.isDeleted());
    }

    @Test
    public void  ExpenseVoucherService_DeleteExpense_DeleteExpenseSaveError() {
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));
        when(expenseVoucherRepository.save(any())).thenThrow(new RuntimeException("Lỗi khi lưu phiếu chi"));

        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            expenseVoucherService.deleteExpense( 1L);
        });
        assertEquals("Lỗi khi xóa phiếu chi", e.getMessage() );
    }

}
