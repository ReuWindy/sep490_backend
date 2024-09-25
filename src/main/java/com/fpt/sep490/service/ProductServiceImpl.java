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

        // Tìm supplier theo id
        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // Tìm đơn vị đo lường (unitOfMeasure) theo id
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(productDto.getUnitOfMeasureId())
                .orElseThrow(() -> new RuntimeException("Unit of Measure not found"));

        product.setSupplier(supplier);
        product.setUnitOfMeasure(unitOfMeasure);

        // Lưu product trước để có id
        Product savedProduct = productRepository.save(product);

        // Nếu có warehouseId trong ProductDto, tạo ProductWarehouse
        if (productDto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(productDto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found"));

            // Tạo ProductWarehouse và gán thông tin
            ProductWarehouse productWarehouse = new ProductWarehouse();
            productWarehouse.setProduct(savedProduct);
            productWarehouse.setWarehouse(warehouse);
            productWarehouse.setQuantity(0);  // Gán số lượng ban đầu, có thể cập nhật sau

            // Lưu ProductWarehouse
            productWareHouseRepository.save(productWarehouse);
        }

        return savedProduct; // Trả về product sau khi đã lưu và tạo ProductWarehouse
    }

    public List<Product> getProductsByWarehouse(Long warehouseId) {
        return productWareHouseRepository.findProductsByWarehouseId(warehouseId);
    }
    @Override
    public Product updateProduct(long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(productDto.getUnitOfMeasureId())
                .orElseThrow(() -> new RuntimeException("Unit of Measure not found"));

        product.setSupplier(supplier);
        product.setUnitOfMeasure(unitOfMeasure);

        return productRepository.save(product);
    }

    @Override
    public double getTotalProductQuantity(long productId) {
        List<ProductWarehouse> productWarehouses = productWareHouseRepository.findByProductId(productId);
        return productWarehouses.stream().mapToDouble(ProductWarehouse::getQuantity).sum();
    }

    private ProductDto toDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setImage(product.getImage());
        productDto.setProductCode(product.getProductCode());
        productDto.setSupplierId(product.getSupplier().getId());
        productDto.setUnitOfMeasureId(product.getUnitOfMeasure().getId());
        productDto.setCreateAt(product.getCreateAt());
        productDto.setUpdateAt(product.getUpdateAt());
        productDto.setIsDeleted(product.getIsDeleted());
        return productDto;
    }

    private Product toModel(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());
        product.setProductCode(productDto.getProductCode());

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(productDto.getUnitOfMeasureId())
                .orElseThrow(() -> new RuntimeException("Unit of Measure not found"));

        product.setSupplier(supplier);
        product.setUnitOfMeasure(unitOfMeasure);

        return product;
    }
}
