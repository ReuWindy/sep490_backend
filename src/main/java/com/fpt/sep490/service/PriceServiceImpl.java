package com.fpt.sep490.service;

import com.fpt.sep490.dto.CustomerPriceDto;
import com.fpt.sep490.dto.PriceRequestDto;
import com.fpt.sep490.dto.ProductPriceDto;
import com.fpt.sep490.dto.ProductPriceRequestDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.CustomerRepository;
import com.fpt.sep490.repository.PriceRepository;
import com.fpt.sep490.repository.ProductPriceRepository;
import com.fpt.sep490.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class PriceServiceImpl implements PriceService{

    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ProductPriceRepository productPriceRepository;

    public PriceServiceImpl(PriceRepository priceRepository, ProductRepository productRepository,CustomerRepository customerRepository,ProductPriceRepository productPriceRepository){
        this.priceRepository = priceRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.productPriceRepository = productPriceRepository;
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
        try{
            return priceRepository.save(price);
        }catch (Exception e){
            throw new RuntimeException("Xảy ra lỗi trong quá trình tạo giá mới!");
        }
    }

    @Override
    public Customer updateCustomerPrice(CustomerPriceDto customerPriceDto) {
        Price price = priceRepository.findById(customerPriceDto.getPriceId()).orElseThrow(()-> new RuntimeException("Không tìm thấy giá phù hợp!"));
        Customer customer = customerRepository.findById(customerPriceDto.getCustomerIds()).orElseThrow(()->new RuntimeException("Không tìm thấy khách hàng phù hợp!"));
        customer.setPrice(price);
        try {
            customerRepository.save(customer);
            return customer;
        }catch (Exception e){
            throw new RuntimeException("Xảy ra lỗi trong quá trình cập nhật khách hàng!");
        }
    }

    @Override
    public List<ProductPrice> updateProductPrice(ProductPriceRequestDto productPriceDto) {
          List<ProductPrice> updatedProductPriceDto = new ArrayList<>();
          for(ProductPriceDto request : productPriceDto.getProductPrice()){
              Product updatedProduct = productRepository.findById(request.getProductId()).orElseThrow(()->new RuntimeException("Không tìm thấy sản phẩm đang được cập nhật!"));
              Price updatedPrice = priceRepository.findById(request.getPriceId()).orElseThrow(()->new RuntimeException("Không tìm thấy giá đang được cập nhật!"));
              double updateUnitPrice = request.getUnitPrice();
              if(updateUnitPrice <=0){
                  throw new RuntimeException("Giá thiết lập riêng không được trống hay là số âm !");
              }
              Optional<ProductPrice> existingProductPrice = productPriceRepository.findByPriceIdAndProductId(request.getPriceId(), request.getProductId());
              if(existingProductPrice.isPresent()){
                  ProductPrice productPrice = existingProductPrice.get();
                  productPrice.setUnit_price(updateUnitPrice);
                  updatedProductPriceDto.add(productPriceRepository.save(productPrice));
              }else{
                  ProductPrice productPrice = new ProductPrice();
                  productPrice.setUnit_price(updateUnitPrice);
                  productPrice.setProduct(updatedProduct);
                  productPrice.setPrice(updatedPrice);
                  updatedProductPriceDto.add(productPriceRepository.save(productPrice));
              }
          }
          return updatedProductPriceDto;
    }

    @Override
    public void deletePrice(long priceId) {
        Price price = priceRepository.findById(priceId).orElseThrow(()-> new RuntimeException("Không tìm thấy giá phù hợp!"));

        productPriceRepository.deleteByPriceId(priceId);

        customerRepository.updatePriceIdForCustomers(priceId, 1L);

        priceRepository.deleteById(priceId);
    }
}
