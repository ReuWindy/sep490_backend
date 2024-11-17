package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductionOrderDto;
import com.fpt.sep490.model.ProductionOrder;
import com.fpt.sep490.utils.RandomProductionOrderCodeGenerator;
import org.springframework.stereotype.Service;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {
    @Override
    public ProductionOrder createProductionOrder(ProductionOrderDto dto) {
        return null;
    }
}
