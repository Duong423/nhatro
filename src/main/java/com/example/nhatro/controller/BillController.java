package com.example.nhatro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.dto.request.BillRequestDTO.ConfirmPaymentRequestDTO;
import com.example.nhatro.dto.request.BillRequestDTO.CreateBillRequestDTO;
import com.example.nhatro.dto.request.BillRequestDTO.UpdateBillRequestDTO;
import com.example.nhatro.dto.response.BillResponseDTO;
import com.example.nhatro.service.BillService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    /**
     * Tạo hóa đơn thủ công (Owner only)
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<BillResponseDTO> createBill(@RequestBody CreateBillRequestDTO request) {
        BillResponseDTO response = billService.createBill(request);
        return ApiResponse.<BillResponseDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Bill created successfully")
                .result(response)
                .build();
    }

    /**
     * Lấy chi tiết hóa đơn theo ID (Owner và Tenant)
     */
    @GetMapping("/{billId}")
    @PreAuthorize("hasAnyRole('OWNER', 'TENANT')")
    public ApiResponse<BillResponseDTO> getBillById(@PathVariable Long billId) {
        BillResponseDTO response = billService.getBillById(billId);
        return ApiResponse.<BillResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Bill retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Lấy danh sách hóa đơn của Owner
     */
    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<List<BillResponseDTO>> getBillsByOwner() {
        List<BillResponseDTO> response = billService.getBillsByOwner();
        return ApiResponse.<List<BillResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Bills retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Lấy danh sách hóa đơn của Tenant
     */
    @GetMapping("/tenant")
    @PreAuthorize("hasRole('TENANT')")
    public ApiResponse<List<BillResponseDTO>> getBillsByTenant() {
        List<BillResponseDTO> response = billService.getBillsByTenant();
        return ApiResponse.<List<BillResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Bills retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Lấy hóa đơn theo contract (Owner only)
     */
    @GetMapping("/contract/{contractId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<List<BillResponseDTO>> getBillsByContract(@PathVariable Long contractId) {
        List<BillResponseDTO> response = billService.getBillsByContract(contractId);
        return ApiResponse.<List<BillResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Bills retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Lấy hóa đơn theo room code (Owner only)
     */
    @GetMapping("/room/{roomCode}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<List<BillResponseDTO>> getBillsByRoomCode(@PathVariable String roomCode) {
        List<BillResponseDTO> response = billService.getBillsByRoomCode(roomCode);
        return ApiResponse.<List<BillResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Bills retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Cập nhật hóa đơn (Owner only)
     */
    @PutMapping("/{billId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<BillResponseDTO> updateBill(
            @PathVariable Long billId,
            @RequestBody UpdateBillRequestDTO request) {
        BillResponseDTO response = billService.updateBill(billId, request);
        return ApiResponse.<BillResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Bill updated successfully")
                .result(response)
                .build();
    }

    /**
     * Xác nhận thanh toán hóa đơn (Owner và Tenant)
     */
    @PostMapping("/{billId}/confirm-payment")
    @PreAuthorize("hasAnyRole('OWNER', 'TENANT')")
    public ApiResponse<BillResponseDTO> confirmPayment(
            @PathVariable Long billId,
            @RequestBody ConfirmPaymentRequestDTO request) {
        BillResponseDTO response = billService.confirmPayment(billId, request);
        return ApiResponse.<BillResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Payment confirmed successfully")
                .result(response)
                .build();
    }

    /**
     * Hủy hóa đơn (Owner only)
     */
    @DeleteMapping("/{billId}")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<Void> cancelBill(@PathVariable Long billId) {
        billService.cancelBill(billId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Bill canceled successfully")
                .build();
    }

    /**
     * Tự động tạo hóa đơn hàng tháng (Admin/System only - for manual trigger)
     */
    @PostMapping("/generate-monthly")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<String> generateMonthlyBills() {
        billService.generateMonthlyBills();
        return ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Monthly bills generated successfully")
                .build();
    }
}
