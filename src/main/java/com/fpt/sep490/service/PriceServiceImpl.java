package com.fpt.sep490.service;

import com.fpt.sep490.dto.PriceRequestDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.CustomerRepository;
import com.fpt.sep490.repository.PriceRepository;
import com.fpt.sep490.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService{

    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public PriceServiceImpl(PriceRepository priceRepository, ProductRepository productRepository,CustomerRepository customerRepository){
        this.priceRepository = priceRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }
    @Override
    public List<Price> findAllPrices() {
        return priceRepository.findAll();
    }

    @Override
    public Page<Price> getPriceByFilter(String name, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Specification<Price> specification = PriceSpecification.hasName( name);
        return priceRepository.findAll(specification, pageable);
    }

    @Override
    public Price AddPrice(PriceRequestDto request) {
        Price price = Price.builder()
                .name(request.getName())
                .productPrices(new HashSet<>())
                .build();
        for (Long productId : request.getProductIds()){
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
            ProductPrice productPrice = ProductPrice.builder()
                    .unit_price(request.getUnitPrice())
                    .product(product)
                    .price(price)
                    .build();
            price.getProductPrices().add(productPrice);
        }
        Price createdPrice = priceRepository.save(price);
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(()->new RuntimeException("Customer Not Found"));
        customer.setPrice(createdPrice);
        customerRepository.save(customer);
        return  createdPrice;
    }
}
