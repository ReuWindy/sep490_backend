package com.fpt.sep490.service;

import com.fpt.sep490.model.Contract;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ContractService {
    List<Contract> getAllContracts();
    Contract getContractById(int id);
    Contract getContractByContractNumber(String contractNumber);
    Contract createContract(Contract contract);
    Contract updateContract(Contract contract);
    Contract deleteContract(int id);
    Page<Contract> getContractByFilter(String contractNumber, String name, int pageNumber, int pageSize);
}
