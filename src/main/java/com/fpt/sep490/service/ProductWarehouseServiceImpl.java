package com.fpt.sep490.service;

import com.fpt.sep490.dto.ImportProductionDto;
import com.fpt.sep490.dto.ProductWarehouseDto;
import com.fpt.sep490.model.BatchProduct;
import com.fpt.sep490.model.FinishedProduct;
import com.fpt.sep490.model.ProductWarehouse;
import com.fpt.sep490.model.ProductionOrder;
import com.fpt.sep490.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductWarehouseServiceImpl implements ProductWarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final BatchProductRepository batchProductRepository;
    private final ProductWareHouseRepository productWareHouseRepository;
    private final ProductionOrderRepository productionOrderRepository;

    public ProductWarehouseServiceImpl(WarehouseRepository warehouseRepository, BatchProductRepository batchProductRepository, ProductWareHouseRepository productWareHouseRepository, ProductionOrderRepository productionOrderRepository) {
        this.warehouseRepository = warehouseRepository;
        this.batchProductRepository = batchProductRepository;
        this.productWareHouseRepository = productWareHouseRepository;
        this.productionOrderRepository = productionOrderRepository;
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
    public List<ProductWarehouse> getAllProducts() {
        Long id = 2L;
        return productWareHouseRepository.findProductWarehousesByWarehouseId(id);
    }

    @Override
    public List<ProductWarehouse> getAllIngredients() {
        Long id = 1L;
        return productWareHouseRepository.findProductWarehousesByWarehouseId(id);    }

    @Override
    public ProductWarehouse createProductWarehouse(ProductWarehouseDto productWarehouse) {
        return null;
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

//        long warehouseId = batchProduct.getBatch().getWarehouse().getId();
//        Warehouse defaultWarehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new RuntimeException("Warehouse không tìm thấy với id: " + warehouseId ));
//        productWarehouse.setWarehouse(defaultWarehouse);
//        productWarehouse.setBatchCode(batchProduct.getBatch().getBatchCode());
//        productWareHouseRepository.save(productWarehouse);
        return productWarehouse;
    }

    @Override
    public Page<ProductWarehouse> getPageProductWarehouseByFilter(double minPrice, double maxPrice, String unit, double weightPerUnit, int categoryId, int supplierId, int warehouseId, String sortDirection, String priceOrder, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public void importProductWarehouseToProduction(long productionId, ImportProductionDto dto) {
        ProductionOrder order = productionOrderRepository.findById(productionId)
                .orElseThrow(()-> new RuntimeException("Không tìm thấy đơn sả xuất với id: "+ productionId));
        Set<FinishedProduct> finishedProducts = order.getFinishedProducts();

        for (FinishedProduct finishedProduct : finishedProducts) {
            ProductWarehouse productWarehouse = new ProductWarehouse();
            productWarehouse.setBatchCode(order.getProductionCode());
            productWarehouse.setWarehouse(warehouseRepository.findById((long) dto.getWarehouseId()).orElseThrow(() -> new RuntimeException("Không tìm thấy kho")));
            productWarehouse.setWeightPerUnit(dto.getWeightPerUnit());
            productWarehouse.setUnit(dto.getUnit());
            productWarehouse.setProduct(finishedProduct.getProduct());
            int quantity = (int) Math.round((order.getQuantity() / 100.0) * finishedProduct.getProportion());

            productWarehouse.setQuantity(quantity);
            productWarehouse.setWeight(quantity * dto.getWeightPerUnit());
            productWareHouseRepository.save(productWarehouse);
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

        productWarehouse.setQuantity(productWarehouse.getQuantity() - quantity);

        productWareHouseRepository.save(productWarehouse);
    }

//    @Override
//    public Page<ProductWarehouse> getPageProductWarehouseByFilter(double minPrice, double maxPrice, String unit,
//                                                                  double weightPerUnit, int categoryId, int supplierId,
//                                                                  int warehouseId, String sortDirection, String priceOrder,
//                                                                  int pageNumber, int pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
//        Specification<ProductWarehouse> spec = ProductWareHouseSpecification.hasUnitOrHasWeightPerUnitOrCategoryOrSupplierOrWarehouse(unit, weightPerUnit, categoryId, supplierId, warehouseId, sortDirection, priceOrder);
//        return productWareHouseRepository.findAll(spec, pageable);
//    }


}
