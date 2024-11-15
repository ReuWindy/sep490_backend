package com.fpt.sep490.service;

import com.fpt.sep490.dto.ContractDto;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Contract;
import com.fpt.sep490.model.Customer;
import com.fpt.sep490.model.User;
import com.fpt.sep490.repository.ContractRepository;
import com.fpt.sep490.repository.CustomerRepository;
import com.fpt.sep490.utils.ContractNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService{
    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;

    public ContractServiceImpl(ContractRepository contractRepository, CustomerRepository customerRepository){
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
            Contract savedContract = contractRepository.save(existingContract);
            return savedContract;
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
