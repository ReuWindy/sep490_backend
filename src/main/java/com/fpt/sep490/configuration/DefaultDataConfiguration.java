package com.fpt.sep490.configuration;

import com.fpt.sep490.dto.importProductDto;
import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.service.ProductService;
import com.fpt.sep490.service.SupplierService;
import com.fpt.sep490.service.WarehouseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DefaultDataConfiguration {
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final ProductService productService;

    public DefaultDataConfiguration(SupplierRepository supplierRepository, CategoryRepository categoryRepository, WarehouseRepository warehouseRepository, UnitOfMeasureRepository unitOfMeasureRepository, ProductService productService) {
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.productService = productService;
    }

    @Bean
    public CommandLineRunner createDefaultSupplier(SupplierService supplierService) {
        return args -> {
            Supplier existingDefaultSupplier = supplierService.getSupplierByName("Default Supplier");
            if(existingDefaultSupplier == null) {
                Supplier defaultSupplier = new Supplier();
                defaultSupplier.setName("Default Supplier");
                defaultSupplier.setActive(true);
                defaultSupplier.setEmail("default@email.com");
                defaultSupplier.setAddress("defaultAddress");
                defaultSupplier.setPhoneNumber("defaultPhoneNumber");
                defaultSupplier.setContactPerson("Em Tu Chim to");
                supplierRepository.save(defaultSupplier);
            }
        };
    }

    @Bean
    public CommandLineRunner createStaterCategory() {
        return args -> {
            categoryRepository.save(new Category(1L, "Gạo", "Danh muc gao",true));
            categoryRepository.save(new Category(2L, "Cám", "Danh muc cam",true));
            categoryRepository.save(new Category(3L, "Thóc", "Danh muc thoc",true));
            categoryRepository.save(new Category(4L, "Trấu", "Danh muc trau",true));
            categoryRepository.save(new Category(5L, " Thức ăn chăn nuôi", "Danh muc thuc anh chan nuoi",true));
        };
    }

    @Bean
    public CommandLineRunner createDefaultWarehouses(WarehouseService warehouseService) {
        return args -> {
            if(warehouseService.getWarehouseByName("Kho Nguyên Liệu") == null) {
                Warehouse defaultWarehouse1 = new Warehouse();
                defaultWarehouse1.setName("Kho Nguyên Liệu");
                defaultWarehouse1.setLocation("Đây là Kho Nguyên Liệu");
                warehouseRepository.save(defaultWarehouse1);
            }

            if(warehouseService.getWarehouseByName("Kho Bán Hàng") == null) {
                Warehouse defaultWarehouse2 = new Warehouse();
                defaultWarehouse2.setName("Kho Bán Hàng");
                defaultWarehouse2.setLocation("Đây là Kho Bán Hàng");
                warehouseRepository.save(defaultWarehouse2);
            }
        };
    }

    @Bean
    public CommandLineRunner createDefaultUnitOfMeasures() {
        return args -> {
            UnitOfMeasure tan = unitOfMeasureRepository.findByUnitName("tấn");
            if(tan == null) {
                tan = new UnitOfMeasure();
                tan.setUnitName("tấn");
                tan.setConversionFactor(1000);
                unitOfMeasureRepository.save(tan);
            }

            UnitOfMeasure kg = unitOfMeasureRepository.findByUnitName("kg");
            if(kg == null) {
                kg = new UnitOfMeasure();
                kg.setUnitName("kg");
                kg.setConversionFactor(1);
                unitOfMeasureRepository.save(kg);
            }
        };
    }
}