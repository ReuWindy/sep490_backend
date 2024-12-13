package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.utils.RandomBatchCodeGenerator;
import com.fpt.sep490.utils.RandomInventoryCodeGenerator;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductWareHouseRepository productWareHouseRepository;
    private final WarehouseReceiptRepository warehouseReceiptRepository;
    private final BatchRepository batchRepository;
    private final BatchProductRepository batchProductRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, WarehouseRepository warehouseRepository, ProductRepository productRepository, UserRepository userRepository, ProductWareHouseRepository productWareHouseRepository, WarehouseReceiptRepository warehouseReceiptRepository, BatchRepository batchRepository, BatchProductRepository batchProductRepository) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productWareHouseRepository = productWareHouseRepository;
        this.warehouseReceiptRepository = warehouseReceiptRepository;
        this.batchRepository = batchRepository;
        this.batchProductRepository = batchProductRepository;
    }

    @Override
    public Inventory createInventory(InventoryDto inventoryDto, String username) {
        Inventory createdInventory = new Inventory();
        Warehouse warehouse = warehouseRepository.findById(inventoryDto.getWarehouseId()).orElseThrow(() -> new RuntimeException("Không tìm thấy kho"));
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        createdInventory.setWarehouse(warehouse);
        createdInventory.setInventoryDate(inventoryDto.getInventoryDate());
        createdInventory.setInventoryCode(RandomInventoryCodeGenerator.generateInventoryCode());
        createdInventory.setInventoryDate(new Date());
        createdInventory.setCreateBy(user);

        Set<InventoryDetail> details = inventoryDto.getInventoryDetails().stream().map(detailDto -> {
            InventoryDetail inventoryDetail = new InventoryDetail();
            Product product = productRepository.findById(detailDto.getProductId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            inventoryDetail.setProduct(product);
            inventoryDetail.setQuantity(detailDto.getQuantity());
            inventoryDetail.setDescription(detailDto.getDescription());
            inventoryDetail.setInventory(createdInventory);
            inventoryDetail.setUnit(detailDto.getUnit());
            inventoryDetail.setWeightPerUnit(detailDto.getWeightPerUnit());

            ProductWarehouse productWarehouse = productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                            detailDto.getProductId(), inventoryDto.getWarehouseId(), detailDto.getUnit(), detailDto.getWeightPerUnit())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm phù hợp trong kho"));
            inventoryDetail.setSystemQuantity(productWarehouse.getQuantity());
            inventoryDetail.setQuantity_discrepancy(detailDto.getQuantity() - productWarehouse.getQuantity());

            return inventoryDetail;
        }).collect(Collectors.toSet());

        createdInventory.setInventoryDetails(details);
        createdInventory.setStatus(StatusEnum.PENDING);

        inventoryRepository.save(createdInventory);

        return createdInventory;
    }

    @Override
    public Page<InventoryDto> getInventoryByFilter(String inventoryCode, Date startDate, Date endDate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<Inventory> specification = InventorySpecification.hasCode(inventoryCode)
                    .and(InventorySpecification.isInventoryDateBetween(startDate, endDate));

            Page<Inventory> inventoryPage = inventoryRepository.findAll(specification, pageable);

            List<InventoryDto> dtos = inventoryPage.getContent().stream()
                    .map(InventoryDto::toDto)
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, inventoryPage.getTotalElements());
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Inventory getInventoryById(long inventoryId) {
        return inventoryRepository.findById(inventoryId).orElseThrow(() -> new RuntimeException("Inventory Not Found"));
    }

    @Transactional
    @Override
    public String confirmAndAddSelectedProductToInventory(Long inventoryId, InventoryDto inventoryDto) {
        Inventory inventory = inventoryRepository.findById(inventoryId).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu kiểm kho phù hợp!"));

        for (var detail : inventoryDto.getInventoryDetails()) {
            InventoryDetail inventoryDetail = inventory.getInventoryDetails().stream()
                    .filter(d -> d.getProduct().getId() == detail.getProductId()
                            && d.getUnit().equals(detail.getUnit())
                            && d.getWeightPerUnit() == detail.getWeightPerUnit())
                    .findFirst()
                    .orElse(null);   // Không tạo mới, nếu không tìm thấy thì bỏ qua

            // Nếu tìm thấy InventoryDetail, cập nhật nó
            if (inventoryDetail != null) {
                int discrepancy = detail.getQuantity() - detail.getSystemQuantity();
                inventoryDetail.setQuantity(detail.getQuantity());
                inventoryDetail.setDescription(detail.getDescription());
                inventoryDetail.setSystemQuantity(detail.getSystemQuantity());
                inventoryDetail.setQuantity_discrepancy(discrepancy);
            } else {
                // Nếu không tìm thấy InventoryDetail tương ứng, có thể bỏ qua hoặc báo lỗi
                throw new RuntimeException("Lỗi: Không tìm thấy chi tiết kiểm kho phù hợp.");
            }

        }
        inventory.setStatus(StatusEnum.COMPLETED);

        try {
            inventoryRepository.save(inventory);
            Batch importBatch = new Batch();
            Batch exportBatch = new Batch();
            importBatch.setImportDate(new Date());
            importBatch.setReceiptType(ReceiptType.IMPORT);
            importBatch.setBatchCode(RandomBatchCodeGenerator.generateBatchCode());
            importBatch.setBatchCreator(inventory.getCreateBy());
            importBatch.setBatchStatus("Đã xác nhận");
            exportBatch.setImportDate(new Date());
            exportBatch.setReceiptType(ReceiptType.EXPORT);
            exportBatch.setBatchCode(RandomBatchCodeGenerator.generateBatchCode());
            exportBatch.setBatchCreator(inventory.getCreateBy());
            exportBatch.setBatchStatus("Đã xác nhận");
            batchRepository.save(importBatch);
            batchRepository.save(exportBatch);
            Set<BatchProduct> importBatchProducts = new HashSet<>();
            Set<BatchProduct> exportBatchProducts = new HashSet<>();
            for (var detail : inventory.getInventoryDetails()) {
                ProductWarehouse productWarehouse = productWareHouseRepository.findByProductIdAndWarehouseIdAndUnitAndWeightPerUnit(
                                detail.getProduct().getId(), inventoryDto.getWarehouseId(), detail.getUnit(), detail.getWeightPerUnit())
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm phù hợp trong kho"));
                BatchProduct batchProduct = new BatchProduct();
                batchProduct.setProduct(productWarehouse.getProduct());
                batchProduct.setQuantity(Math.abs(detail.getQuantity() - detail.getSystemQuantity()));
                batchProduct.setAdded(true);
                batchProduct.setPrice(0.0);
                batchProduct.setUnit(detail.getUnit());
                batchProduct.setWeightPerUnit(detail.getWeightPerUnit());
                batchProduct.setWarehouseId(productWarehouse.getWarehouse().getId());
                if (detail.getQuantity_discrepancy() < 0) {
                    batchProduct.setBatch(exportBatch);
                    batchProduct.setDescription("Sản phẩm bị hao hụt trong quá trình kiểm kho");
                    exportBatchProducts.add(batchProduct);
                } else if (detail.getQuantity_discrepancy() > 0) {
                    batchProduct.setBatch(importBatch);
                    batchProduct.setDescription("Sản phẩm dư trong quá trình kiểm kho");
                    importBatchProducts.add(batchProduct);
                }
            }
            if (!exportBatchProducts.isEmpty()) {
                exportBatch.setBatchProducts(exportBatchProducts);
                batchRepository.save(exportBatch);
                WarehouseReceipt receipt = new WarehouseReceipt();
                receipt.setReceiptDate(new Date());
                receipt.setReceiptType(ReceiptType.EXPORT);
                receipt.setBatch(exportBatch);
                receipt.setIsPay(true);
                receipt.setReceiptReason("Xuất sản phẩm hao hụt");
                receipt.setDocument("N/A");
                warehouseReceiptRepository.save(receipt);
                exportBatch.setWarehouseReceipt(receipt);
                batchRepository.save(exportBatch);
            } else {
                batchRepository.delete(exportBatch);
            }
            if (!importBatchProducts.isEmpty()) {
                importBatch.setBatchProducts(importBatchProducts);
                batchRepository.save(importBatch);
                WarehouseReceipt receipt = new WarehouseReceipt();
                receipt.setReceiptDate(new Date());
                receipt.setReceiptType(ReceiptType.IMPORT);
                receipt.setBatch(importBatch);
                receipt.setIsPay(true);
                receipt.setReceiptReason("Nhập sản phẩm dư");
                receipt.setDocument("N/A");
                warehouseReceiptRepository.save(receipt);
                importBatch.setWarehouseReceipt(receipt);
                batchRepository.save(importBatch);
            } else {
                batchRepository.delete(importBatch);
            }
            return "Xác nhận phiếu kiểm kho và cập nhật số lượng thành công !";
        } catch (Exception e) {
            throw new RuntimeException("Xảy ra lỗi trong quá trình xác nhận phiếu kiểm kho !");
        }
    }

    @Override
    public Inventory deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu chi!"));
        if (inventory.getStatus() == StatusEnum.COMPLETED) {
            throw new IllegalStateException("Không thể phiếu kiểm kho này  vì phiếu đã được xác nhận!");
        } else if (inventory.getStatus() == StatusEnum.CANCELED) {
            inventoryRepository.delete(inventory);
        } else {
            inventory.setStatus(StatusEnum.CANCELED);
            inventoryRepository.save(inventory);
        }
        return inventory;
    }
}
