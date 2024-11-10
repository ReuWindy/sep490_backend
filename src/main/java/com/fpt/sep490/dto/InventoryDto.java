package com.fpt.sep490.dto;

import com.fpt.sep490.model.InventoryDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
       private long warehouseId;
       private Date inventoryDate;
       Set<InventoryDetailDto> inventoryDetails;
}
