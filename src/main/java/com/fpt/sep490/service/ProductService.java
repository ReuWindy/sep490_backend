package com.fpt.sep490.service;

import com.fpt.sep490.dto.AdminProductDto;
import com.fpt.sep490.dto.ExportProductDto;
import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.dto.importProductDto;
import com.fpt.sep490.model.Product;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(int id);
    Product createProduct(ProductDto productDto);
    Product updateProduct(long id, ProductDto productDto);
    String importProductToBatch(List<importProductDto> ImportProductDtoList);
    String exportProduct(List<ExportProductDto> ExportProductDtoList);
    List<Product> getProductsByWarehouse(Long warehouseId);
    void updateProductStatus(String productCode);
    Page<AdminProductDto> getProductByFilterForAdmin(String productCode, String productName, String batchCode, Date importDate, String productQuantity, String sortDirection, String priceOrder, int pageNumber, int pageSize);
    Page<ProductDto> getProductByFilterForCustomer(String productCode,String categoryName, String supplierName, int pageNumber, int pageSize);
}
