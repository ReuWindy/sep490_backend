package com.fpt.sep490.dto;

import com.fpt.sep490.model.ProductWarehouse;
import lombok.Data;

@Data
public class ProductWarehouseDto {
    private Long id;
    private Double price;
    private Double weight;
    private Double weightPerUnit;
    private String unit;
    private String batchCode;
    private String description;
    private Long productId;
    private Long warehouseId;
    private Integer quantity;
    private ProductDto product;

    public static ProductWarehouseDto toDto(ProductWarehouse productWarehouse) {
        ProductWarehouseDto dto = new ProductWarehouseDto();
        dto.setId(productWarehouse.getId());
        dto.setPrice(productWarehouse.getImportPrice());
        dto.setWeight(productWarehouse.getWeight());
        dto.setUnit(productWarehouse.getUnit());
        dto.setWeightPerUnit(productWarehouse.getWeightPerUnit());
        dto.setBatchCode(productWarehouse.getBatchCode());
        dto.setQuantity(productWarehouse.getQuantity());
        ProductDto productDto = new ProductDto();
        productDto.setId(productWarehouse.getProduct().getId());
        productDto.setProductCode(productWarehouse.getProduct().getProductCode());
        productDto.setName(productWarehouse.getProduct().getName());
        productDto.setDescription(productWarehouse.getProduct().getDescription());
        productDto.setDeleted(productWarehouse.getProduct().getIsDeleted());
        dto.setProduct(productDto);
        return dto;
    }
}
