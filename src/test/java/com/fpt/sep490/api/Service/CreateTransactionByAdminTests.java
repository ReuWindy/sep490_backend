package com.fpt.sep490.api.Service;

import com.fpt.sep490.Enum.StatusEnum;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateTransactionByAdminTests {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionDto transactionDto;
    @Mock
    private ReceiptVoucher receiptVoucher;
    @Mock
    private ReceiptVoucherRepository receiptVoucherRepository;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {

        // Dữ liệu TransactionDto
        transactionDto = new TransactionDto();
        transactionDto.setReceiptVoucherId(1L);
        transactionDto.setAmount(1000.0);
        transactionDto.setPaymentMethod("Credit Card");

        // Dữ liệu ReceiptVoucher
        receiptVoucher = new ReceiptVoucher();
        receiptVoucher.setId(1L);
        receiptVoucher.setPaidAmount(500.0);
        receiptVoucher.setTotalAmount(2000.0);
        receiptVoucher.setRemainAmount(1500.0);
    }

    @Test
    void testCreateTransactionByAdminSuccess() {
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));

        Transaction newTransaction = new Transaction();
        newTransaction.setId(1L);
        newTransaction.setAmount(transactionDto.getAmount());
        newTransaction.setTransactionDate(new Date());
        newTransaction.setPaymentMethod(transactionDto.getPaymentMethod());
        newTransaction.setStatus(StatusEnum.COMPLETED);
        newTransaction.setReceiptVoucher(receiptVoucher);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(newTransaction);

        Transaction createdTransaction = transactionService.createTransactionByAdmin(transactionDto);

        assertNotNull(createdTransaction);
        assertEquals(transactionDto.getAmount(), createdTransaction.getAmount());
        assertEquals(StatusEnum.COMPLETED, createdTransaction.getStatus());
        verify(receiptVoucherRepository, times(1)).save(receiptVoucher);
    }

    @Test
    void testCreateTransactionByAdminReceiptVoucherNotFound() {
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransactionByAdmin(transactionDto);
        });

        assertEquals("ReceiptVoucher Not Found !", exception.getMessage());
    }

    @Test
    void testCreateTransactionByAdminAmountExceedsTotal() {
        transactionDto.setAmount(3000.0); // Số tiền lớn hơn tổng số tiền còn lại
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));

        Transaction createdTransaction = transactionService.createTransactionByAdmin(transactionDto);

        assertNotNull(createdTransaction);
        assertEquals(receiptVoucher.getTotalAmount() - receiptVoucher.getPaidAmount(), receiptVoucher.getRemainAmount(), "Số tiền còn lại không đúng.");
    }

    @Test
    void testCreateTransactionAmountNegative() {
        transactionDto.setAmount(-1);

        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransactionByAdmin(transactionDto);
        });

        assertEquals("Số tiền giao dịch phải là số dương", exception.getMessage());
    }

    @Test
    void testCreateTransactionPaymentMethodNull() {
        transactionDto.setPaymentMethod(null);

        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransactionByAdmin(transactionDto);
        });

        assertEquals("Phương thức thanh toán không được để trống", exception.getMessage());
    }

    @Test
    void testCreateTransactionByAdminSaveReceiptVoucherError() {
        when(receiptVoucherRepository.findById(anyLong())).thenReturn(Optional.of(receiptVoucher));
        doThrow(new RuntimeException("Xảy ra lỗi khi lưu giao dịch mới")).when(receiptVoucherRepository).save(any(ReceiptVoucher.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransactionByAdmin(transactionDto);
        });

        assertEquals("Xảy ra lỗi khi tạo giao dịch mới", exception.getMessage(), "Thông báo lỗi không đúng.");
    }
}
