package com.example.nhatro.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.nhatro.entity.Contract;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.entity.Vehicle;
import com.example.nhatro.enums.ContractStatus;
import com.example.nhatro.enums.HostelStatus;
import com.example.nhatro.enums.VehicleStatus;
import com.example.nhatro.repository.ContractRepository;
import com.example.nhatro.repository.HostelRepository;
import com.example.nhatro.repository.VehicleRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled task để tự động kiểm tra và set hợp đồng ACTIVE thành EXPIRED
 * khi đã hết hạn, đồng thời cập nhật hostel và vehicle status
 */
@Component
@Slf4j
public class ContractScheduler {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Chạy mỗi ngày vào lúc 00:00 để kiểm tra hợp đồng hết hạn
     * Cron format: second minute hour day month weekday
     * 0 0 0 * * * = Chạy vào 00:00:00 mỗi ngày
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireContracts() {
        log.info("Starting scheduled task to expire contracts...");

        LocalDate today = LocalDate.now();

        // Tìm tất cả hợp đồng ACTIVE đã hết hạn (endDate < today)
        List<Contract> expiredContracts = contractRepository.findByStatusAndEndDateBefore(
                ContractStatus.ACTIVE, 
                today
        );

        if (expiredContracts.isEmpty()) {
            log.info("No expired contracts found.");
            return;
        }

        log.info("Found {} expired contract(s) to process.", expiredContracts.size());

        int processedCount = 0;
        for (Contract contract : expiredContracts) {
            try {
                // Cập nhật contract status thành EXPIRED
                contract.setStatus(ContractStatus.EXPIRED);
                contract.setNotes((contract.getNotes() != null ? contract.getNotes() + "\n" : "") + 
                                 "Auto-expired on: " + today);
                contractRepository.save(contract);

                // Cập nhật hostel status về AVAILABLE
                Hostel hostel = contract.getHostel();
                if (hostel != null && hostel.getStatus() == HostelStatus.FULL) {
                    hostel.setStatus(HostelStatus.AVAILABLE);
                    hostelRepository.save(hostel);
                    log.info("Contract ID: {} expired, hostel ID: {} set to AVAILABLE", 
                            contract.getContractId(), hostel.getHostelId());
                }

                // Cập nhật vehicle status thành INACTIVE nếu có phương tiện
                Vehicle vehicle = contract.getVehicle();
                if (vehicle != null && vehicle.getStatus() == VehicleStatus.ACTIVE) {
                    vehicle.setStatus(VehicleStatus.INACTIVE);
                    vehicleRepository.save(vehicle);
                    log.info("Contract ID: {} expired, vehicle ID: {} set to INACTIVE", 
                            contract.getContractId(), vehicle.getVehicleId());
                }

                processedCount++;
            } catch (Exception e) {
                log.error("Error processing expired contract ID: {}", contract.getContractId(), e);
            }
        }

        log.info("Completed: Processed {} out of {} expired contract(s).", 
                processedCount, expiredContracts.size());
    }
}
