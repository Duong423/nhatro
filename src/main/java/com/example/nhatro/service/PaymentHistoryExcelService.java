package com.example.nhatro.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.example.nhatro.dto.response.PaymentHistoryResponseDTO;

public interface PaymentHistoryExcelService {

    /**
     * Xuất lịch sử thanh toán theo tháng/năm ra file Excel
     * 
     * @param histories danh sách lịch sử thanh toán
     * @param month     tháng
     * @param year      năm
     * @return ByteArrayOutputStream chứa nội dung file Excel
     */
    ByteArrayOutputStream exportPaymentHistoryToExcel(List<PaymentHistoryResponseDTO> histories, int month, int year);
}
