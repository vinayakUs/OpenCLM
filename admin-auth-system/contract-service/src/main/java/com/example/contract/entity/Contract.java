package com.example.contract.entity;

import com.example.contract.comman.ContractStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "contract")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true ,callSuper = false)
public class Contract extends  AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "name",nullable = false)
    @NonNull
    private String name;

    @Column(name = "workflow_id",nullable = false)
    @NonNull
    private UUID workflowId;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractStatus status = ContractStatus.DRAFT;

    /*
     * ======================
     * Domain behavior
     * ======================
     */
    public void changeStatus(ContractStatus newStatus){
        this.status = newStatus;
    }


}
