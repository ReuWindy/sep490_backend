package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.*;
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
    private final UserRepository userRepository;
    private final WarehouseReceiptService warehouseReceiptService;
    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final ProductPriceRepository productPriceRepository;

    public ProductServiceImpl(ProductRepository productRepository, SupplierRepository supplierRepository, UnitOfMeasureRepository unitOfMeasureRepository, ProductWareHouseRepository productWareHouseRepository, WarehouseRepository warehouseRepository, CategoryRepository categoryRepository, BatchRepository batchRepository, BatchProductRepository batchProductRepository, WarehouseReceiptService warehouseReceiptService, UserService userService, UserRepository userRepository, CustomerRepository customerRepository, ProductPriceRepository productPriceRepository) {
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
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.productPriceRepository = productPriceRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllBatchProducts(String batchCode) {
        Optional<List<Product>> p = Optional.ofNullable(productRepository.findByBatchCode(batchCode));
        return p.orElse(null);
    }

    @Override
    public Product getProductById(int id) {
        Optional<Product> p = productRepository.findById((long) id);
        return p.orElse(null);
    }

    @Override
    public Product createProduct(ProductDto productDto) {
        Optional<Product> existingProduct = productRepository.findByNameAndCategoryIdAndSupplierId(productDto.getName(),
                productDto.getCategoryId(), productDto.getSupplierId());

        if (existingProduct.isPresent()) {
            throw new RuntimeException("Error:  Sản phẩm đã tồn tại");
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());
        product.setProductCode(RandomProductCodeGenerator.generateProductCode());

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));

        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(productDto.getUnitOfMeasureId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị"));

        product.setCategory(category);
        product.setSupplier(supplier);
        product.setUnitOfMeasure(unitOfMeasure);

        Product savedProduct = productRepository.save(product);

        if (productDto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(productDto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kho"));

            ProductWarehouse productWarehouse = new ProductWarehouse();
            productWarehouse.setProduct(savedProduct);
            productWarehouse.setWarehouse(warehouse);
            product.setCreateAt(new Date());
            product.setIsDeleted(true);

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
        if (product.isPresent()) {
            product.get().setUpdateAt(new Date());
            product.get().setIsDeleted(!product.get().getIsDeleted());
        }
    }

    @Override
    public Page<AdminProductDto> getProductByFilterForAdmin(String productCode, String productName, String batchCode, Long warehouseId, Date importDate, String productQuantity, String sortDirection, String priceOrder, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ProductSpecification productSpecs = new ProductSpecification();
        Specification<Product> specification = productSpecs.hasProductCodeOrProductNameOrBatchCodeOrImportDate(productCode, productName, warehouseId, batchCode, importDate, priceOrder, sortDirection);
        Page<Product> products = productRepository.findAll(specification, pageable);
        return products.map(this::convertToAdminProductDto);
    }

    @Override
    public Page<ProductDto> getProductByFilterForCustomer(String productCode, String categoryName, String supplierName, String username, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ProductSpecification productSpecification = new ProductSpecification();
        Specification<Product> specification = productSpecification.hasProductCodeOrCategoryNameOrSupplierName(productCode, categoryName, supplierName);
        Page<Product> products = productRepository.findAll(specification, pageable);
        Customer customer = getCustomerByUsername(username);
        return products.map(product -> toProductDto(product, customer));
    }

    @Override
    public Page<ProductDto> getProductByFilterForCustomer(String productCode, String categoryName, String supplierName, Long id, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ProductSpecification productSpecification = new ProductSpecification();
        Specification<Product> specification = productSpecification.hasProductCodeOrCategoryNameOrSupplierName(productCode, categoryName, supplierName);
        Page<Product> products = productRepository.findAll(specification, pageable);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        return products.map(product -> toProductDto(product, customer));
    }

    @Override
    public Product createCustomerProduct(ProductDto productDto) {
        Product createdProduct = new Product();
        createdProduct.setCreateAt(new Date());
        createdProduct.setDescription(productDto.getDescription());
        createdProduct.setImage(productDto.getImage());
        createdProduct.setImportPrice(productDto.getPrice());
        createdProduct.setIsDeleted(false);
        createdProduct.setName(productDto.getName());
        createdProduct.setPrice(productDto.getPrice());
        createdProduct.setProductCode(RandomProductCodeGenerator.generateProductCode());
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        createdProduct.setSupplier(supplier);
        createdProduct.setCategory(category);
        productRepository.save(createdProduct);
        return createdProduct;
    }

    @Override
    public Product updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm"));
        boolean exist = productRepository.existsByNameAndCategoryIdAndSupplierId(productDto.getId(), productDto.getName(), productDto.getCategoryId(), productDto.getSupplierId());

        if (exist) {
            throw new RuntimeException("Lỗi:  Sản phẩm đã tồn tại");
        }

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy danh mục"));
        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy nhà cung cấp"));

        product.setCategory(category);
        product.setSupplier(supplier);
        product.setUpdateAt(new Date());

        return productRepository.save(product);
    }

    @Override
    public List<BatchProduct> previewBatchProducts(List<importProductDto> ImportProductDtoList) {
        Batch batch = new Batch();
        batch.setBatchStatus("Bản xem trước");
        Date importDate = new Date();
        batch.setImportDate(importDate);
        batch.setReceiptType(ReceiptType.IMPORT);
        batch = createNewBatch(batch);

        Set<BatchProduct> batchProducts = new HashSet<>();
        for (importProductDto dto : ImportProductDtoList) {
            Product product = findOrCreateProduct(dto);
            BatchProduct batchProduct = getBatchProduct(dto, product, batch);
            batchProduct = batchProductRepository.save(batchProduct);
            batchProducts.add(batchProduct);
        }

        batch.setBatchProducts(batchProducts);
        batch.setWarehouseReceipt(warehouseReceiptService.createImportWarehouseReceipt(batch.getBatchCode()));
        batchRepository.save(batch);

        return new ArrayList<>(batchProducts);
    }

    private static BatchProduct getBatchProduct(importProductDto dto, Product product, Batch batch) {
        BatchProduct batchProduct = new BatchProduct();
        batchProduct.setProduct(product);
        batchProduct.setQuantity(dto.getQuantity());
        batchProduct.setPrice(dto.getImportPrice());
        batchProduct.setWeightPerUnit(dto.getWeightPerUnit());
        batchProduct.setWeight(dto.getWeightPerUnit() * dto.getQuantity());
        batchProduct.setUnit(dto.getUnit());
        batchProduct.setDescription("Nhập: Lô hàng của sản phẩm: " + dto.getName());
        batchProduct.setWarehouseId(dto.getWarehouseId());
        batchProduct.setAdded(false);
        batchProduct.setBatch(batch);
        return batchProduct;
    }

    private Batch createNewBatch(Batch batch) {
        batch.setBatchCode(RandomBatchCodeGenerator.generateBatchCode());

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        batch.setBatchCreator(user);
        batch = batchRepository.save(batch);
        return batch;
    }

    @Override
    public String confirmAndAddSelectedProductToWarehouse(long batchId, List<BatchProductSelection> batchProductSelections) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy lô hàng với id:" + batchId));

        Map<String, BatchProductSelection> selectionMap = batchProductSelections.stream()
                .collect(Collectors.toMap(
                        selection -> selection.getProductId() + "-" + selection.getUnit() + "-" + selection.getWeighPerUnit() + "-" + selection.getSupplierId(),
                        selection -> selection
                ));

        for (BatchProduct batchProduct : batch.getBatchProducts()) {
            String key = batchProduct.getProduct().getId() + "-" + batchProduct.getUnit() + "-" + batchProduct.getWeightPerUnit() + "-" + batchProduct.getProduct().getSupplier().getId();

            BatchProductSelection selection = selectionMap.get(key);
            if (selection != null && !batchProduct.isAdded()) {
                batchProduct.setAdded(true);
                batchProduct.setDescription("Đã thêm vào kho");
                ProductWarehouse productWarehouse = new ProductWarehouse();
                productWarehouse.setQuantity(batchProduct.getQuantity());
                productWarehouse.setBatchCode(batch.getBatchCode());
                productWarehouse.setImportPrice(batchProduct.getPrice());
                productWarehouse.setWeightPerUnit(batchProduct.getWeightPerUnit());
                productWarehouse.setWeight(batchProduct.getWeight());
                productWarehouse.setUnit(batchProduct.getUnit());
                productWarehouse.setProduct(batchProduct.getProduct());
                Product product = productWarehouse.getProduct();
                product.setImportPrice(productWarehouse.getImportPrice() / productWarehouse.getWeightPerUnit());
                product.setIsDeleted(false);

                Warehouse warehouse = warehouseRepository.findById(batchProduct.getWarehouseId())
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy kho hàng với Id: " + batchProduct.getWarehouseId()));
                productWarehouse.setWarehouse(warehouse);
                productRepository.save(product);
                productWareHouseRepository.save(productWarehouse);
            }
        }
        batchProductRepository.saveAll(batch.getBatchProducts());
        batch.setBatchStatus("Đã xác nhận");
        batchRepository.save(batch);

        return "Đã thêm những sản phẩm đã chọn vào kho.";
    }


    @Override
    public String prepareExportProduct(List<ExportProductDto> exportProductDtoList) {
        Batch batch = new Batch();
        batch.setBatchStatus("Chờ xác nhận");
        batch.setImportDate(new Date());
        batch.setReceiptType(ReceiptType.EXPORT);
        batch = createNewBatch(batch);

        for (ExportProductDto dto : exportProductDtoList) {
            if (dto.getQuantity() <= 0) {
                throw new RuntimeException("Lỗi: Số lượng không thể <= 0");
            }
            Optional<ProductWarehouse> p = productWareHouseRepository.findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(
                    dto.getProductName(),
                    dto.getUnit(),
                    dto.getWeightPerUnit(),
                    (long) dto.getWarehouseId()
            );
            if (p.isPresent()) {
                ProductWarehouse productWarehouse = p.get();
                BatchProduct batchProduct = getBatchProduct(dto, productWarehouse, batch);
                batchProduct.setBatch(batch);
                batchProductRepository.save(batchProduct);
            } else {
                batchRepository.delete(batch);
                throw new RuntimeException("Lỗi: Không tìm thấy batchProduct");
            }
        }
        batch.setWarehouseReceipt(warehouseReceiptService.createExportWarehouseReceipt(batch.getBatchCode()));
        return "Ok";
    }

    @Override
    public String confirmAndExportProducts(Long batchId, List<ExportProductDto> exportProductDtos) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy lô hàng"));

        List<BatchProduct> modifiedBatchProducts = new ArrayList<>();

        for (ExportProductDto selection : exportProductDtos) {
            ProductWarehouse productWarehouse = productWareHouseRepository.findByProductNameAndUnitAndWeightPerUnitAndWarehouseId(
                    selection.getProductName(),
                    selection.getUnit(),
                    selection.getWeightPerUnit(),
                    (long) selection.getWarehouseId()
            ).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm " + selection.getProductName() + " trong kho"));

            int newQuantity = productWarehouse.getQuantity() - selection.getQuantity();
            if (newQuantity < 0) {
                throw new RuntimeException("Lỗi: Số lượng sản phẩm " + selection.getProductName() + " trong kho không đủ.");
            }
            productWarehouse.setQuantity(newQuantity);
            productWareHouseRepository.save(productWarehouse);

            batch.getBatchProducts().stream()
                    .filter(bp -> bp.getProduct().getName().equals(selection.getProductName())
                            && bp.getUnit().equals(selection.getUnit())
                            && bp.getWeightPerUnit() == selection.getWeightPerUnit()
                            && !bp.isAdded())
                    .forEach(bp -> {
                        bp.setDescription("Đã xuất hàng khỏi kho");
                        bp.setAdded(true);
                        modifiedBatchProducts.add(bp);
                    });
        }
        if (!modifiedBatchProducts.isEmpty()) {
            batchProductRepository.saveAll(modifiedBatchProducts);
        }
        batch.setBatchStatus("Đã xác nhận");
        batchRepository.save(batch);

        return "Xuất kho thành công.";
    }

    private static BatchProduct getBatchProduct(ExportProductDto dto, ProductWarehouse productWarehouse, Batch batch) {
        Product product = productWarehouse.getProduct();

        BatchProduct batchProduct = new BatchProduct();
        batchProduct.setProduct(product);
        batchProduct.setQuantity(dto.getQuantity());
        batchProduct.setPrice(product.getImportPrice());
        batchProduct.setWeightPerUnit(dto.getWeightPerUnit());
        batchProduct.setWeight(dto.getWeightPerUnit() * dto.getQuantity());
        batchProduct.setUnit(dto.getUnit());
        batchProduct.setWarehouseId((long) dto.getWarehouseId());
        batchProduct.setDescription("Xuất: Lô hàng của sản phẩm: " + product.getName());
        batchProduct.setBatch(batch);
        return batchProduct;
    }
    
    private Product findOrCreateProduct(importProductDto dto) {
        Optional<Product> existingProduct = productRepository.findByNameAndCategoryIdAndSupplierId(dto.getName(),
                Long.valueOf(dto.getCategoryId()), dto.getSupplierId());
        if (existingProduct.isPresent()) {
            return existingProduct.get();
        } else {
            Product product = new Product();
            product.setName(dto.getName());
            product.setProductCode(RandomProductCodeGenerator.generateProductCode());
            product.setDescription(dto.getDescription());
            product.setImportPrice(dto.getImportPrice());
            product.setImage(dto.getImage());
            product.setCreateAt(new Date());
            product.setIsDeleted(false);

            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy nhà cung cấp với id: " + dto.getSupplierId()));

            Category category = categoryRepository.findById(Long.valueOf(dto.getCategoryId()))
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy danh mục với id: " + dto.getCategoryId()));

            UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(dto.getUnitOfMeasureId())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy hệ quy đổi với id: " + dto.getUnitOfMeasureId()));

            product.setCategory(category);
            product.setUnitOfMeasure(unitOfMeasure);
            product.setSupplier(supplier);
            return productRepository.save(product);
        }
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
        dto.setActive(product.getIsDeleted());
        dto.setImportDate(product.getCreateAt());
        dto.setPrice((long) product.getImportPrice());
        dto.setUpdateAt(product.getUpdateAt());
        if (product.getProductWarehouses() != null && !product.getProductWarehouses().isEmpty()) {
            dto.setProductQuantity(String.valueOf(
                    product.getProductWarehouses().iterator().next().getQuantity()));
        }
        if (product.getSupplier() != null && product.getSupplier().isActive()) {
            dto.setSupplierName(product.getSupplier().getName());
        }
        if (product.getCategory() != null && product.getCategory().getActive()) {
            dto.setCategoryName(product.getCategory().getName());
        }
        List<ProductWarehouseDto> productWarehouseDtos = new ArrayList<>();
        for (ProductWarehouse productWarehouse : product.getProductWarehouses()) {
            ProductWarehouseDto productWarehouseDto = new ProductWarehouseDto();
            productWarehouseDto.setQuantity(productWarehouse.getQuantity());
            productWarehouseDto.setUnit(productWarehouse.getUnit());
            productWarehouseDto.setWeightPerUnit(productWarehouse.getWeightPerUnit());
            productWarehouseDtos.add(productWarehouseDto);
        }
        dto.setProductWarehouseDtos(productWarehouseDtos);
        return dto;
    }

    private ProductDto toProductDto(Product product, Customer customer) {
        Set<UnitWeightPairs> unitWeightPairs = product.getProductWarehouses().stream()
                .map(pw -> new UnitWeightPairs(
                        pw.getUnit(),
                        pw.getWeightPerUnit(),
                        pw.getQuantity()
                )).collect(Collectors.toSet());
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setProductCode(product.getProductCode());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setImage(product.getImage());

        if (product.getCategory() != null) {
            productDto.setCategoryId(product.getCategory().getId());
            productDto.setCategoryName(product.getCategory().getName());
        }
        if (product.getSupplier() != null) {
            productDto.setSupplierId(product.getSupplier().getId());
        }
        if (product.getUnitOfMeasure() != null) {
            productDto.setUnitOfMeasureId(product.getUnitOfMeasure().getId());
        }
        ProductPrice productPrice = productPriceRepository.findByPriceIdAndProductId(customer.getPrice().getId(), product.getId()).orElse(null);
        if (productPrice != null) {
            productDto.setCustomerPrice(productPrice.getUnit_price());
        } else {
            productDto.setCustomerPrice(product.getPrice());
        }
        productDto.setUnitWeightPairsList(unitWeightPairs);
        return productDto;
    }

    private Customer getCustomerByUsername(String username) {
        User user = userRepository.findByUsername(username);
        long id = user.getId();
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("no customer here for u"));
    }

    @Override
    public Product disableProduct(Long id) {
        Product productToDisable = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        productToDisable.setIsDeleted(true);
        return productToDisable;
    }

    @Override
    public Product enableProduct(Long id) {
        Product productToEnable = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        productToEnable.setIsDeleted(false);
        return productToEnable;
    }
}
