package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.model.ProductWarehouse;

public interface ProductWarehouseService {
    ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouse);
    ProductWarehouse createProductWarehouseFromBatchProduct(Long batchProductId);
}
