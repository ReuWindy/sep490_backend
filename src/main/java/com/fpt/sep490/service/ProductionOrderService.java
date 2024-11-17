package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductionOrderDto;
import com.fpt.sep490.model.ProductionOrder;

public interface ProductionOrderService {
    ProductionOrder createProductionOrder(ProductionOrderDto dto);
}
