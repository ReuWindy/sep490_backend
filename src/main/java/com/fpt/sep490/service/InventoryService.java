package com.fpt.sep490.service;

import com.fpt.sep490.dto.BatchProductSelection;
import com.fpt.sep490.dto.InventoryDto;
import com.fpt.sep490.model.Inventory;

import java.util.List;

public interface InventoryService {
    Inventory createInventory(InventoryDto inventoryDto);

    Inventory getInventoryById(long inventoryId);

    String confirmAndAddSelectedProductToInventory(Long inventoryId, InventoryDto inventoryDto);
}
