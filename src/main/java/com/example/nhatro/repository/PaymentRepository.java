package com.example.nhatro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nhatro.entity.Payment;
import com.example.nhatro.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Tìm payment theo booking
    Optional<Payment> findByBookingBookingId(Long bookingId);
    
    // Tìm payment theo invoice
    List<Payment> findByInvoiceInvoiceId(Long invoiceId);
    
    // Tìm payment theo status
    List<Payment> findByStatus(PaymentStatus status);
    
    // Tìm payment theo transaction ID
    Optional<Payment> findByTransactionId(String transactionId);
}
