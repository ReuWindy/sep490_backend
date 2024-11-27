package com.fpt.sep490.api.Service;

import com.fpt.sep490.dto.TransactionDto;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.repository.ReceiptVoucherRepository;
import com.fpt.sep490.repository.TransactionRepository;
import com.fpt.sep490.service.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateTransactionTests {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ReceiptVoucherRepository receiptVoucherRepository;
    @Mock
    private TransactionDto transactionDto;
    @Mock
    private Transaction existingTransaction;
    @Mock
    private ReceiptVoucher receiptVoucher;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {

        // Tạo dữ liệu giả cho TransactionDto
        transactionDto = new TransactionDto();
        transactionDto.setId(1L);
        transactionDto.setReceiptVoucherId(1L);
        transactionDto.setAmount(1000.0);
        transactionDto.setTransactionDate(new Date());
        transactionDto.setPaymentMethod("Credit Card");

        // Tạo dữ liệu giả cho Transaction
        existingTransaction = new Transaction();
        existingTransaction.setId(1L);
        existingTransaction.setAmount(500.0);
        existingTransaction.setTransactionDate(new Date());
        existingTransaction.setPaymentMethod("Cash");

        // Tạo dữ liệu giả cho ReceiptVoucher
        receiptVoucher = new ReceiptVoucher();
        receiptVoucher.setId(1L);
        receiptVoucher.setPaidAmount(1000.0);
        receiptVoucher.setTotalAmount(5000.0);
        receiptVoucher.setRemainAmount(4000.0);
    }

    @Test
    void testUpdateTransactionSuccess() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(existingTransaction));
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));

        Transaction updatedTransaction = transactionService.updateTransaction(transactionDto);

        assertNotNull(updatedTransaction, "Giao dịch phải được cập nhật.");
        assertEquals(1000.0, updatedTransaction.getAmount(), "Số tiền giao dịch phải được cập nhật.");
        verify(transactionRepository, times(1)).save(existingTransaction);
        verify(receiptVoucherRepository, times(1)).save(receiptVoucher);
    }

    @Test
    void testUpdateTransactionTransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransaction(transactionDto);
        });

        assertEquals("Transaction Not Found !", exception.getMessage(), "Thông báo lỗi không đúng.");
    }

    @Test
    void testUpdateTransactionReceiptVoucherNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(existingTransaction));
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransaction(transactionDto);
        });

        assertEquals("ReceiptVoucher Not Found !", exception.getMessage(), "Thông báo lỗi không đúng.");
    }

    @Test
    void testUpdateTransactionExpired() {
        // Cập nhật thời gian giao dịch đã quá 3 ngày
        transactionDto.setTransactionDate(new Date(System.currentTimeMillis() - (4L * 24 * 60 * 60 * 1000)));

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(existingTransaction));
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransaction(transactionDto);
        });

        assertEquals("Giao dịch đã quá hạn và không thể cập nhật.", exception.getMessage(), "Thông báo lỗi không đúng.");
    }

    @Test
    void testUpdateTransactionSaveReceiptVoucherError() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(existingTransaction));
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));
        doThrow(new RuntimeException("Xảy ra lỗi khi lưu giao dịch !")).when(receiptVoucherRepository).save(any(ReceiptVoucher.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransaction(transactionDto);
        });

        assertEquals("Xảy ra lỗi khi lưu giao dịch !", exception.getMessage(), "Thông báo lỗi không đúng.");
    }

    @Test
    void testUpdateTransactionAmountNegative() {
        transactionDto.setAmount(-1);

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(existingTransaction));
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransaction(transactionDto);
        });

        assertEquals("Số tiền giao dịch phải là số dương", exception.getMessage());
    }

    @Test
    void testUpdateTransactionPaymentMethodNull() {
        transactionDto.setPaymentMethod(null);

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(existingTransaction));
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransaction(transactionDto);
        });

        assertEquals("Phương thức thanh toán không được để trống", exception.getMessage());
    }
}
