package com.fpt.sep490.service;

import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(int id);
    Product createProduct(ProductDto productDto);
    Product updateProduct(long id, ProductDto productDto);
    List<Product> getProductsByWarehouse(Long warehouseId);
    void updateProductStatus(String productCode);
}
