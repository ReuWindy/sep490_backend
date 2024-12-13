package com.fpt.sep490.configuration;

import com.fpt.sep490.model.*;
import com.fpt.sep490.repository.*;
import com.fpt.sep490.service.ProductService;
import com.fpt.sep490.service.SupplierService;
import com.fpt.sep490.service.WarehouseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultDataConfiguration {
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final PriceRepository priceRepository;
    private final ProductService productService;
    private final EmployeeRoleRepository employeeRoleRepository;

    public DefaultDataConfiguration(SupplierRepository supplierRepository, CategoryRepository categoryRepository, WarehouseRepository warehouseRepository, UnitOfMeasureRepository unitOfMeasureRepository, ProductService productService, PriceRepository priceRepository, EmployeeRoleRepository employeeRoleRepository) {
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.productService = productService;
        this.priceRepository = priceRepository;
        this.employeeRoleRepository = employeeRoleRepository;
    }

    @Bean
    public CommandLineRunner createDefaultSupplier(SupplierService supplierService) {
        return args -> {
            Supplier existingDefaultSupplier = supplierService.getSupplierByName("Kho Thanh Quang");
            if (existingDefaultSupplier == null) {
                Supplier defaultSupplier = new Supplier();
                defaultSupplier.setName("Kho Thanh Quang");
                defaultSupplier.setActive(true);
                defaultSupplier.setEmail("thanhquang@email.com");
                defaultSupplier.setAddress("Khu 1, Xã Bản Nguyên, Huyện Lâm Thao, Phú Thọ");
                defaultSupplier.setPhoneNumber("0394696259");
                defaultSupplier.setContactPerson("Phạm Trung");
                supplierRepository.save(defaultSupplier);
            }
        };
    }

    @Bean
    public CommandLineRunner createStaterCategory() {
        return args -> {
            categoryRepository.save(new Category(1L, "Gạo", "Danh mục gạo", true));
            categoryRepository.save(new Category(2L, "Cám", "Danh mục cám", true));
            categoryRepository.save(new Category(3L, "Thóc", "Danh mục thóc", true));
            categoryRepository.save(new Category(4L, "Ngô", "Danh mục ngô", true));
            categoryRepository.save(new Category(5L, " Thức ăn chăn nuôi", "Danh mục thức ăn chăn nuôi", true));
        };
    }

    @Bean
    public CommandLineRunner createDefaultWarehouses(WarehouseService warehouseService) {
        return args -> {
            if (warehouseService.getWarehouseByName("Kho Nguyên Liệu") == null) {
                Warehouse defaultWarehouse1 = new Warehouse();
                defaultWarehouse1.setName("Kho Nguyên Liệu");
                defaultWarehouse1.setLocation("Đây là Kho Nguyên Liệu");
                warehouseRepository.save(defaultWarehouse1);
            }

            if (warehouseService.getWarehouseByName("Kho Bán Hàng") == null) {
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
            if (tan == null) {
                tan = new UnitOfMeasure();
                tan.setUnitName("tấn");
                tan.setConversionFactor(1000);
                unitOfMeasureRepository.save(tan);
            }

            UnitOfMeasure kg = unitOfMeasureRepository.findByUnitName("kg");
            if (kg == null) {
                kg = new UnitOfMeasure();
                kg.setUnitName("kg");
                kg.setConversionFactor(1);
                unitOfMeasureRepository.save(kg);
            }
            UnitOfMeasure ta = unitOfMeasureRepository.findByUnitName("tạ");
            if (ta == null) {
                ta = new UnitOfMeasure();
                ta.setUnitName("tạ");
                ta.setConversionFactor(100);
                unitOfMeasureRepository.save(ta);
            }
        };
    }

    @Bean
    public CommandLineRunner createDefaultPrices(){
        return args -> {
            Price defaultPrice = priceRepository.findByName("Bảng giá chung");
            if(defaultPrice == null){
                defaultPrice = new Price();
                defaultPrice.setName("Bảng giá chung");
                priceRepository.save(defaultPrice);
            }
        };
    }
}