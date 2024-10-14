package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.utils.RandomProductCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final ProductWareHouseRepository productWareHouseRepository;
    private final WarehouseRepository warehouseRepository;

    public ProductServiceImpl(ProductRepository productRepository, SupplierRepository supplierRepository, UnitOfMeasureRepository unitOfMeasureRepository, ProductWareHouseRepository productWareHouseRepository1, WarehouseRepository warehouseRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.productWareHouseRepository = productWareHouseRepository1;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(int id) {
        Optional<Product> p = productRepository.findById((long) id);
        return p.orElse(null);
    }

    @Override
    public Product createProduct(ProductDto productDto) {
        // Tạo đối tượng Product từ ProductDto
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());
        product.setProductCode(RandomProductCodeGenerator.generateProductCode());
        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }

    public List<Product> getProductsByWarehouse(Long warehouseId) {
        return productWareHouseRepository.findProductsByWarehouseId(warehouseId);
    }

    @Override
    public void updateProductStatus(String productCode) {
        Optional<Product> product = productRepository.findByProductCode(productCode);
        if(product.isPresent()) {
            product.get().setUpdateAt(new Date());
            product.get().setIsDeleted(!product.get().getIsDeleted());
        }
    }

    @Override
    public Product updateProduct(long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());

        return productRepository.save(product);
    }
}
