package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.dto.ProductionCompleteDto;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductWarehouseServiceImpl implements ProductWarehouseService {
    private final BatchProductRepository batchProductRepository;
    private final ProductWareHouseRepository productWareHouseRepository;

    public ProductWarehouseServiceImpl(BatchProductRepository batchProductRepository, ProductWareHouseRepository productWareHouseRepository) {
        this.batchProductRepository = batchProductRepository;
        this.productWareHouseRepository = productWareHouseRepository;
    }

    @Override
    public List<ProductWarehouse> getAll() {
        return productWareHouseRepository.findAll();
    }

    @Override
    public ProductWarehouse getById(long id) {
        return productWareHouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("không tìm thấy với id " + id));
    }

    @Override
    public List<ProductWarehouseDto> getAllProducts() {
        Long id = 2L;
        List<ProductWarehouseDto> productWarehouseDto = new ArrayList<>();
        List<ProductWarehouse> productWarehouses = productWareHouseRepository.findProductWarehousesByWarehouseId(id);
        for (ProductWarehouse pw : productWarehouses) {
            productWarehouseDto.add(ProductWarehouseDto.toDto(pw));
        }
        return productWarehouseDto;
    }

    @Override
    public List<ProductWarehouseDto> getAllIngredients() {
        Long id = 1L;
        List<ProductWarehouseDto> productWarehouseDto = new ArrayList<>();
        List<ProductWarehouse> productWarehouses = productWareHouseRepository.findProductWarehousesByWarehouseId(id);
        for (ProductWarehouse pw : productWarehouses) {
            productWarehouseDto.add(ProductWarehouseDto.toDto(pw));
        }
        return productWarehouseDto;
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
        return productWarehouse;
    }

    @Override
    public void importProductWarehouseToProduction(List<ProductionCompleteDto> dtos) {
        for (ProductionCompleteDto dto : dtos) {
            ProductWarehouse productWarehouse = productWareHouseRepository.findById(dto.getProductWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            productWarehouse.setQuantity(productWarehouse.getQuantity() + dto.getQuantity());
            productWarehouse.setWeight(productWarehouse.getQuantity() * productWarehouse.getWeightPerUnit());
            productWareHouseRepository.save(productWarehouse);
        }
    }

    @Override
    public void exportProductWarehouseToProduction(long productWarehouseId, int quantity) {
        ProductWarehouse productWarehouse = productWareHouseRepository.findById(productWarehouseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong kho với Id: " + productWarehouseId));

        if (productWarehouse.getQuantity() < quantity) {
            throw new IllegalArgumentException("Số lượng xuất vượt quá số lượng tồn kho! Số lượng hiện tại: "
                    + productWarehouse.getQuantity() + ", số lượng yêu cầu: " + quantity);
        }

        productWarehouse.setQuantity(productWarehouse.getQuantity() - quantity);

        productWareHouseRepository.save(productWarehouse);
    }
}
