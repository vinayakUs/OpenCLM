package com.example.contract.repository;

import com.example.contract.ContractResponse;
import com.example.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    List<Contract> findByNameContainingIgnoreCase(String name);

}
