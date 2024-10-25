package com.fpt.sep490.service;

import com.fpt.sep490.dto.PriceRequestDto;
import com.fpt.sep490.model.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceService {
    List<Price> findAllPrices();

    Page<Price> getPriceByFilter(String name, int pageNumber, int pageSize);
    Price AddPrice(PriceRequestDto request);
}
