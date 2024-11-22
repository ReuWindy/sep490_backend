package com.fpt.sep490.api.Service;

import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.model.Transaction;
import com.fpt.sep490.repository.ReceiptVoucherRepository;
import com.fpt.sep490.service.ReceiptVoucherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExtendReceiptTests {
    @Mock
    private ReceiptVoucherRepository receiptVoucherRepository;
    @Mock
    private ReceiptVoucher receiptVoucher;
    @InjectMocks
    private ReceiptVoucherServiceImpl receiptVoucherService;

    @BeforeEach
    public void setUp() throws ParseException {
        receiptVoucher = new ReceiptVoucher();
        receiptVoucher.setId(1L);
        receiptVoucher.setDueDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-01-01"));
    }

    @Test
    public void ReceiptVoucherService_ExtendReceipt_ExtendReceiptByDays() {

        when(receiptVoucherRepository.findById(1L)).thenReturn(Optional.of(receiptVoucher));
        when(receiptVoucherRepository.save(any())).thenReturn(receiptVoucher);
        ReceiptVoucher result = receiptVoucherService.extendReceipt(1L, 5, "Ngày");
        assertEquals("2024-01-06", new SimpleDateFormat("yyyy-MM-dd").format(result.getDueDate()));
        verify(receiptVoucherRepository).save(receiptVoucher);
    }

    @Test
    public void ReceiptVoucherService_ExtendReceipt_ExtendReceiptByWeeks() {
        when(receiptVoucherRepository.findById(1L)).thenReturn(Optional.of(receiptVoucher));
        when(receiptVoucherRepository.save(any())).thenReturn(receiptVoucher);

        ReceiptVoucher result = receiptVoucherService.extendReceipt(1L, 2, "Tuần");

        assertEquals("2024-01-15", new SimpleDateFormat("yyyy-MM-dd").format(result.getDueDate()));
        verify(receiptVoucherRepository).save(receiptVoucher);
    }

    @Test
    public void ReceiptVoucherService_ExtendReceipt_ExtendReceiptByMonths() {
        when(receiptVoucherRepository.findById(1L)).thenReturn(Optional.of(receiptVoucher));
        when(receiptVoucherRepository.save(any())).thenReturn(receiptVoucher);

        ReceiptVoucher result = receiptVoucherService.extendReceipt(1L, 1, "Tháng");

        assertEquals("2024-02-01", new SimpleDateFormat("yyyy-MM-dd").format(result.getDueDate()));
        verify(receiptVoucherRepository).save(receiptVoucher);
    }

    @Test
    public void ReceiptVoucherService_ExtendReceipt_ReceiptVoucherNotFound() {
        when(receiptVoucherRepository.findById(1L)).thenReturn(Optional.empty());


        Exception exception = assertThrows(RuntimeException.class, () ->
                receiptVoucherService.extendReceipt(1L, 5, "Ngày")
        );

        assertEquals("Phiếu thu không tồn tại", exception.getMessage());
    }

    @Test
    public void ReceiptVoucherService_ExtendReceipt_InvalidExtendType() {
        when(receiptVoucherRepository.findById(1L)).thenReturn(Optional.of(receiptVoucher));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                receiptVoucherService.extendReceipt(1L, 5, "Năm")
        );

        assertEquals("Loại gia hạn không hợp lệ", exception.getMessage());
    }

    @Test
    public void ReceiptVoucherService_ExtendReceipt_NegativeDaysExtension() {
        when(receiptVoucherRepository.findById(1L)).thenReturn(Optional.of(receiptVoucher));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                receiptVoucherService.extendReceipt(1L, -3, "Ngày")
        );

        assertEquals("Thời gian gia hạn không được âm hoặc bằng 0", exception.getMessage());
    }

    @Test
    public void ReceiptVoucherService_ExtendReceipt_ZeroExtension() {
        when(receiptVoucherRepository.findById(1L)).thenReturn(Optional.of(receiptVoucher));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                receiptVoucherService.extendReceipt(1L, 0, "Ngày")
        );

        assertEquals("Thời gian gia hạn không được âm hoặc bằng 0", exception.getMessage());
    }
}
