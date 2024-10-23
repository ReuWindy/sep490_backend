package com.fpt.sep490.service;

import com.fpt.sep490.model.Contract;
import com.fpt.sep490.model.User;
import com.fpt.sep490.repository.ContractRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService{
    private final ContractRepository contractRepository;

    public ContractServiceImpl(ContractRepository contractRepository){
        this.contractRepository = contractRepository;
    }
    @Override
    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    @Override
    public Contract getContractById(int id) {
        Optional<Contract> contract = contractRepository.findById((long) id);
        return contract.orElse(null);
    }

    @Override
    public Contract getContractByContractNumber(String contractNumber) {
        Optional<Contract> contract = contractRepository.findByContractNumber(contractNumber);
        return contract.orElse(null);
    }

    @Override
    public Contract createContract(Contract contract) {
        Contract newContract = new Contract();
        newContract.setContractNumber(contract.getContractNumber());
        newContract.setContractTime(contract.getContractTime());
        newContract.setAmount(contract.getAmount());
        newContract.setPdfFilePath(contract.getPdfFilePath());
        newContract.setImageFilePath(contract.getImageFilePath());
        newContract.setConfirmed(contract.isConfirmed());
        newContract.setConfirmationDate(contract.getConfirmationDate());
        contractRepository.save(newContract);
        return newContract;
    }

    @Override
    public Contract updateContract(Contract contract) {
        Contract existingContract = contractRepository.findById(contract.getId()).orElse(null);
        if(existingContract != null){
            existingContract.setContractNumber(contract.getContractNumber());
            existingContract.setContractTime(contract.getContractTime());
            existingContract.setAmount(contract.getAmount());
            existingContract.setPdfFilePath(contract.getPdfFilePath());
            existingContract.setImageFilePath(contract.getImageFilePath());
            existingContract.setConfirmed(contract.isConfirmed());
            existingContract.setConfirmationDate(contract.getConfirmationDate());
            contractRepository.save(existingContract);
            return existingContract;
        }
        return null;
    }

    @Override
    public Contract deleteContract(int id) {
        return null;
    }

    @Override
    public Page<Contract> getContractByFilter(String contractNumber, String name, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Specification<Contract> specification = ContractSpecification.hasContractNumberOrName(contractNumber, name);
        return contractRepository.findAll(specification, pageable);
    }
}
