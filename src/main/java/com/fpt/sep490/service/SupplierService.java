package com.fpt.sep490.service;

import com.fpt.sep490.model.Supplier;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;


import java.io.IOException;
import java.util.List;

public interface SupplierService {

    List<Supplier> getAllSupplier();
    Supplier getSupplierById(int id);
    Supplier getSupplierByName(String name);
    Supplier createSupplier(Supplier supplier);
    Supplier updateSupplier(Supplier supplier);
    Page<Supplier> getSupplierByFilter(String name, String email, String phoneNumber, int pageNumber, int pageSize );
    List<String> getAllSupplierNames();
    void disableSupplierAndReassignProducts(Long supplierId, Long defaultSupplierId);
    void reinstateSupplier(Long supplierId);
    void exportToExcel(HttpServletResponse response) throws IOException;
}
