package com.fpt.sep490.service;

import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.model.Inventory;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface InventoryService {
    Inventory createInventory(InventoryDto inventoryDto);

    Page<InventoryDto> getInventoryByFilter(String inventoryCode, Date startDate, Date endDate, int pageNumber, int pageSize);

    Inventory getInventoryById(long inventoryId);

    String confirmAndAddSelectedProductToInventory(Long inventoryId, InventoryDto inventoryDto);

    Inventory deleteInventory(Long id);
}
