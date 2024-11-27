package com.fpt.sep490.repository;

import com.fpt.sep490.model.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByContractNumber(String contractNumber);

    Page<Contract> findAll(Specification<Contract> specification, Pageable pageable);
}