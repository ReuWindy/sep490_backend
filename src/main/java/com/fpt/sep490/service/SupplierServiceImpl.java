package com.fpt.sep490.service;

import com.fpt.sep490.model.Supplier;
import com.fpt.sep490.model.SupplierProduct;
import com.fpt.sep490.repository.SupplierProductRepository;
import com.fpt.sep490.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
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
        Supplier existingSupplier = supplierRepository.findById(supplier.getId()).orElse(null);
        if (existingSupplier != null) {
            if (supplier.getName().isBlank()) {
                throw new RuntimeException("Tên nhà cung cấp không được để trống");
            }
            if (supplier.getContactPerson().isBlank()) {
                throw new RuntimeException("Tên người đại diện không được để trống");
            }
            if (supplier.getEmail().isBlank()) {
                throw new RuntimeException("Địa chỉ email không được để trống");
            }
            String email = supplier.getEmail();
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

            Pattern pattern = Pattern.compile(emailRegex);
            if (!pattern.matcher(email).matches()) {
                throw new RuntimeException("Địa chỉ email không hợp lệ");
            }
            if (supplier.getPhoneNumber().isBlank()) {
                throw new RuntimeException("Số điện thoại không được để trống");
            }
            String phoneNumber = supplier.getPhoneNumber();
            String phoneNumberRegex = "^(\\+84|0)[3-9]{1}[0-9]{8}$";

            Pattern phonePattern = Pattern.compile(phoneNumberRegex);
            if (!phonePattern.matcher(phoneNumber).matches()) {
                throw new RuntimeException("Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10 hoặc 11 chữ số");
            }
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

//    @Transactional
//    public void disableSupplierAndReassignProducts(Long supplierId, Long defaultSupplierId) {
//           Supplier supplierToDisable = supplierRepository.findById(supplierId)
//                   .orElseThrow(()-> new RuntimeException("Lỗi:  Không tìm thấy nhà cung cấp"));
//           Supplier defaultSupplier = supplierRepository.findById(defaultSupplierId)
//                   .orElseGet(()-> {
//                       Supplier newDefaultSupplier = new Supplier();
//                       newDefaultSupplier.setName("Default Supplier");
//                       return supplierRepository.save(newDefaultSupplier);
//                   });
//           supplierProductRepository.findBySupplierId(supplierId)
//                   .stream()
//                   .peek(sp -> sp.setSupplier(defaultSupplier))
//                   .forEach(supplierProductRepository::save);
//           supplierToDisable.setActive(!supplierToDisable.isActive());
//    }

    @Override
    public Supplier disableSupplier(Long supplierId) {
        Supplier supplierToDisable = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));
        supplierToDisable.setActive(false);
        return supplierToDisable;
    }

    @Override
    public Supplier enableSupplier(Long supplierId) {
        Supplier supplierToEnable = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));
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
