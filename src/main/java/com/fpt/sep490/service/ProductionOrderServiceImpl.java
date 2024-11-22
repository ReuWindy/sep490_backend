package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.ImportProductionDto;
import com.fpt.sep490.dto.ProductionOrderDto;
import com.fpt.sep490.dto.ProductionOrderView;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.ProductionOrderRepository;
import com.fpt.sep490.utils.RandomProductionOrderCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final FinishedProductService finishedProductService;
    private final ProductWarehouseService productWarehouseService;
    private final ProductionOrderRepository productionOrderRepository;

    public ProductionOrderServiceImpl(
            FinishedProductService finishedProductService,
            ProductWarehouseService productWarehouseService,
            ProductionOrderRepository productionOrderRepository) {
        this.finishedProductService = finishedProductService;
        this.productWarehouseService = productWarehouseService;
        this.productionOrderRepository = productionOrderRepository;
    }

    @Override
    public ProductionOrder createProductionOrder(ProductionOrderDto dto) {
        ProductWarehouse productWarehouse = productWarehouseService.getById(dto.getProductWarehouseId());

        ProductionOrder productionOrder = ProductionOrder.builder()
                .productionCode(RandomProductionOrderCodeGenerator.generateOrderCode())
                .description("Đơn sản xuất cho nguyên liệu: " + productWarehouse.getProduct().getName())
                .productionDate(dto.getProductionDate())
                .status(StatusEnum.PENDING)
                .quantity(dto.getQuantity())
                .defectiveQuantity(0)
                .defectReason("Chưa hoàn thành sản xuất")
                .productWarehouse(productWarehouse)
                .build();

        Set<FinishedProduct> finishedProducts = finishedProductService.getFinishedProductForProduction(productWarehouse.getProduct().getId());
        if (finishedProducts != null && !finishedProducts.isEmpty()) {
            productionOrder.setFinishedProducts(finishedProducts);
        }

        return productionOrderRepository.save(productionOrder);
    }


    @Override
    public List<ProductionOrder> getAllProductionOrders() {
        return productionOrderRepository.findAll();
    }

    @Override
    public ProductionOrder getProductionOrderById(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ProductionOrder với ID: " + id));
        Set<FinishedProduct> finishedProducts = finishedProductService.getFinishedProductForProduction(productionOrder.getProductWarehouse().getProduct().getId());
        productionOrder.setFinishedProducts(finishedProducts);
        return productionOrder;
    }

    @Override
    public ProductionOrder updateProductionOrder(Long id, ProductionOrderDto dto) {
        ProductionOrder productionOrder = getProductionOrderById(id);
        ProductWarehouse productWarehouse = productWarehouseService.getById(dto.getProductWarehouseId());

        productionOrder.setProductWarehouse(productWarehouse);
        productionOrder.setDescription(dto.getDescription());
        productionOrder.setProductionDate(dto.getProductionDate());

        Set<FinishedProduct> finishedProducts = finishedProductService.getFinishedProductForProduction(productWarehouse.getProduct().getId());
        if (finishedProducts != null && !finishedProducts.isEmpty()) {
            productionOrder.setFinishedProducts(finishedProducts);
        }

        return productionOrderRepository.save(productionOrder);
    }

    @Override
    public ProductionOrder deleteProductionOrder(Long id) {
        ProductionOrder productionOrder = getProductionOrderById(id);
        productionOrderRepository.delete(productionOrder);
        return productionOrder;
    }

    @Override
    public void confirmProductionOrder(Long id) {
        ProductionOrder productionOrder = getProductionOrderById(id);

        if (productionOrder.getStatus() != StatusEnum.PENDING) {
            throw new IllegalStateException("Chỉ có thể xác nhận đơn sản xuất ở trạng thái PENDING.");
        }

        productWarehouseService.exportProductWarehouseToProduction(
                productionOrder.getProductWarehouse().getId(),
                (int) productionOrder.getQuantity()
        );

        productionOrder.setStatus(StatusEnum.IN_PROCESS);
        productionOrderRepository.save(productionOrder);

    }


    @Override
    public void ConfirmProductionOrderDone(Long id, ImportProductionDto dto, Double defectiveQuantity, String defectiveReason) {
        ProductionOrder productionOrder = getProductionOrderById(id);

        if (productionOrder.getStatus() != StatusEnum.IN_PROCESS) {
            throw new IllegalStateException("Chỉ có thể hoàn thành đơn sản xuất ở trạng thái IN_PROCESS.");
        }
        productWarehouseService.importProductWarehouseToProduction(productionOrder.getId(), dto);

        productionOrder.setDescription("Đã hoàn thành sản xuất");
        productionOrder.setDefectiveQuantity(defectiveQuantity);
        productionOrder.setDefectReason(defectiveReason);
        productionOrder.setStatus(StatusEnum.COMPLETED);
        productionOrder.setCompletionDate(new Date());
        productionOrderRepository.save(productionOrder);
    }

    @Override
    public Page<ProductionOrderView> getProductionOrders(
            String status,
            Date startDate,
            Date endDate,
            String productName,
            Pageable pageable) {

        Specification<ProductionOrder> spec = Specification.where(null);

        if (status != null && !status.isEmpty()) {
            spec = spec.and(ProductionOrderSpecification.hasStatus(StatusEnum.valueOf(status)));
        }

        if (startDate != null) {
            spec = spec.and(ProductionOrderSpecification.hasProductionDateAfter(startDate));
        }

        if (endDate != null) {
            spec = spec.and(ProductionOrderSpecification.hasProductionDateBefore(endDate));
        }

        if (productName != null && !productName.isEmpty()) {
            spec = spec.and(ProductionOrderSpecification.hasProductName(productName));
        }

        Page<ProductionOrder> productionOrders = productionOrderRepository.findAllWithFinishedProducts(spec, pageable);


        return productionOrders.map(order -> ProductionOrderView.builder()
                .productName(order.getProductWarehouse().getProduct().getName())
                .quantity(order.getQuantity())
                .productionDate(order.getProductionDate())
                .completionDate(order.getCompletionDate())
                .finishedProducts(order.getFinishedProducts().stream()
                        .map(finishedProduct -> ProductionOrderView.FinishedProductDetail.builder()
                                .productName(finishedProduct.getProduct().getName())
                                .proportion(finishedProduct.getProportion())
                                .build())
                        .collect(Collectors.toList()))
                .defectiveQuantity(order.getDefectiveQuantity())
                .defectiveReason(order.getDefectReason())
                .status(order.getStatus().name())
                .build());
    }

}

