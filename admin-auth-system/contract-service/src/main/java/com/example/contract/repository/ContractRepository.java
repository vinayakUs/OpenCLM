package com.example.contract.repository;

import com.example.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> , JpaSpecificationExecutor<Contract> {

    List<Contract> findByNameContainingIgnoreCase(String name);

}
