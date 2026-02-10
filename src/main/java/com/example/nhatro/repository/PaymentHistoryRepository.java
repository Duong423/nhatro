package com.example.nhatro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.nhatro.entity.PaymentHistory;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    // Tìm lịch sử thanh toán theo bill ID
    Optional<PaymentHistory> findByBillIdRef(Long billIdRef);

    // Tìm lịch sử thanh toán theo owner
    List<PaymentHistory> findByOwnerIdOrderByPaymentDateDesc(Long ownerId);

    // Tìm lịch sử thanh toán theo tenant
    List<PaymentHistory> findByTenantIdOrderByPaymentDateDesc(Long tenantId);

    // Tìm lịch sử thanh toán theo room code
    List<PaymentHistory> findByRoomCodeAndOwnerIdOrderByPaymentDateDesc(String roomCode, Long ownerId);

    // Tìm lịch sử thanh toán theo contract
    List<PaymentHistory> findByContractIdOrderByPaymentDateDesc(Long contractId);

    // Tìm lịch sử thanh toán theo tháng/năm
    List<PaymentHistory> findByBillingMonthAndBillingYearAndOwnerIdOrderByPaymentDateDesc(
            Integer billingMonth, Integer billingYear, Long ownerId);

    // Tổng thu nhập theo owner
    @Query("SELECT COALESCE(SUM(ph.totalAmount), 0) FROM PaymentHistory ph WHERE ph.ownerId = :ownerId")
    java.math.BigDecimal getTotalRevenueByOwnerId(@Param("ownerId") Long ownerId);

    // Tổng thu nhập theo owner theo tháng/năm
    @Query("SELECT COALESCE(SUM(ph.totalAmount), 0) FROM PaymentHistory ph WHERE ph.ownerId = :ownerId AND ph.billingMonth = :month AND ph.billingYear = :year")
    java.math.BigDecimal getTotalRevenueByOwnerIdAndMonth(
            @Param("ownerId") Long ownerId, @Param("month") Integer month, @Param("year") Integer year);
}
