package com.fpt.sep490.service;

import com.fpt.sep490.dto.FinishedProductDto;
import com.fpt.sep490.dto.FinishedProductView;
import com.fpt.sep490.model.FinishedProduct;
import com.fpt.sep490.model.Product;
import com.fpt.sep490.repository.FinishedProductRepository;
import com.fpt.sep490.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FinishedProductServiceImpl implements FinishedProductService {
    private final ProductRepository productRepository;
    private final FinishedProductRepository finishedProductRepository;

    public FinishedProductServiceImpl(ProductRepository productRepository, FinishedProductRepository finishedProductRepository) {
        this.productRepository = productRepository;
        this.finishedProductRepository = finishedProductRepository;
    }

    @Override
    public List<FinishedProduct> getAllFinishedProduct() {
        return finishedProductRepository.findAll();
    }

    @Override
    public FinishedProduct getFinishedProductById(long id) {
        return finishedProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm đầu ra với id: " + id));
    }

    @Override
    public FinishedProduct createFinishedProduct(FinishedProductDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + dto.getProductId()));

        List<FinishedProduct> existingFinishedProducts = finishedProductRepository.findByProductId(dto.getProductId());

        double currentProportionSum = existingFinishedProducts.stream()
                .mapToDouble(FinishedProduct::getProportion)
                .sum();

        double newTotalProportion = currentProportionSum + dto.getProportion();
        if (newTotalProportion > 100) {
            throw new IllegalArgumentException("Tổng tỷ lệ của FinishedProduct vượt quá 100%.");
        } else if (newTotalProportion < 100) {
            throw new IllegalArgumentException("Tổng tỷ lệ của FinishedProduct chưa đạt 100%.");
        }

        FinishedProduct finishedProduct = new FinishedProduct();
        finishedProduct.setProportion(dto.getProportion());
        finishedProduct.setProduct(product);
        finishedProduct.setActive(true);
        return finishedProductRepository.save(finishedProduct);
    }

    @Override
    public FinishedProduct updateFinishedProduct(long id, FinishedProductDto dto, Boolean isActive) {
        FinishedProduct finishedProduct = getFinishedProductById(id);
        finishedProduct.setActive(isActive);
        finishedProduct.setProportion(dto.getProportion());
        return finishedProduct;
    }

    @Override
    public FinishedProduct deleteFinishedProduct(long id) {
        FinishedProduct finishedProduct = getFinishedProductById(id);
        finishedProductRepository.delete(finishedProduct);
        return finishedProduct;
    }

    @Override
    public Page<FinishedProductView> getPagedFinishedProducts(int page, int size, Specification<FinishedProduct> spec) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FinishedProduct> finishedProductPage = finishedProductRepository.findAll(spec, pageable);

        return finishedProductPage.map(finishedProduct -> {
            List<FinishedProductView.FinishedProductDetail> details = List.of(
                    FinishedProductView.FinishedProductDetail.builder()
                            .finishedProductName(finishedProduct.getProduct().getName())
                            .proportion(finishedProduct.getProportion())
                            .build()
            );

            return FinishedProductView.builder()
                    .productName(finishedProduct.getProduct().getName())
                    .finishedProducts(details)
                    .build();
        });
    }

    @Override
    public boolean CheckSumProductProportion(long productId) {
        List<FinishedProduct> finishedProducts = finishedProductRepository.findByProductId(productId);

        double totalProportion = finishedProducts.stream()
                .mapToDouble(FinishedProduct::getProportion)
                .sum();

        return totalProportion == 100.0;
    }
}
