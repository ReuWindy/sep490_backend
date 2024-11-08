package com.fpt.sep490.service;

import com.fpt.sep490.dto.*;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.Product;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(int id);
    Product createProduct(ProductDto productDto);
    Product updateProduct(ProductDto productDto);
    List<Product> getProductsByWarehouse(Long warehouseId);
    void updateProductStatus(String productCode);
    Page<AdminProductDto> getProductByFilterForAdmin(String productCode, String productName, String batchCode, Date importDate, String productQuantity, String sortDirection, String priceOrder, int pageNumber, int pageSize);
    Page<ProductDto> getProductByFilterForCustomer(String productCode,String categoryName, String supplierName, int pageNumber, int pageSize);
    Product createCustomerProduct(ProductDto productDto);

    List<BatchProduct> previewBatchProducts(List<importProductDto> ImportProductDtoList);
    String confirmAndAddSelectedProductToWarehouse(long batchId, List<BatchProductSelection> batchProductSelections);
    String confirmAndExportProducts(Long batchId, List<ExportProductDto> exportProductDtos);
    String prepareExportProduct(List<ExportProductDto> exportProductDtoList);
}
