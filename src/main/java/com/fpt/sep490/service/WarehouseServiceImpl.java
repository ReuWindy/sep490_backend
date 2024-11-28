package com.fpt.sep490.service;

import com.fpt.sep490.model.Warehouse;
import com.fpt.sep490.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public List<Warehouse> getAllWarehouse() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse getWarehouseById(int id) {
        Optional<Warehouse> warehouse = warehouseRepository.findById((long) id);
        return warehouse.orElse(null);
    }

    @Override
    public Warehouse getWarehouseByName(String name) {
        Optional<Warehouse> warehouse = warehouseRepository.findByName(name);
        return warehouse.orElse(null);
    }

    @Override
    public Warehouse createWarehouse(Warehouse warehouse) {
        String warehouseName = warehouse.getName();
        if ("Kho nguyen lieu".equals(warehouseName) || "Kho hang hoa".equals(warehouseName)) {
            Warehouse newWarehouse = new Warehouse();
            newWarehouse.setName(warehouseName);
            newWarehouse.setLocation(warehouse.getLocation());
            warehouseRepository.save(newWarehouse);
            return newWarehouse;
        }
        return null;
    }

    @Override
    public Warehouse updateWarehouse(Warehouse warehouse) {
        Warehouse existingWarehouse = warehouseRepository.findById(warehouse.getId()).orElse(null);
        if (existingWarehouse != null) {
            String warehouseName = warehouse.getName();
            if ("Kho nguyen lieu".equals(warehouseName) || "Kho hang hoa".equals(warehouseName)) {
                existingWarehouse.setName(warehouseName);
                existingWarehouse.setLocation(warehouse.getLocation());
                warehouseRepository.save(existingWarehouse);
                return existingWarehouse;
            }
        }
        return null;
    }

    @Override
    public Warehouse deleteWarehouse(int id) {
        return null;
    }
}
