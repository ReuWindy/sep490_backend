package com.fpt.sep490.service;

import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierServiceImpl implements SupplierService{

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository){
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> getAllSupplier() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier getSupplierById(int id) {
        Optional<Supplier> supplier = supplierRepository.findById((long) id);
        return supplier.orElse(null);
    }

    @Override
    public Supplier getSupplierByName(String name) {
        Optional<Supplier> warehouse = supplierRepository.findByName(name);
        return warehouse.orElse(null);
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        Supplier newSupplier = new Supplier();
        newSupplier.setName(supplier.getName());
        newSupplier.setContactPerson(supplier.getContactPerson());
        newSupplier.setEmail(supplier.getEmail());
        newSupplier.setPhoneNumber(supplier.getPhoneNumber());
        newSupplier.setAddress(supplier.getAddress());
        supplierRepository.save(newSupplier);
        return newSupplier;
    }

    @Override
    public Supplier updateSupplier(Supplier supplier) {
        Supplier existingSupplier = supplierRepository.findById(supplier.getId()).orElse(null);
        if(existingSupplier != null){
            existingSupplier.setName(supplier.getName());
            existingSupplier.setContactPerson(supplier.getContactPerson());
            existingSupplier.setEmail(supplier.getEmail());
            existingSupplier.setPhoneNumber(supplier.getPhoneNumber());
            existingSupplier.setAddress(supplier.getAddress());
            supplierRepository.save(existingSupplier);
            return existingSupplier;
        }
        return null;
    }

    @Override
    public Supplier deleteSupplier(int id) {
        return null;
    }

    @Override
    public Page<Supplier> getSupplierByFilter(String name, String email, String phoneNumber, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<Supplier> specification = SupplierSpecification.hasEmailOrNameOrPhoneNumber(name, phoneNumber, email);
            return supplierRepository.findAll(specification, pageable);
        } catch (Exception e) {
            return null;
        }
    }

}
