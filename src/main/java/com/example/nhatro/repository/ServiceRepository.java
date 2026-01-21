package com.example.nhatro.repository;

import com.example.nhatro.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    // Tìm tất cả dịch vụ theo hostel
    List<Service> findByHostel_HostelId(Long hostelId);
    
    // Tìm dịch vụ theo tên trong một hostel
    List<Service> findByServiceNameContainingAndHostel_HostelId(String serviceName, Long hostelId);
}
