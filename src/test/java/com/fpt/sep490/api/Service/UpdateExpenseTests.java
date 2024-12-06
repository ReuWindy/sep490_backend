package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.ExpenseVoucherDto;
import com.fpt.sep490.model.ExpenseVoucher;
import com.fpt.sep490.repository.ExpenseVoucherRepository;
import com.fpt.sep490.service.ExpenseVoucherService;
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
public class UpdateExpenseTests {

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
    public void ExpenseService_UpdateExpense_UpdateExpenseSuccessfully() {
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));
        when(expenseVoucherRepository.save(any())).thenReturn(expenseVoucher);

        ExpenseVoucher updatedExpense = expenseVoucherService.updateExpense(expenseVoucherDto);

        assertNotNull(updatedExpense);
        assertEquals(5000, updatedExpense.getTotalAmount());
        assertEquals("Test Expense", updatedExpense.getNote());
        assertEquals("Miscellaneous", updatedExpense.getType());
    }

    @Test
    public void ExpenseService_UpdateExpense_VoucherNotFound() {
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            expenseVoucherService.updateExpense(expenseVoucherDto);
        });

        assertEquals("Không tìm thấy phiếu chi", exception.getMessage());
    }

    @Test
    public void ExpenseService_UpdateExpense_DeletedExpenseVoucher() {
        expenseVoucher.setDeleted(true);
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            expenseVoucherService.updateExpense(expenseVoucherDto);
        });

        assertEquals("Phiếu chi không tồn tại!", exception.getMessage());
    }

    @Test
    public void ExpenseService_UpdateExpense_ExpenseAfterDeadline() {
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 4);  // Set expense date to 4 days ago
        expenseVoucher.setExpenseDate(calendar.getTime());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            expenseVoucherService.updateExpense(expenseVoucherDto);
        });

        assertEquals("Đã quá hạn sửa phiếu chi!", exception.getMessage());
    }

    @Test
    public void ExpenseService_UpdateExpense_ExpenseWithinDeadline() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -2);  // Set expense date to 2 days ago
        expenseVoucher.setExpenseDate(calendar.getTime());

        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));
        when(expenseVoucherRepository.save(any())).thenReturn(expenseVoucher);

        ExpenseVoucher updatedExpense = expenseVoucherService.updateExpense(expenseVoucherDto);

        assertNotNull(updatedExpense);
        assertEquals(5000, updatedExpense.getTotalAmount());
        assertEquals("Test Expense", updatedExpense.getNote());
        assertEquals("Miscellaneous", updatedExpense.getType());
    }

    @Test
    public void ExpenseService_UpdateExpense_ExpenseSaveError() {
        when(expenseVoucherRepository.findById(1L)).thenReturn(Optional.of(expenseVoucher));
        when(expenseVoucherRepository.save(any())).thenThrow(new RuntimeException("Lỗi khi cập nhật phiếu chi"));

        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            expenseVoucherService.updateExpense(expenseVoucherDto);
        });
        assertEquals("Lỗi khi cập nhật phiếu chi", e.getMessage() );
    }
}
