package com.fpt.sep490.service;

import com.fpt.sep490.dto.ImportProductionDto;
import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.dto.ProductionCompleteDto;
import com.fpt.sep490.model.ProductWarehouse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductWarehouseService {
    List<ProductWarehouse> getAll();
    ProductWarehouse getById(long id);
    List<ProductWarehouseDto> getAllProducts();
    List<ProductWarehouseDto> getAllIngredients();
    ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouse);
    ProductWarehouse createProductWarehouseFromBatchProduct(Long batchProductId);
    Page<ProductWarehouse> getPageProductWarehouseByFilter(double minPrice, double maxPrice, String unit, double weightPerUnit, int categoryId, int supplierId, int warehouseId, String sortDirection, String priceOrder, int pageNumber, int pageSize);

    void importProductWarehouseToProduction(List<ProductionCompleteDto> dtos);
    void exportProductWarehouseToProduction(long productWarehouseId,int quantity);
}
