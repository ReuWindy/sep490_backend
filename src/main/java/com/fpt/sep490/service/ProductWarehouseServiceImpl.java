package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductWarehouseServiceImpl implements ProductWarehouseService {
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    public ProductWarehouseServiceImpl(ProductRepository productRepository, WarehouseRepository warehouseRepository) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouseDto) {
        ProductWarehouse productWarehouse = new ProductWarehouse();
        productWarehouse.setProduct(productRepository.findById(productWarehouseDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found")));
        productWarehouse.setQuantity(productWarehouseDto.getQuantity());
        productWarehouse.setWarehouse(warehouseRepository.findById(productWarehouseDto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found")));
        return productWarehouse;
    }
}
