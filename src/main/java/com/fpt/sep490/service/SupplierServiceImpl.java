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
        if(name != null && !name.trim().isEmpty()) {
            Optional<Supplier> supplier = supplierRepository.findByName(name);
            return supplier.orElse(null);
        }else{
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình tạo nhà cung cấp mới!");
        }
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        try {
        Supplier newSupplier = new Supplier();
        if(supplier.getName().trim().isEmpty()){
            throw new RuntimeException("Lỗi: Tên nhà cung cấp không được để trống!");
        }
        newSupplier.setName(supplier.getName());
        newSupplier.setContactPerson(supplier.getContactPerson());
        if(supplier.getEmail().isEmpty()){
            throw new RuntimeException("Lỗi: Email của nhà cung cấp không được để trống!");
        }
        Optional<Supplier> existingSupplier = supplierRepository.findByEmail(supplier.getEmail());
        if (existingSupplier.isPresent()) {
                throw new RuntimeException("Lỗi: Email của nhà cung cấp đã tồn tại trong hệ thống!");
        }
        newSupplier.setEmail(supplier.getEmail());
        if(!supplier.getPhoneNumber().matches("^[0-9]+$")){
            throw new RuntimeException("Lỗi: Số điện thoại của nhà cung cấp chỉ bao gồm số từ 0 đến 9 !");
        }
        newSupplier.setPhoneNumber(supplier.getPhoneNumber());
        newSupplier.setAddress(supplier.getAddress());

            supplierRepository.save(newSupplier);
            return newSupplier;
        }catch (Exception e){
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình tạo nhà cung cấp mới! "+ e.getMessage());
        }
    }

    @Override
    public Supplier updateSupplier(Supplier supplier) {
        Supplier existingSupplier = supplierRepository.findById(supplier.getId())
                .orElseThrow(() -> new RuntimeException("Lỗi: nhà cung cấp không tồn tại!"));

        try {
            if (supplier.getName() != null && !supplier.getName().trim().isEmpty()) {
                existingSupplier.setName(supplier.getName());
            } else if (supplier.getName() != null) {
                throw new RuntimeException("Lỗi: Tên nhà cung cấp không được để trống!");
            }

            if (supplier.getEmail() != null && !supplier.getEmail().isEmpty()) {
                Optional<Supplier> existingSupplierEmail = supplierRepository.findByEmail(supplier.getEmail());
                if (existingSupplierEmail.isPresent()) {
                    throw new RuntimeException("Lỗi: Email của nhà cung cấp đã tồn tại trong hệ thống!");
                }
                existingSupplier.setEmail(supplier.getEmail());
            } else if (supplier.getEmail() != null) {
                throw new RuntimeException("Lỗi: Email của nhà cung cấp không được để trống!");
            }
            if (supplier.getPhoneNumber() != null) {
                if (!supplier.getPhoneNumber().matches("^[0-9]+$")) {
                    throw new RuntimeException("Lỗi: Số điện thoại của nhà cung cấp chỉ bao gồm số từ 0 đến 9!");
                }
                existingSupplier.setPhoneNumber(supplier.getPhoneNumber());
            }
            if (supplier.getContactPerson() != null) {
                existingSupplier.setContactPerson(supplier.getContactPerson());
            }
            if (supplier.getAddress() != null) {
                existingSupplier.setAddress(supplier.getAddress());
            }
            supplierRepository.save(existingSupplier);
            return existingSupplier;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi: Xảy ra lỗi trong quá trình cập nhật nhà cung cấp! " + e.getMessage());
        }
    }


    @Override
    public List<Supplier> getAllActiveSupplier() {
        return supplierRepository.findAll().stream().takeWhile(Supplier::isActive).collect(Collectors.toList());
    }

    @Override
    public Page<Supplier> getSupplierByFilter(String name, String email, String phoneNumber, Boolean status, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<Supplier> specification = SupplierSpecification.hasEmailOrNameOrPhoneNumber(name, phoneNumber, email, status);
            return supplierRepository.findAll(specification, pageable);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getAllSupplierNames() {
        return supplierRepository.findAll()
                .stream()
                .takeWhile(Supplier::isActive)
                .map(Supplier::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public void disableSupplierAndReassignProducts(Long supplierId, Long defaultSupplierId) {
           Supplier supplierToDisable = supplierRepository.findById(supplierId)
                   .orElseThrow(()-> new RuntimeException("Lỗi:  Không tìm thấy nhà cung cấp"));
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

    @Override
    public Supplier disableSupplier(Long supplierId) {
        Supplier supplierToDisable = supplierRepository.findById(supplierId)
                .orElseThrow(()-> new RuntimeException("Lỗi:  Không tìm thấy nhà cung cấp"));
        supplierToDisable.setActive(false);
        return supplierToDisable;
    }

    @Override
    public Supplier enableSupplier(Long supplierId) {
        Supplier supplierToEnable = supplierRepository.findById(supplierId)
                .orElseThrow(()-> new RuntimeException("Lỗi:  Không tìm thấy nhà cung cấp"));
        supplierToEnable.setActive(true);
        return supplierToEnable;
    }

//    @Transactional
//    public void reinstateSupplier(Long supplierId) {
//        Supplier supplierToReinstate = supplierRepository.findById(supplierId)
//                .orElseThrow(()-> new RuntimeException("Supplier to reinstate not found"));
//        supplierToReinstate.setActive(!supplierToReinstate.isActive());
//        List<SupplierProduct> supplierProducts = supplierProductRepository.findByPreviousSupplierId(supplierId);
//        supplierProducts.forEach(sp-> sp.setSupplier(supplierToReinstate));
//        supplierProductRepository.saveAll(supplierProducts);
//        supplierRepository.save(supplierToReinstate);
//    }

//    @Override
//    public void exportToExcel(HttpServletResponse response) throws IOException {
//        response.setContentType("application/vnd.ms-excel");
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=suppliers.xlsx";
//        response.setHeader(headerKey, headerValue);
//
//        List<Supplier> suppliers = supplierRepository.findAll();
//        List<Map<String, Object>> data = suppliers.stream().map(this::getObjectAsMap).collect(Collectors.toList());
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        dataExporterService.exportToExcel(data, outputStream);
//        byte[] bytes = outputStream.toByteArray();
//        response.getOutputStream().write(bytes);
//    }

//    private Map<String, Object> getObjectAsMap(Supplier supplier) {
//        Map<String, Object> map = new LinkedHashMap<>();
//        map.put("id", supplier.getId());
//        map.put("name", supplier.getName());
//        map.put("contactPerson", supplier.getContactPerson());
//        map.put("email", supplier.getEmail());
//        map.put("phoneNumber", supplier.getPhoneNumber());
//        map.put("address", supplier.getAddress());
//        return map;
//    }
}
