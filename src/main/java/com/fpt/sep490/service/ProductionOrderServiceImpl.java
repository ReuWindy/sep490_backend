package com.fpt.sep490.service;

import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.*;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.FinishedProductRepository;
import com.fpt.sep490.repository.ProductRepository;
import com.fpt.sep490.repository.ProductionOrderRepository;
import com.fpt.sep490.repository.UserRepository;
import com.fpt.sep490.utils.RandomProductionOrderCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {
    private final ProductWarehouseService productWarehouseService;
    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final FinishedProductRepository finishedProductRepository;
    private final UserRepository userRepository;

    public ProductionOrderServiceImpl(ProductWarehouseService productWarehouseService, ProductionOrderRepository productionOrderRepository, ProductRepository productRepository, FinishedProductRepository finishedProductRepository, UserRepository userRepository) {
        this.productWarehouseService = productWarehouseService;
        this.productionOrderRepository = productionOrderRepository;
        this.productRepository = productRepository;
        this.finishedProductRepository = finishedProductRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProductionOrder createProductionOrder(ProductionOrderDto dto) {
        ProductWarehouse productWarehouse = productWarehouseService.getById(dto.getProductWarehouseId());

        if (dto.getQuantity() > productWarehouse.getQuantity()) {
            throw new RuntimeException("Không đủ hàng trong kho");
        }

        User user = userRepository.findByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        ProductionOrder productionOrder = ProductionOrder.builder()
                .productionCode(RandomProductionOrderCodeGenerator.generateOrderCode())
                .description(dto.getDescription())
                .productionDate(new Date())
                .status(StatusEnum.PENDING)
                .quantity(dto.getQuantity())
                .createBy(user)
                .productWarehouse(productWarehouse)
                .build();

        Set<FinishedProduct> finishedProducts = new HashSet<>();
        int totalRatio = 0;
        for (FinishedProductDto productDto : dto.getFinishedProductDtoList()) {
            FinishedProduct finishedProduct = new FinishedProduct();
            Product p = productRepository.findById(productDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            finishedProduct.setProduct(p);
            finishedProduct.setProportion(productDto.getProportion());
            finishedProduct.setActive(true);
            finishedProduct.setQuantity(productDto.getQuantity());
            finishedProduct.setProductionOrder(productionOrder);
            totalRatio += (int) productDto.getProportion();
            finishedProducts.add(finishedProduct);
        }

        if (totalRatio > 100 || totalRatio <= 0) {
            throw new RuntimeException("Tổng tỉ lệ không được vượt quá 100%");
        }

        productionOrder.setFinishedProducts(finishedProducts);

        return productionOrderRepository.save(productionOrder);
    }

    @Override
    public ProductionOrder finishProductionOrder(List<ProductionFinishDto> dto, Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu sản xuất"));

        for (ProductionFinishDto finishDto : dto) {
            FinishedProduct finishedProduct = finishedProductRepository.findById(finishDto.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phẩm"));
            finishedProduct.setDefectQuantity(finishDto.getDefectQuantity());
            finishedProduct.setFinalQuantity(finishDto.getRealQuantity());
            finishedProduct.setNote(finishDto.getDescription());
        }

        productionOrder.setStatus(StatusEnum.COMPLETED);
        return productionOrderRepository.save(productionOrder);
    }


    @Override
    public List<ProductionOrder> getAllProductionOrders() {
        return productionOrderRepository.findAll();
    }

    @Override
    public ProductionOrderView getProductionOrderById(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu kiểm kho với ID: " + id));

        List<ProductionOrderView.FinishedProductDetail> finishedProductDetails = new ArrayList<>();
        if (productionOrder.getFinishedProducts() != null) {
            finishedProductDetails = productionOrder.getFinishedProducts().stream()
                    .map(fp -> ProductionOrderView.FinishedProductDetail.builder()
                            .productId(fp.getProduct().getId())
                            .quantity(fp.getQuantity())
                            .productCode(fp.getProduct().getProductCode())
                            .productName(fp.getProduct().getName())
                            .note(fp.getNote())
                            .id(fp.getId())
                            .realQuantity(fp.getFinalQuantity())
                            .defectQuantity(fp.getDefectQuantity())
                            .proportion(fp.getProportion())
                            .productWarehouses(fp.getProduct().getProductWarehouses())
                            .build())
                    .toList();
        }

        return ProductionOrderView.builder()
                .id(productionOrder.getId())
                .note(productionOrder.getDescription())
                .productId(productionOrder.getProductWarehouse().getId())
                .productionCode(productionOrder.getProductionCode())
                .productName(productionOrder.getProductWarehouse().getProduct().getName())
                .quantity(productionOrder.getQuantity())
                .unit(productionOrder.getProductWarehouse().getUnit())
                .status(productionOrder.getStatus().name())
                .weightPerUnit(productionOrder.getProductWarehouse().getWeightPerUnit())
                .productionDate(productionOrder.getProductionDate())
                .completionDate(productionOrder.getCompletionDate())
                .finishedProducts(finishedProductDetails)
                .creator(productionOrder.getCreateBy())
                .build();
    }

    @Override
    public ProductionOrder updateProductionOrder(Long id, ProductionOrderDto dto) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu sản xuất"));

        ProductWarehouse productWarehouse = productWarehouseService.getById(dto.getProductWarehouseId());

        if (dto.getQuantity() > productWarehouse.getQuantity()) {
            throw new RuntimeException("Không đủ hàng trong kho");
        }

        productionOrder.setProductWarehouse(productWarehouse);
        productionOrder.setDescription(dto.getDescription());
        productionOrder.setQuantity(dto.getQuantity());
        productionOrder.setStatus(StatusEnum.PENDING);
        productionOrder.getFinishedProducts().clear();
        Set<FinishedProduct> finishedProducts = new HashSet<>();
        int totalRatio = 0;

        for (FinishedProductDto productDto : dto.getFinishedProductDtoList()) {
            FinishedProduct finishedProduct = new FinishedProduct();
            Product p = productRepository.findById(productDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            finishedProduct.setProduct(p);
            finishedProduct.setProportion(productDto.getProportion());
            finishedProduct.setActive(true);
            finishedProduct.setQuantity(productDto.getQuantity());
            finishedProduct.setProductionOrder(productionOrder);
            totalRatio += (int) productDto.getProportion();
            finishedProducts.add(finishedProduct);
        }

        if (totalRatio > 100 || totalRatio <= 0) {
            throw new RuntimeException("Tổng tỉ lệ không được vượt quá 100%");
        }

        productionOrder.getFinishedProducts().addAll(finishedProducts);

        return productionOrderRepository.save(productionOrder);
    }

    @Override
    public ProductionOrder deleteProductionOrder(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu sản xuất"));
        productionOrderRepository.delete(productionOrder);
        return productionOrder;
    }

    @Override
    public void cancelProductionOrder(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu sản xuất"));

        if (productionOrder.getStatus() != StatusEnum.PENDING) {
            throw new IllegalStateException("Chỉ có thể hủy đơn sản xuất ở trạng thái chờ xác nhận.");
        }

        productionOrder.setStatus(StatusEnum.CANCELED);
        productionOrderRepository.save(productionOrder);
    }

    @Override
    public void confirmProductionOrder(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu sản xuất"));

        if (productionOrder.getStatus() != StatusEnum.PENDING) {
            throw new IllegalStateException("Chỉ có thể xác nhận đơn sản xuất ở trạng thái chờ xác nhận.");
        }

        productWarehouseService.exportProductWarehouseToProduction(
                productionOrder.getProductWarehouse().getId(),
                (int) productionOrder.getQuantity()
        );

        productionOrder.setStatus(StatusEnum.IN_PROCESS);
        productionOrderRepository.save(productionOrder);
    }


    @Override
    public void ConfirmProductionOrderDone(Long id, List<ProductionCompleteDto> dtos) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu sản xuất"));

        if (productionOrder.getStatus() != StatusEnum.COMPLETED) {
            throw new IllegalStateException("Chỉ có thể hoàn thành đơn sản xuất ở trạng thái hoàn thành sản xuất.");
        }
        productWarehouseService.importProductWarehouseToProduction(dtos);

        productionOrder.setDescription("Đã hoàn thành sản xuất");
        productionOrder.setStatus(StatusEnum.CONFIRMED);
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
                .id(order.getId())
                .creator(order.getCreateBy())
                .productionCode(order.getProductionCode())
                .productName(order.getProductWarehouse().getProduct().getName())
                .quantity(order.getQuantity())
                .status(String.valueOf(order.getStatus()))
                .productionDate(order.getProductionDate())
                .completionDate(order.getCompletionDate())
                .finishedProducts(order.getFinishedProducts().stream()
                        .map(finishedProduct -> ProductionOrderView.FinishedProductDetail.builder()
                                .productName(finishedProduct.getProduct().getName())
                                .proportion(finishedProduct.getProportion())
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }

}

