package com.example.nhatro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nhatro.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByContractContractId(Long contractId);
}
