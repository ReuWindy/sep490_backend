package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.AdminProductDto;
import com.fpt.sep490.dto.ProductDto;
import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.dto.ProductionCompleteDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.BatchProductRepository;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.ProductWareHouseRepository;
import com.fpt.sep490.security.service.UserService;
import com.fpt.sep490.utils.RandomBatchCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductWarehouseServiceImpl implements ProductWarehouseService {
    private final BatchProductRepository batchProductRepository;
    private final ProductWareHouseRepository productWareHouseRepository;
    private final UserService userService;
    private final BatchRepository batchRepository;
    private final WarehouseReceiptService warehouseReceiptService;

    public ProductWarehouseServiceImpl(BatchProductRepository batchProductRepository, ProductWareHouseRepository productWareHouseRepository, UserService userService, BatchRepository batchRepository, WarehouseReceiptService warehouseReceiptService) {
        this.batchProductRepository = batchProductRepository;
        this.productWareHouseRepository = productWareHouseRepository;
        this.userService = userService;
        this.batchRepository = batchRepository;
        this.warehouseReceiptService = warehouseReceiptService;
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
    public Page<ProductWarehouseDto> getProductWarehousesByFilter(String productCode, String productName, Long categoryId, Long supplierId, Long warehouseId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        ProductWarehouseSpecification productSpecs = new ProductWarehouseSpecification();
        Specification<ProductWarehouse> specification = productSpecs.hasProductCodeOrProductNameOrBatchCodeOrImportDate(productCode, productName, categoryId, supplierId, warehouseId);
        Page<ProductWarehouse> products = productWareHouseRepository.findAll(specification, pageable);
        return products.map(this::toDto);
    }

    private ProductWarehouseDto toDto(ProductWarehouse productWarehouse) {
        ProductWarehouseDto dto = new ProductWarehouseDto();
        dto.setId(productWarehouse.getId());
        dto.setPrice(productWarehouse.getImportPrice());
        dto.setWeight(productWarehouse.getWeight());
        dto.setUnit(productWarehouse.getUnit());
        dto.setWeightPerUnit(productWarehouse.getWeightPerUnit());
        dto.setQuantity(productWarehouse.getQuantity());
        ProductDto productDto = new ProductDto();
        productDto.setCategoryName(productWarehouse.getProduct().getCategory().getName());
        productDto.setSupplierName(productWarehouse.getProduct().getSupplier().getName());
        productDto.setId(productWarehouse.getProduct().getId());
        productDto.setProductCode(productWarehouse.getProduct().getProductCode());
        productDto.setName(productWarehouse.getProduct().getName());
        productDto.setDescription(productWarehouse.getProduct().getDescription());
        productDto.setDeleted(productWarehouse.getProduct().getIsDeleted());
        dto.setProduct(productDto);
        return dto;
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
            try {
                productWareHouseRepository.save(productWarehouse);
            }catch (Exception e){
                throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình lưu! "+e.getMessage());
            }
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

        Batch batch = new Batch();
        batch.setBatchStatus("Chờ xác nhận");
        batch.setImportDate(new Date());
        batch.setReceiptType(ReceiptType.EXPORT);
        batch = createNewBatch(batch);
        BatchProduct batchProduct = getBatchProduct(quantity, productWarehouse, batch);
        batchProduct.setBatch(batch);
        batchProductRepository.save(batchProduct);
        batch.setWarehouseReceipt(warehouseReceiptService.createExportWarehouseReceiptForProduction(batch.getBatchCode()));

        productWarehouse.setQuantity(productWarehouse.getQuantity() - quantity);
        productWareHouseRepository.save(productWarehouse);
    }

    private Batch createNewBatch(Batch batch) {
        batch.setBatchCode(RandomBatchCodeGenerator.generateBatchCode());

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        if(user == null){
            throw new RuntimeException("Lỗi : không tìm thấy thông tin người dùng!");
        }
        batch.setBatchCreator(user);
        try {
            batch = batchRepository.save(batch);
            return batch;
        }catch (Exception e){
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình tạo lô hàng!"+ e.getMessage());
        }
    }

    private static BatchProduct getBatchProduct(int quantity, ProductWarehouse productWarehouse, Batch batch) {
        Product product = productWarehouse.getProduct();

        BatchProduct batchProduct = new BatchProduct();
        batchProduct.setProduct(product);
        batchProduct.setQuantity(quantity);
        batchProduct.setPrice(0);
        batchProduct.setWeightPerUnit(productWarehouse.getWeightPerUnit());
        batchProduct.setWeight(productWarehouse.getWeightPerUnit() * quantity);
        batchProduct.setUnit(productWarehouse.getUnit());
        batchProduct.setWarehouseId((long) productWarehouse.getWarehouse().getId());
        batchProduct.setDescription("Xuất: Lô hàng của sản phẩm: " + product.getName());
        batchProduct.setBatch(batch);
        return batchProduct;
    }
}
