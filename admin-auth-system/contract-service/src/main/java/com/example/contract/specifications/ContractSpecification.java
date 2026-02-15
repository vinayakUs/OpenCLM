package com.example.contract.specifications;

import com.example.common.dto.contract.ContractStatus;
import com.example.contract.entity.Contract;
import com.example.contract.entity.Contract_;
import org.springframework.data.jpa.domain.Specification;


public class ContractSpecification {

    public static Specification<Contract> filterByStatus(ContractStatus status)
    {
        return  (root, query, criteriaBuilder) -> {
                if(status == null){
                    return null;
                }
         return criteriaBuilder.equal(root.get(Contract_.status), status);
        };

    }

    public static Specification<Contract> filterByName(String name)
    {
        return (root, query, criteriaBuilder) -> {
          if(name == null){
              return null;
          }
            return criteriaBuilder.like(root.get(Contract_.name), "%" + name + "%");
        };
    }
}
