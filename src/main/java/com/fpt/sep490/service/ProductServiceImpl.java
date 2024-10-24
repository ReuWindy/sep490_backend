package com.fpt.sep490.service;

import com.fpt.sep490.dto.AdminProductDto;
import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.dto.UnitWeightPairs;
import com.fpt.sep490.dto.importProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.utils.RandomBatchCodeGenerator;
import com.fpt.sep490.utils.RandomProductCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final ProductWareHouseRepository productWareHouseRepository;
    private final WarehouseRepository warehouseRepository;
    private final CategoryRepository categoryRepository;
    private final BatchRepository batchRepository;
    private final BatchProductRepository batchProductRepository;

    private final WarehouseReceiptService warehouseReceiptService;
    private final UserService userService;


    public ProductServiceImpl(ProductRepository productRepository, SupplierRepository supplierRepository, UnitOfMeasureRepository unitOfMeasureRepository, ProductWareHouseRepository productWareHouseRepository, WarehouseRepository warehouseRepository, CategoryRepository categoryRepository, BatchRepository batchRepository, BatchProductRepository batchProductRepository, WarehouseReceiptService warehouseReceiptService, UserService userService) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.productWareHouseRepository = productWareHouseRepository;
        this.warehouseRepository = warehouseRepository;
        this.categoryRepository = categoryRepository;
        this.batchRepository = batchRepository;
        this.batchProductRepository = batchProductRepository;
        this.warehouseReceiptService = warehouseReceiptService;
        this.userService = userService;
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
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());
        product.setProductCode(RandomProductCodeGenerator.generateProductCode());

        Category category = categoryRepository.findById(Long.valueOf(productDto.getCategoryId()))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(productDto.getUnitOfMeasureId())
                .orElseThrow(() -> new RuntimeException("Unit of Measure not found"));

        product.setCategory(category);
        product.setSupplier(supplier);
        product.setUnitOfMeasure(unitOfMeasure);

        Product savedProduct = productRepository.save(product);

        if (productDto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(productDto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found"));

            ProductWarehouse productWarehouse = new ProductWarehouse();
            productWarehouse.setProduct(savedProduct);
            productWarehouse.setWarehouse(warehouse);
            product.setCreateAt(new Date());
            product.setIsDeleted(false);

            productWareHouseRepository.save(productWarehouse);
        }

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
    public Page<AdminProductDto> getProductByFilterForAdmin(String productCode, String productName, String batchCode, Date importDate, String productQuantity, String sortDirection, String priceOrder, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        ProductSpecification productSpecs = new ProductSpecification();
        Specification<Product> specification = productSpecs.hasProductCodeOrProductNameOrBatchCodeOrImportDate(productCode, productName, batchCode, importDate, priceOrder, sortDirection);
        Page<Product> products = productRepository.findAll(specification, pageable);
        return products.map(this::convertToAdminProductDto);
    }

    @Override
    public Page<ProductDto> getProductByFilterForCustomer(String productCode, String categoryName, String supplierName, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1,pageSize);
        ProductSpecification productSpecification = new ProductSpecification();
        Specification<Product> specification = productSpecification.hasProductCodeOrCategoryNameOrSupplierName(productCode, categoryName, supplierName);
        Page<Product> products = productRepository.findAll(specification, pageable);
        return products.map(this::toProductDto);
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
    public void importProductToBatch(List<importProductDto> ImportProductDtoList) {
        Batch batch = new Batch();
        batch.setBatchStatus("OK");
        Date importDate = new Date();
        batch.setImportDate(importDate);
        batch.setBatchCode(RandomBatchCodeGenerator.generateBatchCode());

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        batch.setBatchCreator(user);

        batch = batchRepository.save(batch);

        Set<BatchProduct> batchProducts = new HashSet<>();

        for(importProductDto dto : ImportProductDtoList) {
            Product product = new Product();
            product.setName(dto.getName());
            product.setProductCode(RandomProductCodeGenerator.generateProductCode());
            product.setDescription(dto.getDescription());
            product.setImportPrice(dto.getImportPrice());
            product.setImage(dto.getImage());
            product.setProductCode(RandomProductCodeGenerator.generateProductCode());
            product.setCreateAt(importDate);
            product.setIsDeleted(false);

            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(dto.getUnitOfMeasureId())
                    .orElseThrow(() -> new RuntimeException("Unit of Measure not found"));

            Category category = categoryRepository.findById(Long.valueOf(dto.getCategoryId()))
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setSupplier(supplier);
            product.setUnitOfMeasure(unitOfMeasure);
            product.setCategory(category);
            product = productRepository.save(product);

            BatchProduct batchProduct = new BatchProduct();
            batchProduct.setProduct(product);
            batchProduct.setQuantity(dto.getQuantity());
            batchProduct.setPrice(product.getImportPrice());
            batchProduct.setWeightPerUnit(dto.getWeightPerUnit());
            batchProduct.setWeight(dto.getWeightPerUnit() * dto.getQuantity());
            batchProduct.setUnit(dto.getUnit());
            batchProduct.setDescription("batch for product" + product.getName());

            batchProduct.setBatch(batch);
            batchProduct = batchProductRepository.save(batchProduct);
            batchProducts.add(batchProduct);

            ProductWarehouse productWarehouse = new ProductWarehouse();
            productWarehouse.setQuantity(dto.getQuantity());
            productWarehouse.setPrice(product.getImportPrice());
            productWarehouse.setWeightPerUnit(dto.getWeightPerUnit());
            productWarehouse.setWeight(dto.getWeightPerUnit() * dto.getQuantity());
            productWarehouse.setUnit(dto.getUnit());
            productWarehouse.setBatchCode(batch.getBatchCode());
            productWarehouse.setDescription("batch of product " + product.getName());

            productWarehouse.setProduct(product);

            Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId()) // Example
                    .orElseThrow(() -> new RuntimeException("Warehouse not found for given id"));

            productWarehouse.setWarehouse(warehouse);

            productWarehouse = productWareHouseRepository.save(productWarehouse);
        }
        batch.setBatchProducts(batchProducts);
        batch.setWarehouseReceipt(warehouseReceiptService.createImportWarehouseReceipt(batch.getBatchCode()));
    }

    private AdminProductDto convertToAdminProductDto(Product product) {
        AdminProductDto dto = new AdminProductDto();
        dto.setId((int) product.getId());
        dto.setProductCode(product.getProductCode());
        dto.setProductName(product.getName());
        if (product.getBatchProducts() != null && !product.getBatchProducts().isEmpty()) {
            BatchProduct firstBatchProduct = product.getBatchProducts().iterator().next();
            if (firstBatchProduct.getBatch() != null) {
                dto.setBatchCode(firstBatchProduct.getBatch().getBatchCode());
            }
        }
        dto.setImportDate(product.getCreateAt());
        dto.setPrice((long) product.getPrice());
        if (product.getProductWarehouses() != null && !product.getProductWarehouses().isEmpty()) {
            dto.setProductQuantity(String.valueOf(
                    product.getProductWarehouses().iterator().next().getQuantity()));
        }
        return dto;
    }

    private ProductDto toProductDto(Product product) {
        Set<UnitWeightPairs> unitWeightPairs = product.getProductWarehouses().stream()
                .map(pw -> new UnitWeightPairs(pw.getUnit(), pw.getWeightPerUnit())).collect(Collectors.toSet());
        ProductDto productDto = new ProductDto();
        productDto.setProductCode(product.getProductCode());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setImage(product.getImage());
        if (product.getCategory() != null) {
            productDto.setCategoryId(String.valueOf(product.getCategory().getId()));
        }
        if (product.getSupplier() != null) {
            productDto.setSupplierId(product.getSupplier().getId());
        }
        if (product.getUnitOfMeasure() != null) {
            productDto.setUnitOfMeasureId(product.getUnitOfMeasure().getId());
        }
        productDto.setUnitWeightPairsList(unitWeightPairs);
        return productDto;
    }

}
