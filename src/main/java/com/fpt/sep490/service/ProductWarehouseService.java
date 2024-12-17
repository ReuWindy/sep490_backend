package com.fpt.sep490.service;

import com.fpt.sep490.dto.AdminProductDto;
import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.dto.ProductionCompleteDto;
import com.fpt.sep490.model.ProductWarehouse;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface ProductWarehouseService {
    List<ProductWarehouse> getAll();

    ProductWarehouse getById(long id);

    List<ProductWarehouseDto> getAllProducts();

    Page<ProductWarehouseDto> getProductWarehousesByFilter(String productCode, String productName, Long categoryId, Long supplierId, Long warehouseId, int pageNumber, int pageSize);

    List<ProductWarehouseDto> getAllIngredients();

    ProductWarehouse createProductWarehouseFromBatchProduct(Long batchProductId);

    void importProductWarehouseToProduction(List<ProductionCompleteDto> dtos);

    void exportProductWarehouseToProduction(long productWarehouseId, int quantity);
}
