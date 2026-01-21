package com.example.nhatro.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "contract_services")
@Data
public class ContractService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    private Integer quantity; // VD: 2 chiếc xe máy
    private LocalDate startDate;
}