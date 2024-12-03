package com.fpt.sep490.service;

import com.fpt.sep490.dto.ContractDto;
import com.fpt.sep490.model.Contract;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.repository.ContractRepository;
import com.fpt.sep490.repository.CustomerRepository;
import com.fpt.sep490.utils.ContractNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;

    public ContractServiceImpl(ContractRepository contractRepository, CustomerRepository customerRepository) {
        this.contractRepository = contractRepository;
        this.customerRepository = customerRepository;
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
    public Contract createContract(ContractDto contractDto) {
        Contract newContract = new Contract();
        Customer customer = customerRepository.findById(contractDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        newContract.setContractNumber(ContractNumberGenerator.generateContractCode());
        newContract.setContractDuration(contractDto.getDuration());
        newContract.setContractTime(new Date());
        newContract.setConfirmed(false);
        newContract.setCustomer(customer);
        newContract.setPdfFilePath(contractDto.getPdfFilePath());
        contractRepository.save(newContract);
        return newContract;
    }

    @Override
    public Contract updateContract(ContractDto contract) {
        Contract existingContract = contractRepository.findById(contract.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));
        if (existingContract != null) {
            existingContract.setImageFilePath(contract.getImageFilePath());
            if (contract.getConfirmationDate() == null) {
                existingContract.setConfirmationDate(new Date());
            } else {
                existingContract.setConfirmationDate(contract.getConfirmationDate());
            }
            existingContract.setConfirmed(true);
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
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "contractTime"));
        Specification<Contract> specification = ContractSpecification.hasContractNumberOrName(contractNumber, name);
        return contractRepository.findAll(specification, pageable);
    }
}