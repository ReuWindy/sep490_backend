package com.fpt.sep490.service;

import com.fpt.sep490.model.Supplier;
import org.springframework.data.domain.Page;


import java.util.List;

public interface SupplierService {

    List<Supplier> getAllSupplier();
    Supplier getSupplierById(int id);
    Supplier getSupplierByName(String name);
    Supplier createSupplier(Supplier supplier);
    Supplier updateSupplier(Supplier supplier);
    Supplier deleteSupplier(int id);
    Page<Supplier> getSupplierByFilter(String name, String email, String phoneNumber, int pageNumber, int pageSize );

}
