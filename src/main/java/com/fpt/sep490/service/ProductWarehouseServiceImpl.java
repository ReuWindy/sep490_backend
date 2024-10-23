package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.model.Warehouse;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
import com.fpt.sep490.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouseDto) {
        ProductWarehouse productWarehouse = new ProductWarehouse();
        productWarehouse.setProduct(productRepository.findById(productWarehouseDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found")));
        productWarehouse.setWarehouse(warehouseRepository.findById(productWarehouseDto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found")));
        return productWarehouse;
    }

    @Override
    public ProductWarehouse createProductWarehouseFromBatchProduct(Long batchProductId) {
        BatchProduct batchProduct = batchProductRepository.findById(batchProductId).orElseThrow(() -> new RuntimeException("BatchProduct không tìm thấy với id: " + batchProductId));

        ProductWarehouse productWarehouse = new ProductWarehouse();

        productWarehouse.setQuantity(batchProduct.getQuantity());
        productWarehouse.setPrice(batchProduct.getPrice());
        productWarehouse.setWeight(batchProduct.getWeight());
        productWarehouse.setUnit(batchProduct.getUnit());
        productWarehouse.setDescription(batchProduct.getDescription());
        productWarehouse.setProduct(batchProduct.getProduct());

        long warehouseId = batchProduct.getBatch().getWarehouse().getId();
        Warehouse defaultWarehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new RuntimeException("Warehouse không tìm thấy với id: " + warehouseId ));
        productWarehouse.setWarehouse(defaultWarehouse);
        productWarehouse.setBatchCode(batchProduct.getBatch().getBatchCode());
        productWareHouseRepository.save(productWarehouse);
        return productWarehouse;
    }
}
