package com.fpt.sep490.service;

import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.model.SupplierProduct;
import com.fpt.sep490.repository.SupplierProductRepository;
import com.fpt.sep490.repository.SupplierRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService{

    private final SupplierRepository supplierRepository;
    private final SupplierProductRepository supplierProductRepository;
    private final DataExporterService dataExporterService;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierProductRepository supplierProductRepository, DataExporterService dataExporterService){
        this.supplierRepository = supplierRepository;
        this.supplierProductRepository = supplierProductRepository;
        this.dataExporterService = dataExporterService;
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
    public Page<Supplier> getSupplierByFilter(String name, String email, String phoneNumber, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<Supplier> specification = SupplierSpecification.hasEmailOrNameOrPhoneNumber(name, phoneNumber, email);
            return supplierRepository.findAll(specification, pageable);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getAllSupplierNames() {
        return supplierRepository.findAll()
                .stream()
                .map(Supplier::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public void disableSupplierAndReassignProducts(Long supplierId, Long defaultSupplierId) {
           Supplier supplierToDisable = supplierRepository.findById(supplierId)
                   .orElseThrow(()-> new RuntimeException("Supplier not found"));
           Supplier defaultSupplier = supplierRepository.findById(defaultSupplierId)
                   .orElseGet(()-> {
                       Supplier newDefaultSupplier = new Supplier();
                       newDefaultSupplier.setName("Default Supplier");
                       return supplierRepository.save(newDefaultSupplier);
                   });
           supplierProductRepository.findBySupplierId(supplierId)
                   .stream()
                   .peek(sp -> sp.setSupplier(defaultSupplier))
                   .forEach(supplierProductRepository::save);
           supplierToDisable.setActive(!supplierToDisable.isActive());
    }

    @Transactional
    public void reinstateSupplier(Long supplierId) {
        Supplier supplierToReinstate = supplierRepository.findById(supplierId)
                .orElseThrow(()-> new RuntimeException("Supplier to reinstate not found"));
        supplierToReinstate.setActive(!supplierToReinstate.isActive());
        List<SupplierProduct> supplierProducts = supplierProductRepository.findByPreviousSupplierId(supplierId);
        supplierProducts.forEach(sp-> sp.setSupplier(supplierToReinstate));
        supplierProductRepository.saveAll(supplierProducts);
        supplierRepository.save(supplierToReinstate);
    }

    @Override
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=suppliers.xlsx";
        response.setHeader(headerKey, headerValue);

        List<Supplier> suppliers = supplierRepository.findAll();
        List<Map<String, Object>> data = suppliers.stream().map(this::getObjectAsMap).collect(Collectors.toList());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataExporterService.exportToExcel(data, outputStream);
        byte[] bytes = outputStream.toByteArray();
        response.getOutputStream().write(bytes);
    }

    private Map<String, Object> getObjectAsMap(Supplier supplier) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", supplier.getId());
        map.put("name", supplier.getName());
        map.put("contactPerson", supplier.getContactPerson());
        map.put("email", supplier.getEmail());
        map.put("phoneNumber", supplier.getPhoneNumber());
        map.put("address", supplier.getAddress());
        return map;
    }
}
