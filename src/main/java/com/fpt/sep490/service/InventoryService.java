package com.fpt.sep490.service;

import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.model.Inventory;

public interface InventoryService {
    Inventory createInventory(InventoryDto inventoryDto);

    Inventory getInventoryById(long inventoryId);
}
