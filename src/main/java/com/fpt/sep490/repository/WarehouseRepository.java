package com.fpt.sep490.repository;

import com.fpt.sep490.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByName(String name);

    @Query("SELECT w.name, w.id FROM Warehouse w")
    List<Object[]> getWarehouseNameAndId();

    @Query("SELECT w.name FROM Warehouse w")
    List<String> findAllWarehouseNames();
}