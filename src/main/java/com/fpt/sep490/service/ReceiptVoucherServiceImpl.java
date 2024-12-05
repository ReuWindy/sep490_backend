package com.fpt.sep490.service;

import com.fpt.sep490.dto.ReceiptVoucherDto;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.repository.ReceiptVoucherRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceiptVoucherServiceImpl implements ReceiptVoucherService {

    private final ReceiptVoucherRepository receiptVoucherRepository;

    public ReceiptVoucherServiceImpl(ReceiptVoucherRepository receiptVoucherRepository) {
        this.receiptVoucherRepository = receiptVoucherRepository;
    }

    @Override
    public Page<ReceiptVoucherDto> getReceiptVoucherPagination(Date startDate, Date endDate, int pageNumber, int pageSize, String incomeCode) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "receiptDate"));
            Specification<ReceiptVoucher> specification = ReceiptVoucherSpecification.isReceiptDateBetween(startDate, endDate, incomeCode);

            Page<ReceiptVoucher> receipVoucherPage = receiptVoucherRepository.findAll(specification, pageable);

            List<ReceiptVoucherDto> dtos = receipVoucherPage.getContent().stream()
                    .map(ReceiptVoucherDto::toDto)
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, receipVoucherPage.getTotalElements());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ReceiptVoucher extendReceipt(Long id, int number, String type) {
        ReceiptVoucher receiptVoucher = receiptVoucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phiếu thu không tồn tại"));
        Date extendDate = receiptVoucher.getDueDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(extendDate);
        if (number <= 0) {
            throw new IllegalArgumentException("Thời gian gia hạn không được âm hoặc bằng 0");
        }
        if (type.equalsIgnoreCase("Ngày")) {
            calendar.add(Calendar.DAY_OF_MONTH, number);
        } else if (type.equalsIgnoreCase("Tuần")) {
            calendar.add(Calendar.WEEK_OF_YEAR, number);
        } else if (type.equalsIgnoreCase("Tháng")) {
            calendar.add(Calendar.MONTH, number);
        } else {
            throw new IllegalArgumentException("Loại gia hạn không hợp lệ");
        }
        extendDate = calendar.getTime();
        receiptVoucher.setDueDate(extendDate);
        try {
            return receiptVoucherRepository.save(receiptVoucher);
        }catch (Exception e){
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình gia hạn phiếu! "+e.getMessage());
        }
    }
}
