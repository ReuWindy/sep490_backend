package com.fpt.sep490.service;

import com.fpt.sep490.dto.ReceiptVoucherDto;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.ReceiptVoucher;
import com.fpt.sep490.repository.ReceiptVoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReceiptVoucherServiceImpl implements ReceiptVoucherService {

    private final ReceiptVoucherRepository receiptVoucherRepository;

    public ReceiptVoucherServiceImpl(ReceiptVoucherRepository receiptVoucherRepository) {
        this.receiptVoucherRepository = receiptVoucherRepository;
    }

    @Override
    public Page<ReceiptVoucherDto> getReceiptVoucherPagination(Date startDate, Date endDate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<ReceiptVoucher> specification = ReceiptVoucherSpecification.isReceiptDateBetween(startDate, endDate);

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
    public ReceiptVoucher extendReceipt(Long id, Date extendDate) {
        ReceiptVoucher receiptVoucher = receiptVoucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phiếu thu không tồn tại"));

        receiptVoucher.setDueDate(extendDate);
        return receiptVoucherRepository.save(receiptVoucher);
    }
}
