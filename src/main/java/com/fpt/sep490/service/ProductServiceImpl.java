package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.*;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.utils.RandomBatchCodeGenerator;
import com.fpt.sep490.utils.RandomProductCodeGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final OrderRepository orderRepository;
    private final ProductPriceRepository productPriceRepository;
    private final PriceRepository priceRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderServiceImpl orderService;

    public ProductServiceImpl(ProductRepository productRepository, SupplierRepository supplierRepository, UnitOfMeasureRepository unitOfMeasureRepository, ProductWareHouseRepository productWareHouseRepository, WarehouseRepository warehouseRepository, CategoryRepository categoryRepository, BatchRepository batchRepository, BatchProductRepository batchProductRepository, WarehouseReceiptService warehouseReceiptService, UserService userService, UserRepository userRepository, CustomerRepository customerRepository, OrderRepository orderRepository, ProductPriceRepository productPriceRepository, PriceRepository priceRepository, OrderDetailRepository orderDetailRepository, OrderServiceImpl orderService) {
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
        this.orderRepository = orderRepository;
        this.productPriceRepository = productPriceRepository;
        this.priceRepository = priceRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderService = orderService;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAllByIsDeleted(false);
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
        product.setIsDeleted(false);
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
        product.setCreateAt(new Date());

        Product savedProduct = productRepository.save(product);

        if (productDto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(productDto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kho"));

            ProductWarehouse productWarehouse = new ProductWarehouse();
            productWarehouse.setProduct(savedProduct);
            productWarehouse.setWarehouse(warehouse);
            productWarehouse.setUnit("Chưa đóng gói");
            productWarehouse.setWeightPerUnit(1.0);

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
    public Page<AdminProductDto> getProductByFilterForAdmin(String productCode, String productName, Long categoryId, Long supplierId, String batchCode, Long warehouseId, Date importDate, String productQuantity, String sortDirection, String priceOrder, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ProductSpecification productSpecs = new ProductSpecification();
        Specification<Product> specification = productSpecs.hasProductCodeOrProductNameOrBatchCodeOrImportDate(productCode, productName, categoryId, supplierId, warehouseId, batchCode, importDate, priceOrder, sortDirection);
        Page<Product> products = productRepository.findAll(specification, pageable);
        return products.map(this::convertToAdminProductDto);
    }

    @Override
    public Page<ProductDto> getProductByFilterForCustomer(String name, String productCode, String categoryName, String supplierName, String username, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ProductSpecification productSpecification = new ProductSpecification();
        Specification<Product> specification = productSpecification.hasNameOrProductCodeOrCategoryNameOrSupplierNameAndNotNull(name, productCode, categoryName, supplierName);
        Page<Product> products = productRepository.findAll(specification, pageable);
        Customer customer = getCustomerByUsername(username);
        return products.map(product -> toProductDto(product, customer));
    }

    @Override
    public Page<ProductDto> getProductByFilterForCustomer(String name, String productCode, String categoryName, String supplierName, Long id, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ProductSpecification productSpecification = new ProductSpecification();
        Specification<Product> specification = productSpecification.hasNameOrProductCodeOrCategoryNameOrSupplierName(name, productCode, categoryName, supplierName);
        Page<Product> products = productRepository.findAll(specification, pageable);
        if (id != null) {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
            return products.map(product -> toProductDto(product, customer));
        } else {
            return products.map(this::toProductDto);
        }
    }

    @Override
    public Page<ProductDto> getProductAndIngredientByFilterForCustomer(String name, String productCode, String categoryName, String supplierName, Long id, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ProductSpecification productSpecification = new ProductSpecification();
        Specification<Product> specification = productSpecification.hasNameOrProductCodeOrCategoryNameOrSupplierName2(name, productCode, categoryName, supplierName);
        Page<Product> products = productRepository.findAll(specification, pageable);
        if (id != null) {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
            return products.map(product -> toProductDto(product, customer));
        } else {
            return products.map(this::toProductDto);
        }
    }

    @Override
    public Page<MissingProductDto> getMissingProductsByFilter(String name, String productCode, String categoryName, String supplierName, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        List<StatusEnum> statuses = List.of(StatusEnum.CONFIRMED);

        List<OrderDetail> orderDetails = orderDetailRepository.findAllInProgressOrder(statuses);
        Map<Long, MissingProductDto> missingProductMap = new HashMap<>();
        for (OrderDetail orderDetail : orderDetails) {
            int quantity = orderService.getMissingQuantity(orderDetail);
            if (quantity > 0) {
                ProductWarehouse productWarehouse = orderService.getProductWarehouse(orderDetail);
                MissingProductDto missingProductDto = new MissingProductDto();
                missingProductDto.setId(orderDetail.getProduct().getId());
                missingProductDto.setName(orderDetail.getProduct().getName());
                missingProductDto.setProductCode(orderDetail.getProduct().getProductCode());
                missingProductDto.setCategoryId(orderDetail.getProduct().getCategory().getId());
                missingProductDto.setCategoryName(orderDetail.getProduct().getCategory().getName());
                missingProductDto.setSupplierId(orderDetail.getProduct().getSupplier().getId());
                missingProductDto.setSupplierName(orderDetail.getProduct().getSupplier().getName());
                missingProductDto.setImportPrice(productWarehouse.getImportPrice());
                missingProductDto.setUnit(productWarehouse.getUnit());
                missingProductDto.setWeightPerUnit(productWarehouse.getWeightPerUnit());
                missingProductDto.setMissingQuantity(quantity);
                missingProductMap.putIfAbsent(orderDetail.getProduct().getId(), missingProductDto);
            }
        }

        List<MissingProductDto> filteredDtos = missingProductMap.values().stream()
                .filter(dto -> (name == null || dto.getName().contains(name)) &&
                        (productCode == null || dto.getProductCode().equalsIgnoreCase(productCode)) &&
                        (categoryName == null || dto.getCategoryName().equalsIgnoreCase(categoryName)) &&
                        (supplierName == null || dto.getSupplierName().equalsIgnoreCase(supplierName)))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredDtos.size());
        List<MissingProductDto> paginatedList = (start < filteredDtos.size()) ? filteredDtos.subList(start, end) : new ArrayList<>();

        return new PageImpl<>(paginatedList, pageable, filteredDtos.size());
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục!"));

        if (!category.getActive()) {
            throw new RuntimeException("Danh mục đã bị vô hiệu hóa, Vui lòng chọn danh mục khác");
        }

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp!"));

        if (!supplier.isActive()) {
            throw new RuntimeException("Nhà cung cấp đã bị vô hiệu hóa, Vui lòng chọn nhà cung cấp khác");
        }

        createdProduct.setSupplier(supplier);
        createdProduct.setCategory(category);
        try {
            productRepository.save(createdProduct);
            return createdProduct;
        } catch (Exception e) {
            throw new RuntimeException("Xảy ra lỗi trong quá trình tạo sản phẩm mới!");
        }
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
        if (productDto.getPrice() < 0) {
            throw new RuntimeException("Lỗi: Giá của sản phẩm phải là số dương");
        }
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy danh mục"));
        if (!category.getActive()) {
            throw new RuntimeException("Danh mục đã bị vô hiệu hóa, Vui lòng chọn danh mục khác");
        }
        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy nhà cung cấp"));
        if (!supplier.isActive()) {
            throw new RuntimeException("Nhà cung cấp đã bị vô hiệu hóa, Vui lòng chọn nhà cung cấp khác");
        }
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setUpdateAt(new Date());
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Xảy ra lỗi trong quá trình tạo mới sản phẩm!");
        }
    }

    @Override
    public List<importProductDto> readExcelFile(MultipartFile file) {
        List<importProductDto> productList = new ArrayList<>();
        int count = 0;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (Double.parseDouble(getCellValue(row.getCell(2))) <= 0) {
                    throw new RuntimeException("Lỗi ở dòng " + i + ": Giá nhập phải lớn hơn 0");
                }
                if (Double.parseDouble(getCellValue(row.getCell(5))) <= 0) {
                    throw new RuntimeException("Lỗi ở dòng " + i + ": Trọng lượng phải lớn hơn 0");
                }
                if (Integer.parseInt(getCellValue(row.getCell(9))) < 0) {
                    throw new RuntimeException("Lỗi ở dòng " + i + ": Số lượng phải là số nguyên dương");
                }
                if (row != null) {
                    String productCode = getCellValue(row.getCell(0));
                    Product product = productRepository.findByProductCode(productCode).orElseThrow(() -> new RuntimeException("Không tồn tại sản phẩm với mã: " + productCode));
                    importProductDto importProduct = importProductDto.builder()
                            .name(product.getName())
                            .importPrice(Double.parseDouble(getCellValue(row.getCell(2))))
                            .quantity(Integer.parseInt(getCellValue(row.getCell(9))))
                            .unit(getCellValue(row.getCell(4)))
                            .weightPerUnit(Double.parseDouble(getCellValue(row.getCell(5))))
                            .categoryId(findCategoryIdByName(getCellValue(row.getCell(6))))
                            .supplierId(findSupplierIdByName(getCellValue(row.getCell(7))))
                            .warehouseId(findWarehouseIdByName(getCellValue(row.getCell(8))))
                            .unitOfMeasureId(1L)
                            .build();
                    productList.add(importProduct);
                    count += importProduct.getQuantity();
                }
            }
            if (count <= 0) {
                throw new RuntimeException("Số lượng sản phẩm trong phiếu nhập không hợp lệ! Vui lòng kiểm tra lại");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return productList;
    }


    @Override
    public void createExcelTemplate(HttpServletResponse response, AdminOrderDto adminOrderDto) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Nhập Hàng");
            String[] headers = {"Mã", "Tên", "Giá Nhập", "Số Lượng mong muốn", "Quy cách", "Trọng lượng (kg)", "Danh mục", "Nhà cung cấp", "Kho hàng", "Số lượng thực tế"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            CellStyle unlockedCellStyle = workbook.createCellStyle();
            unlockedCellStyle.setLocked(false);

            int rowIndex = 1;
            for (OrderDetailDto orderDetailDto : adminOrderDto.getOrderDetails()) {
                Product product = productRepository.findById(orderDetailDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại, mã sản phẩm: " + orderDetailDto.getProductId()));

                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(product.getProductCode());
                row.createCell(1).setCellValue(product.getName());
                Cell priceCell = row.createCell(2);
                priceCell.setCellValue(product.getImportPrice());
                priceCell.setCellStyle(unlockedCellStyle);

                row.createCell(3).setCellValue(orderDetailDto.getQuantity());
                row.createCell(4).setCellValue(orderDetailDto.getProductUnit());
                row.createCell(5).setCellValue(orderDetailDto.getWeightPerUnit());
                row.createCell(6).setCellValue(product.getCategory() != null ? product.getCategory().getName() : "N/A");
                row.createCell(7).setCellValue(product.getSupplier() != null ? product.getSupplier().getName() : "N/A");

                if (!product.getProductWarehouses().isEmpty()) {
                    ProductWarehouse firstProductWarehouse = new ArrayList<>(product.getProductWarehouses()).get(0);
                    row.createCell(8).setCellValue(firstProductWarehouse.getWarehouse().getName());
                }

                Cell quantityCell = row.createCell(9);
                quantityCell.setCellValue(0);
                quantityCell.setCellStyle(unlockedCellStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            sheet.protectSheet("password");

            CellRangeAddressList priceRange = new CellRangeAddressList(1, rowIndex - 1, 2, 2);
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            DataValidationConstraint priceConstraint = validationHelper.createNumericConstraint(
                    DataValidationConstraint.ValidationType.DECIMAL,
                    DataValidationConstraint.OperatorType.GREATER_OR_EQUAL,
                    "0",
                    null
            );
            DataValidation priceValidation = validationHelper.createValidation(priceConstraint, priceRange);
            priceValidation.setShowErrorBox(true);
            priceValidation.createErrorBox("Lỗi nhập liệu", "Chỉ được nhập số thập phân không âm.");
            sheet.addValidationData(priceValidation);

            CellRangeAddressList quantityRange = new CellRangeAddressList(1, rowIndex - 1, 9, 9);
            DataValidationConstraint quantityConstraint = validationHelper.createNumericConstraint(
                    DataValidationConstraint.ValidationType.INTEGER,
                    DataValidationConstraint.OperatorType.GREATER_OR_EQUAL,
                    "0",
                    null
            );
            DataValidation quantityValidation = validationHelper.createValidation(quantityConstraint, quantityRange);
            quantityValidation.setShowErrorBox(true);
            quantityValidation.createErrorBox("Lỗi nhập liệu", "Chỉ được nhập số nguyên không âm.");
            sheet.addValidationData(quantityValidation);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            response.setHeader("Content-Disposition", "attachment; filename=PhieuNhapHang_" + currentDateTime + ".xlsx");
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage(), e);
        }
    }

//    private void addDropdownToColumn2(Sheet sheet, int column, List<String> options) {
//        if (options.isEmpty()) {
//            throw new RuntimeException("Dropdown options list is empty for column: " + column);
//        }
//
//        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
//
//        CellRangeAddressList addressList = new CellRangeAddressList(1, 100, column, column);
//
//        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(
//                options.toArray(new String[0]));
//
//        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
//        dataValidation.setShowErrorBox(true);
//        sheet.addValidationData(dataValidation);
//    }

//    private List<String> getUnitsFromDatabase() {
//        return List.of("Bao", "Túi");
//    }
//
//    private List<String> getCategoriesFromDatabase() {
//        return categoryRepository.findAllCategoryNames();
//    }
//
//    private List<String> getSuppliersFromDatabase() {
//        return supplierRepository.findAllSupplierNames();
//    }
//
//    private List<String> getWarehousesFromDatabase() {
//        return warehouseRepository.findAllWarehouseNames();
//    }

    private String findCategoryIdByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục: " + categoryName))
                .getId().toString();
    }

    private Long findSupplierIdByName(String supplierName) {
        return supplierRepository.findByName(supplierName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp: " + supplierName))
                .getId();
    }

    private Long findWarehouseIdByName(String warehouseName) {
        return warehouseRepository.findByName(warehouseName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà kho: " + warehouseName))
                .getId();
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

    @Override
    public List<BatchProduct> previewBatchProductsFromProduction(List<importProductFromProductionDto> ImportProductDtoList) {
        Batch batch = new Batch();
        batch.setBatchStatus("Bản xem trước");
        Date importDate = new Date();
        batch.setImportDate(importDate);
        batch.setReceiptType(ReceiptType.IMPORT);
        batch = createNewBatch(batch);

        Set<BatchProduct> batchProducts = new HashSet<>();
        for (importProductFromProductionDto dto : ImportProductDtoList) {
            Product product = findOrCreateProduct(dto);
            BatchProduct batchProduct = getBatchProduct(dto, product, batch);
            batchProduct = batchProductRepository.save(batchProduct);
            batchProducts.add(batchProduct);
        }

        batch.setBatchProducts(batchProducts);
        batch.setWarehouseReceipt(warehouseReceiptService.createImportWarehouseReceiptFromProduction(batch.getBatchCode()));
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

    private static BatchProduct getBatchProduct(importProductFromProductionDto dto, Product product, Batch batch) {
        BatchProduct batchProduct = new BatchProduct();
        batchProduct.setProduct(product);
        batchProduct.setQuantity(dto.getQuantity());
        batchProduct.setPrice(0.0);
        batchProduct.setWeightPerUnit(dto.getWeightPerUnit());
        batchProduct.setWeight(dto.getWeightPerUnit() * dto.getQuantity());
        batchProduct.setUnit(dto.getUnit());
        batchProduct.setDescription("Nhập: Lô hàng của sản phẩm: " + dto.getName() + " sau khi hoàn thành sản xuất");
        batchProduct.setWarehouseId(2L);
        batchProduct.setAdded(false);
        batchProduct.setBatch(batch);
        return batchProduct;
    }

    private Batch createNewBatch(Batch batch) {
        batch.setBatchCode(RandomBatchCodeGenerator.generateBatchCode());

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Lỗi : không tìm thấy thông tin người dùng!");
        }
        batch.setBatchCreator(user);
        try {
            batch = batchRepository.save(batch);
            return batch;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình tạo lô hàng!" + e.getMessage());
        }
    }

    @Override
    public String confirmAndAddSelectedProductToWarehouse(long batchId, List<BatchProductSelection> batchProductSelections) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy lô hàng với id:" + batchId));

        Map<String, BatchProductSelection> selectionMap = new HashMap<>();
        for (BatchProductSelection selection : batchProductSelections) {
            String key = selection.getProductId() + "-" + selection.getUnit() + "-" + selection.getWeighPerUnit() + "-" + selection.getSupplierId();

            if (selectionMap.containsKey(key)) {
                throw new RuntimeException("Lỗi: Trùng sản phẩm với Id: " + selection.getProductId() + ", đơn vị: " + selection.getUnit() + ", trọng lượng: " + selection.getWeighPerUnit() + ". Vui lòng xoá sản phẩm bị trùng");
            }

            selectionMap.put(key, selection);
        }

        for (BatchProduct batchProduct : batch.getBatchProducts()) {
            String key = batchProduct.getProduct().getId() + "-" + batchProduct.getUnit() + "-" + batchProduct.getWeightPerUnit() + "-" + batchProduct.getProduct().getSupplier().getId();

            BatchProductSelection selection = selectionMap.get(key);

            if (selection != null && !batchProduct.isAdded()) {
                batchProduct.setAdded(true);
                batchProduct.setDescription("Đã thêm vào kho");

                Warehouse warehouse = warehouseRepository.findById(batchProduct.getWarehouseId())
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy kho hàng với Id: " + batchProduct.getWarehouseId()));

                ProductWarehouse productWarehouse = getProductWarehouse(batchProduct, warehouse);
                Product product = productWarehouse.getProduct();
                product.setImportPrice(batchProduct.getPrice());
                product.setUpdateAt(new Date());
                productWarehouse.setQuantity(productWarehouse.getQuantity() + batchProduct.getQuantity());
                productWarehouse.setWeight(productWarehouse.getWeight() + batchProduct.getWeight());
                productWarehouse.setImportPrice(batchProduct.getPrice());
                handleProductPrice(product, batchProduct.getPrice(), batchProduct.getUnit(), batchProduct.getWeightPerUnit());
                productRepository.save(product);
                productWareHouseRepository.save(productWarehouse);
            }
        }

        batchProductRepository.saveAll(batch.getBatchProducts());
        batch.setBatchStatus("Đã xác nhận");
        batchRepository.save(batch);

        return batch.getBatchCode();
    }


    private ProductWarehouse getProductWarehouse(BatchProduct batchProduct, Warehouse warehouse) {
        Optional<ProductWarehouse> existingProductWarehouse = productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                batchProduct.getProduct().getId(),
                batchProduct.getWarehouseId(),
                batchProduct.getUnit(),
                batchProduct.getWeightPerUnit()
        );
        if (existingProductWarehouse.isPresent()) {
            return existingProductWarehouse.get();
        }
        ProductWarehouse productWarehouse = new ProductWarehouse();
        productWarehouse.setQuantity(0);
        productWarehouse.setImportPrice(batchProduct.getPrice());
        productWarehouse.setWeightPerUnit(batchProduct.getWeightPerUnit());
        productWarehouse.setWeight(batchProduct.getWeight());
        productWarehouse.setUnit(batchProduct.getUnit());
        productWarehouse.setProduct(batchProduct.getProduct());
        productWarehouse.setWarehouse(warehouse);
        return productWarehouse;
    }


    @Override
    public List<BatchProduct> prepareExportProduct(List<ExportProductDto> exportProductDtoList) {
        Batch batch = new Batch();
        batch.setBatchStatus("Chờ xác nhận");
        batch.setImportDate(new Date());
        batch.setReceiptType(ReceiptType.EXPORT);
        batch = createNewBatch(batch);

        Set<BatchProduct> batchProducts = new HashSet<>();
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
                batchProducts.add(batchProduct);
                batchProductRepository.save(batchProduct);
            } else {
                batchRepository.delete(batch);
                throw new RuntimeException("Lỗi: Không tìm thấy batchProduct");
            }
        }
        batch.setWarehouseReceipt(warehouseReceiptService.createExportWarehouseReceipt(batch.getBatchCode()));
        return new ArrayList<>(batchProducts);
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

        return batch.getBatchCode();
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

    private Product findOrCreateProduct(importProductFromProductionDto dto) {
        Optional<Product> existingProduct = productRepository.findByNameAndCategoryIdAndSupplierId(dto.getName(),
                Long.valueOf(dto.getCategoryId()), 1L);
        if (existingProduct.isPresent()) {
            return existingProduct.get();
        } else {
            Product product = new Product();
            product.setName(dto.getName());
            product.setProductCode(RandomProductCodeGenerator.generateProductCode());
            product.setImportPrice(0.0);
            product.setCreateAt(new Date());
            product.setIsDeleted(false);

            Supplier supplier = supplierRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy nhà cung cấp mặc định"));

            Category category = categoryRepository.findById(Long.valueOf(dto.getCategoryId()))
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy danh mục với id: " + dto.getCategoryId()));

            UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy hệ quy đổi mặc định"));

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
//        Set<UnitWeightPairs> unitWeightPairs = product.getProductWarehouses().stream()
//                .map(pw -> new UnitWeightPairs(
//                        pw.getUnit(),
//                        pw.getWeightPerUnit(),
//                        pw.getQuantity()
//                )).collect(Collectors.groupingBy(
//                        pair-> new AbstractMap.SimpleEntry<>(pair.getProductUnit(),pair.getWeightPerUnit()),
//                        Collectors.summingInt(UnitWeightPairs::getQuantity)
//                ))
//                .entrySet().stream()
//                .map(entry -> new UnitWeightPairs(entry.getKey().getKey(), entry.getKey().getValue(), entry.getValue()))
//                .collect(Collectors.toSet());

        Set<UnitWeightPairs> unitWeightPairs = product.getProductWarehouses().stream()
                .map(pw -> new UnitWeightPairs(
                        pw.getUnit(),
                        pw.getWeightPerUnit(),
                        pw.getQuantity()
                )).collect(Collectors.toMap(
                        pw -> new AbstractMap.SimpleEntry<>(pw.getProductUnit(), pw.getWeightPerUnit()),
                        UnitWeightPairs::getQuantity,
                        Integer::sum
                ))
                .entrySet().stream()
                .map(entry -> new UnitWeightPairs(
                        entry.getKey().getKey(),
                        entry.getKey().getValue(),
                        entry.getValue()))
                .collect(Collectors.toSet());

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setProductCode(product.getProductCode());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setImage(product.getImage());

        Optional<Double> latestImportPrice = product.getBatchProducts().stream()
                .filter(batchProduct -> batchProduct.getBatch() != null && batchProduct.getBatch().getImportDate() != null)
                .max(Comparator.comparing(batchProduct -> batchProduct.getBatch().getImportDate()))
                .map(BatchProduct::getPrice);
        latestImportPrice.ifPresent(productDto::setImportPrice);

        if (product.getCategory() != null) {
            productDto.setCategoryId(product.getCategory().getId());
            productDto.setCategoryName(product.getCategory().getName());
        }
        if (product.getSupplier() != null) {
            productDto.setSupplierId(product.getSupplier().getId());
            productDto.setSupplierName(product.getSupplier().getName());
        }
        if (product.getUnitOfMeasure() != null) {
            productDto.setUnitOfMeasureId(product.getUnitOfMeasure().getId());
        }
        ProductPrice productPrice = productPriceRepository.findByPriceIdAndProductId(customer.getPrice().getId(), product.getId()).orElse(null);
        ProductPrice defaultPrice = productPriceRepository.findByPriceIdAndProductId(1L, product.getId()).orElseThrow(null);
        if (productPrice != null) {
            productDto.setCustomerPrice(productPrice.getUnit_price());
        } else {
            productDto.setCustomerPrice(defaultPrice.getUnit_price());
        }
        productDto.setUnitWeightPairsList(unitWeightPairs);
        return productDto;
    }

    private ProductDto toProductDto(Product product) {
//        Set<UnitWeightPairs> unitWeightPairs = product.getProductWarehouses().stream()
//                .map(pw -> new UnitWeightPairs(
//                        pw.getUnit(),
//                        pw.getWeightPerUnit(),
//                        pw.getQuantity()
//                )).collect(Collectors.toSet());
        Set<UnitWeightPairs> unitWeightPairs = product.getProductWarehouses().stream()
                .map(pw -> new UnitWeightPairs(
                        pw.getUnit(),
                        pw.getWeightPerUnit(),
                        pw.getQuantity()
                )).collect(Collectors.toMap(
                        pw -> new AbstractMap.SimpleEntry<>(pw.getProductUnit(), pw.getWeightPerUnit()),
                        UnitWeightPairs::getQuantity,
                        Integer::sum
                ))
                .entrySet().stream()
                .map(entry -> new UnitWeightPairs(entry.getKey().getKey(),
                        entry.getKey().getValue(),
                        entry.getValue()))
                .collect(Collectors.toSet());

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setProductCode(product.getProductCode());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setImage(product.getImage());
        Optional<Double> latestImportPrice = product.getBatchProducts().stream()
                .filter(batchProduct -> batchProduct.getBatch() != null && batchProduct.getBatch().getImportDate() != null)
                .max(Comparator.comparing(batchProduct -> batchProduct.getBatch().getImportDate()))
                .map(BatchProduct::getPrice);
        latestImportPrice.ifPresent(productDto::setImportPrice);

        if (product.getCategory() != null) {
            productDto.setCategoryId(product.getCategory().getId());
            productDto.setCategoryName(product.getCategory().getName());
        }
        if (product.getSupplier() != null) {
            productDto.setSupplierId(product.getSupplier().getId());
            productDto.setSupplierName(product.getSupplier().getName());
        }
        if (product.getUnitOfMeasure() != null) {
            productDto.setUnitOfMeasureId(product.getUnitOfMeasure().getId());
        }
        ProductPrice productPrice = productPriceRepository.findByPriceIdAndProductId(1L, product.getId()).orElse(null);
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

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    @Override
    public Product disableProduct(Long id) {
        Product productToDisable = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        if (orderRepository.existsOrderWithProductIdAndStatus(id) > 0) {
            throw new RuntimeException("Hiện đang có đơn hàng đang được xử lý! Vui lòng thử lại sau.");
        } else {
            productToDisable.setIsDeleted(true);
        }
        return productToDisable;
    }

    @Override
    public Product enableProduct(Long id) {
        Product productToEnable = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        productToEnable.setIsDeleted(false);
        return productToEnable;
    }

    private void handleProductPrice(Product product, double newImportPrice, String unit, double weightPerUnit) {
        double sellingPrice = newImportPrice + 200;
        Price defaultPrice = priceRepository.findById(1L).orElse(null);
        ProductPrice productPrices = productPriceRepository.findByPriceIdAndProductId(defaultPrice.getId(), product.getId()).orElse(null);
        if (productPrices == null) {
            ProductPrice newProductPrice = new ProductPrice();
            newProductPrice.setPrice(defaultPrice);
            newProductPrice.setProduct(product);
            newProductPrice.setUnit_price(sellingPrice);
            product.setPrice(sellingPrice);
            productPriceRepository.save(newProductPrice);
        } else {
            productPrices.setUnit_price(sellingPrice);
            product.setPrice(sellingPrice);
            productPriceRepository.save(productPrices);
        }
    }
}