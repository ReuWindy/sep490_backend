package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerPriceDto;
import com.fpt.sep490.dto.PriceRequestDto;
import com.fpt.sep490.dto.ProductPriceDto;
import com.fpt.sep490.dto.ProductPriceRequestDto;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.Price;
import com.fpt.sep490.model.ProductPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceService {
    List<Price> findAllPrices();

    Page<Price> getPriceByFilter(String name, int pageNumber, int pageSize);
    Price UpdatePrice(PriceRequestDto request);
    Price AddPrice(PriceRequestDto request);
    List<Customer> updateCustomerPrice(CustomerPriceDto customerPriceDto);

    List<ProductPrice> updateProductPrice(ProductPriceRequestDto productPriceDto);

    void deletePrice(long priceId);
}
