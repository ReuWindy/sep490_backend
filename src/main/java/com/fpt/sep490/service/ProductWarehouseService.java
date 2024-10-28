package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.model.ProductWarehouse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductWarehouseService {
    List<ProductWarehouse> getAll();

    ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouse);
    ProductWarehouse createProductWarehouseFromBatchProduct(Long batchProductId);
    Page<ProductWarehouse> getPageProductWarehouseByFilter(double minPrice, double maxPrice, String unit, double weightPerUnit, int categoryId, int supplierId, int warehouseId, String sortDirection, String priceOrder, int pageNumber, int pageSize);
}
