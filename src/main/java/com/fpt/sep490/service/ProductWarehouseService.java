package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.model.ProductWarehouse;

import java.util.List;

public interface ProductWarehouseService {
    List<ProductWarehouse> getAll();
    List<ProductWarehouse> getAll();
    ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouse);
    ProductWarehouse createProductWarehouseFromBatchProduct(Long batchProductId);
}
