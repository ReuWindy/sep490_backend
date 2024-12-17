package com.fpt.sep490.service;

import com.fpt.sep490.dto.*;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.Product;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    List<Product> getAllBatchProducts(String batchCode);

    Product getProductById(int id);

    Product createProduct(ProductDto productDto);

    Product updateProduct(ProductDto productDto);

    List<Product> getProductsByWarehouse(Long warehouseId);

    void updateProductStatus(String productCode);

    Page<AdminProductDto> getProductByFilterForAdmin(String productCode, String productName, Long categoryId, Long supplierId, String batchCode, Long warehouseId, Date importDate, String productQuantity, String sortDirection, String priceOrder, int pageNumber, int pageSize);

    Page<ProductDto> getProductByFilterForCustomer(String name, String productCode, String categoryName, String supplierName, String username, int pageNumber, int pageSize);

    Page<ProductDto> getProductByFilterForCustomer(String name, String productCode, String categoryName, String supplierName, Long id, int pageNumber, int pageSize);

    Product createCustomerProduct(ProductDto productDto);

    List<BatchProduct> previewBatchProducts(List<importProductDto> ImportProductDtoList);

    List<BatchProduct> previewBatchProductsFromProduction(List<importProductFromProductionDto> ImportProductDtoList);

    String confirmAndAddSelectedProductToWarehouse(long batchId, List<BatchProductSelection> batchProductSelections);

    String confirmAndExportProducts(Long batchId, List<ExportProductDto> exportProductDtos);

    List<BatchProduct> prepareExportProduct(List<ExportProductDto> exportProductDtoList);

    Product disableProduct(Long id);

    Product enableProduct(Long id);

    List<importProductDto> readExcelFile(MultipartFile file);

    List<ExportProductDto> readExcelFileExport(MultipartFile file);

    void createExcelTemplate(HttpServletResponse response) throws IOException;

    void createExcelTemplateExport(HttpServletResponse response) throws IOException;
}