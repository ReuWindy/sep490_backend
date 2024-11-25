package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductionCompleteDto;
import com.fpt.sep490.dto.ProductionFinishDto;
import com.fpt.sep490.dto.ProductionOrderDto;
import com.fpt.sep490.dto.ProductionOrderView;
import com.fpt.sep490.model.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface ProductionOrderService {
    ProductionOrder createProductionOrder(ProductionOrderDto dto);

    ProductionOrder finishProductionOrder(List<ProductionFinishDto> dto, Long id);

    List<ProductionOrder> getAllProductionOrders();

    ProductionOrderView getProductionOrderById(Long id);

    ProductionOrder updateProductionOrder(Long id, ProductionOrderDto dto);

    ProductionOrder deleteProductionOrder(Long id);

    void cancelProductionOrder(Long id);

    void confirmProductionOrder(Long id);

    void ConfirmProductionOrderDone(Long id, List<ProductionCompleteDto> dto);

    Page<ProductionOrderView> getProductionOrders(
            String status,
            Date startDate,
            Date endDate,
            String productName,
            Pageable pageable);
}
