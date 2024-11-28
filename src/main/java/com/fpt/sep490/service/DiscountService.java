package com.fpt.sep490.service;

import com.fpt.sep490.dto.DiscountDto;
import com.fpt.sep490.model.Discount;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface DiscountService {
    List<Discount> getAllDiscounts();

    Discount getDiscountById(int id);

    Discount createDiscount(DiscountDto discountDto);

    Discount updateDiscount(Discount discount);

    Discount disableDiscount(int id);

    void AutoDisableExpiredDiscount();

    Page<Discount> getDiscountByFilter(int supplierId, String customerName, int productId, LocalDateTime startDate, LocalDateTime endDate, int pageNumber, int pageSize);
}
