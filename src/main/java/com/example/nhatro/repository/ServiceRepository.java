package com.example.nhatro.repository;

import com.example.nhatro.entity.ServiceHostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceHostel, Long> {
    // Tìm tất cả dịch vụ theo hostel
    List<ServiceHostel> findByHostel_HostelId(Long hostelId);
    
    // Tìm dịch vụ theo tên trong một hostel
    List<ServiceHostel> findByServiceNameContainingAndHostel_HostelId(String serviceName, Long hostelId);
}
