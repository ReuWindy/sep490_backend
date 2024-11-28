package com.fpt.sep490.service;

import com.fpt.sep490.dto.DiscountDto;
import com.fpt.sep490.model.Discount;
import com.fpt.sep490.repository.DiscountRepository;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public Discount getDiscountById(int id) {
        return discountRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    @Override
    public Discount createDiscount(DiscountDto discountDto) {
        Discount newDiscount = new Discount();
        newDiscount.setDescription(discountDto.getDescription());
        newDiscount.setAmountPerUnit(discountDto.getAmountPerUnit());
        newDiscount.setStartDate(discountDto.getStartDate());
        newDiscount.setEndDate(discountDto.getEndDate());
        discountRepository.save(newDiscount);
        return newDiscount;
    }

    @Override
    public Discount updateDiscount(Discount discount) {
        Discount updatedDiscount = new Discount();
        updatedDiscount.setDescription(discount.getDescription());
        updatedDiscount.setAmountPerUnit(discount.getAmountPerUnit());
        updatedDiscount.setStartDate(discount.getStartDate());
        updatedDiscount.setEndDate(discount.getEndDate());
        discountRepository.save(updatedDiscount);
        return updatedDiscount;
    }

    @Override
    public Discount disableDiscount(int id) {
        Discount discount = getDiscountById(id);
        discount.setActive(true);
        discountRepository.save(discount);
        return discount;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void AutoDisableExpiredDiscount() {
        List<Discount> discounts = discountRepository.findActiveDiscountsWithEndDateBefore(LocalDateTime.now());
        discounts.forEach(discount -> discount.setActive(false));
        discountRepository.saveAll(discounts);
    }

    @Override
    public Page<Discount> getDiscountByFilter(int supplierId, String customerName, int productId, LocalDateTime startDate, LocalDateTime endDate, int pageNumber, int pageSize) {
        return null;
    }
}
