package com.example.nhatro.service;

import java.io.ByteArrayOutputStream;

import com.example.nhatro.dto.response.BillResponseDTO;

public interface PdfService {
    /**
     * Tạo file PDF hóa đơn từ BillResponseDTO
     * @param bill thông tin hóa đơn
     * @return ByteArrayOutputStream chứa dữ liệu PDF
     */
    ByteArrayOutputStream generateBillPdf(BillResponseDTO bill);
}
