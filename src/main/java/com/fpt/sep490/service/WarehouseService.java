package com.fpt.sep490.service;

import com.fpt.sep490.model.Warehouse;

import java.util.List;

public interface WarehouseService {
    List<Warehouse> getAllWarehouse();

    Warehouse getWarehouseById(int id);

    Warehouse getWarehouseByName(String name);

    Warehouse createWarehouse(Warehouse warehouse);

    Warehouse updateWarehouse(Warehouse warehouse);

    Warehouse deleteWarehouse(int id);
}
