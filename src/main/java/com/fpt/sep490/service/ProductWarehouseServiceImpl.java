package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
import com.fpt.sep490.repository.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductWarehouseServiceImpl implements ProductWarehouseService {
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final BatchProductRepository batchProductRepository;
    private final ProductWareHouseRepository productWareHouseRepository;

    public ProductWarehouseServiceImpl(ProductRepository productRepository, WarehouseRepository warehouseRepository, BatchProductRepository batchProductRepository, ProductWareHouseRepository productWareHouseRepository) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.batchProductRepository = batchProductRepository;
        this.productWareHouseRepository = productWareHouseRepository;
    }


    @Override
    public List<ProductWarehouse> getAll() {
        return productWareHouseRepository.findAll();
    }

    @Override
    public ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouse) {
        return null;
    }

    @Override
    public ProductWarehouse createProductWarehouseFromBatchProduct(Long batchProductId) {
        BatchProduct batchProduct = batchProductRepository.findById(batchProductId).orElseThrow(() -> new RuntimeException("BatchProduct không tìm thấy với id: " + batchProductId));

        ProductWarehouse productWarehouse = new ProductWarehouse();

        productWarehouse.setQuantity(batchProduct.getQuantity());
        productWarehouse.setImportPrice(batchProduct.getPrice());
        productWarehouse.setWeight(batchProduct.getWeight());
        productWarehouse.setUnit(batchProduct.getUnit());
        productWarehouse.setProduct(batchProduct.getProduct());

//        long warehouseId = batchProduct.getBatch().getWarehouse().getId();
//        Warehouse defaultWarehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new RuntimeException("Warehouse không tìm thấy với id: " + warehouseId ));
//        productWarehouse.setWarehouse(defaultWarehouse);
//        productWarehouse.setBatchCode(batchProduct.getBatch().getBatchCode());
//        productWareHouseRepository.save(productWarehouse);
        return productWarehouse;
    }

    @Override
    public Page<ProductWarehouse> getPageProductWarehouseByFilter(double minPrice, double maxPrice, String unit, double weightPerUnit, int categoryId, int supplierId, int warehouseId, String sortDirection, String priceOrder, int pageNumber, int pageSize) {
        return null;
    }

//    @Override
//    public Page<ProductWarehouse> getPageProductWarehouseByFilter(double minPrice, double maxPrice, String unit,
//                                                                  double weightPerUnit, int categoryId, int supplierId,
//                                                                  int warehouseId, String sortDirection, String priceOrder,
//                                                                  int pageNumber, int pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
//        Specification<ProductWarehouse> spec = ProductWareHouseSpecification.hasUnitOrHasWeightPerUnitOrCategoryOrSupplierOrWarehouse(unit, weightPerUnit, categoryId, supplierId, warehouseId, sortDirection, priceOrder);
//        return productWareHouseRepository.findAll(spec, pageable);
//    }



}
