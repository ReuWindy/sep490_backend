package com.fpt.sep490.configuration;

import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.repository.CategoryRepository;
import com.fpt.sep490.repository.SupplierRepository;
import com.fpt.sep490.service.SupplierService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultDataConfiguration {
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;

    public DefaultDataConfiguration(SupplierRepository supplierRepository, CategoryRepository categoryRepository) {
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
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
}
