package com.fpt.sep490.dto;

import com.fpt.sep490.model.Inventory;
import com.fpt.sep490.model.InventoryDetail;
import com.fpt.sep490.model.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
    private Long id;
    private String inventoryCode;
    private long warehouseId;
    private Date inventoryDate;
    private Warehouse warehouse;
    private String status;
    private Set<InventoryDetailDto> inventoryDetails;
    private String username;

    public static InventoryDto toDto(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setId(inventory.getId());
        dto.setInventoryCode(inventory.getInventoryCode());
        dto.setWarehouse(inventory.getWarehouse());
        dto.setInventoryDate(inventory.getInventoryDate());
        dto.setStatus(inventory.getStatus().toString());
        Set<InventoryDetailDto> inventoryDetailDtos = new HashSet<>();
        for (InventoryDetail invenDetail : inventory.getInventoryDetails()) {
            InventoryDetailDto inventoryDetailDto = new InventoryDetailDto();
            inventoryDetailDto.setWeightPerUnit(invenDetail.getWeightPerUnit());
            inventoryDetailDto.setProductId(invenDetail.getProduct().getId());
            inventoryDetailDto.setDescription(invenDetail.getDescription());
            inventoryDetailDto.setUnit(invenDetail.getUnit());
            inventoryDetailDto.setQuantity(invenDetail.getQuantity());
            inventoryDetailDto.setSystemQuantity(invenDetail.getSystemQuantity());
            inventoryDetailDto.setQuantity_discrepancy(invenDetail.getQuantity_discrepancy());
            ProductDto productDto = new ProductDto();
            productDto.setId(invenDetail.getProduct().getId());
            productDto.setName(invenDetail.getProduct().getName());
            productDto.setProductCode(invenDetail.getProduct().getProductCode());
            inventoryDetailDto.setProductDto(productDto);
            inventoryDetailDtos.add(inventoryDetailDto);
        }
        dto.setInventoryDetails(inventoryDetailDtos);
        return dto;
    }
}
