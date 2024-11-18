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

    public ProductionOrderServiceImpl(FinishedProductService finishedProductService, ProductWarehouseService productWarehouseService, ProductionOrderRepository productionOrderRepository) {
        this.finishedProductService = finishedProductService;
        this.productWarehouseService = productWarehouseService;
        this.productionOrderRepository = productionOrderRepository;
    }

    @Override
    public ProductionOrder createProductionOrder(ProductionOrderDto dto) {
        ProductWarehouse p = productWarehouseService.getById(dto.getProductWarehouseId());

        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setProductionCode(RandomProductionOrderCodeGenerator.generateOrderCode());
        productionOrder.setDescription("Đơn sản xuất cho của nguyên liệu: "+ p.getProduct().getName());
        productionOrder.setProductionDate(dto.getProductionDate());
        productionOrder.setStatus(StatusEnum.PENDING);
        productionOrder.setQuantity(dto.getQuantity());
        productionOrder.setDefectiveQuantity(0);
        productionOrder.setDefectReason("Chưa hoàn thành sản xuất");
        productionOrder.setProductWarehouse(p);
        productionOrder.setFinishedProducts(finishedProductService.getFinishedProductForProduction(p.getProduct().getId()));

        productionOrderRepository.save(productionOrder);
        return productionOrder;
    }

    @Override
    public List<ProductionOrder> getAllProductionOrders() {
        return productionOrderRepository.findAll();
    }

    @Override
    public ProductionOrder getProductionOrderById(Long id) {
        return productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm đầu ra với id:"));
    }

    @Override
    public ProductionOrder updateProductionOrder(Long id, ProductionOrderDto dto) {
        ProductWarehouse pw = productWarehouseService.getById(dto.getProductWarehouseId());
        ProductionOrder p = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm đầu ra với id:"));
        ProductWarehouse productwarehouse = productWarehouseService.getById(dto.getProductWarehouseId());
        p.setProductWarehouse(productwarehouse);
        p.setDescription(dto.getDescription());
        p.setProductionDate(dto.getProductionDate());
        Set<FinishedProduct> finishedProductSet = finishedProductService.getFinishedProductForProduction(pw.getProduct().getId());
        p.setFinishedProducts(finishedProductSet);
        return null;
    }

    @Override
    public ProductionOrder deleteProductionOrder(Long id) {
        ProductionOrder p = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm đầu ra với id:"));
        productionOrderRepository.delete(p);
        return p;
    }

    @Override
    public void confirmProductionOrder(Long id) {
        ProductionOrder p = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm đầu ra với id:"));
        p.setStatus(StatusEnum.IN_PROCESS);
        productionOrderRepository.save(p);
        productWarehouseService.exportProductWarehouseToProduction(p.getProductWarehouse().getId(), (int) p.getQuantity());

    }

    @Override
    public void ConfirmProductionOrderDone(Long id, ImportProductionDto dto, Double defectiveQuantity, String defectiveReason) {
        ProductionOrder p = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm đầu ra với id:"));
        p.setStatus(StatusEnum.COMPLETED);
        p.setDescription("Đã hoàn thành sản xuất");
        p.setDefectiveQuantity(defectiveQuantity);
        p.setDefectReason(defectiveReason);
        p.setCompletionDate(new Date());
        productionOrderRepository.save(p);
        productWarehouseService.importProductWarehouseToProduction(p.getId(), dto);
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

        Page<ProductionOrder> productionOrders = productionOrderRepository.findAll(spec, pageable);

        return productionOrders.map(order -> ProductionOrderView.builder()
                .productName(order.getProductWarehouse().getProduct().getName()) // Sửa ở đây
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
