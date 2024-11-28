package com.fpt.sep490.service;

import com.fpt.sep490.dto.ReceiptVoucherDto;
import com.fpt.sep490.model.ReceiptVoucher;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface ReceiptVoucherService {
    Page<ReceiptVoucherDto> getReceiptVoucherPagination(Date startDate, Date endDate, int pageNumber, int pageSize);

    ReceiptVoucher extendReceipt(Long id, int number, String type);
}
