package com.fpt.sep490.configuration;

import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.repository.SupplierRepository;
import com.fpt.sep490.service.SupplierService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultDataConfiguration {
    private final SupplierRepository supplierRepository;

    public DefaultDataConfiguration(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
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
}
