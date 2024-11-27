package com.fpt.sep490.service;

import com.fpt.sep490.dto.FinishedProductDto;
import com.fpt.sep490.dto.FinishedProductView;
import com.fpt.sep490.model.FinishedProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface FinishedProductService {
    List<FinishedProduct> getAllFinishedProduct();

    FinishedProduct getFinishedProductById(long id);

    Page<FinishedProductView> getPagedFinishedProducts(int page, int size, Specification<FinishedProduct> spec);

    FinishedProduct createFinishedProduct(FinishedProductDto dto);

    FinishedProduct updateFinishedProduct(long id, FinishedProductDto dto, Boolean isActive);

    FinishedProduct deleteFinishedProduct(long id);

    boolean CheckSumProductProportion(long productId);
}
