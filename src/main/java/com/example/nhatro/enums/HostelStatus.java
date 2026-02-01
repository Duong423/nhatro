package com.example.nhatro.enums;

public enum HostelStatus {
    AVAILABLE,      // Còn phòng trống, đang cho thuê
    RESERVED,       // Đang giữ chỗ (chờ owner xác nhận booking)
    FULL,          // Đã đủ người thuê
    CLOSED,        // Tạm đóng cửa
    UNDER_RENOVATION  // Đang sửa chữa/cải tạo
}
