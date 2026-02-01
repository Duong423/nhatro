package com.example.nhatro.service;

import java.util.List;

import com.example.nhatro.dto.request.BillRequestDTO.ConfirmPaymentRequestDTO;
import com.example.nhatro.dto.request.BillRequestDTO.CreateBillRequestDTO;
import com.example.nhatro.dto.request.BillRequestDTO.UpdateBillRequestDTO;
import com.example.nhatro.dto.response.BillResponseDTO;

public interface BillService {
    
    // Tạo hóa đơn thủ công
    BillResponseDTO createBill(CreateBillRequestDTO request);
    
    // Lấy hóa đơn theo ID
    BillResponseDTO getBillById(Long billId);
    
    // Lấy danh sách hóa đơn của owner
    List<BillResponseDTO> getBillsByOwner();
    
    // Lấy danh sách hóa đơn của tenant
    List<BillResponseDTO> getBillsByTenant();
    
    // Lấy hóa đơn theo contract
    List<BillResponseDTO> getBillsByContract(Long contractId);
    
    // Lấy hóa đơn theo roomCode
    List<BillResponseDTO> getBillsByRoomCode(String roomCode);
    
    // Cập nhật hóa đơn
    BillResponseDTO updateBill(Long billId, UpdateBillRequestDTO request);
    
    // Xác nhận thanh toán
    BillResponseDTO confirmPayment(Long billId, ConfirmPaymentRequestDTO request);
    
    // Hủy hóa đơn
    void cancelBill(Long billId);
    
    // Tự động tạo hóa đơn hàng tháng (được gọi bởi scheduler)
    void generateMonthlyBills();
    
    // Tự động cập nhật status = OVERDUE cho hóa đơn quá hạn
    void updateOverdueBills();
}
