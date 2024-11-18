package com.fpt.sep490.service;

import com.fpt.sep490.dto.ImportProductionDto;
import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.model.ProductWarehouse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductWarehouseService {
    List<ProductWarehouse> getAll();
    ProductWarehouse getById(long id);
    List<ProductWarehouse> getAllProducts();
    List<ProductWarehouse> getAllIngredients();
    ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouse);
    ProductWarehouse createProductWarehouseFromBatchProduct(Long batchProductId);
    Page<ProductWarehouse> getPageProductWarehouseByFilter(double minPrice, double maxPrice, String unit, double weightPerUnit, int categoryId, int supplierId, int warehouseId, String sortDirection, String priceOrder, int pageNumber, int pageSize);

    void importProductWarehouseToProduction(long productionId, ImportProductionDto dto);
    void exportProductWarehouseToProduction(long productWarehouseId,int quantity);
}
