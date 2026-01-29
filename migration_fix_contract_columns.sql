-- Migration: Drop và tạo lại bảng contracts
-- Xóa toàn bộ và tạo lại với cấu trúc đúng

USE nhatro;

-- Tạm thời tắt foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- Bước 1: Xóa các bảng liên quan
DROP TABLE IF EXISTS contract_services;
DROP TABLE IF EXISTS contracts;

-- Bước 2: Tạo lại bảng contracts với cấu trúc đúng
CREATE TABLE contracts (
    contract_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    tenant_id BIGINT NOT NULL,
    tenant_name VARCHAR(100) NOT NULL,
    tenant_phone VARCHAR(20) NOT NULL,
    tenant_email VARCHAR(100),
    owner_id BIGINT NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    owner_phone VARCHAR(20) NOT NULL,
    hostel_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    monthly_rent DECIMAL(10, 2) NOT NULL,
    deposit_amount DECIMAL(10, 2) NOT NULL,
    electricity_cost_per_unit DECIMAL(10, 2),
    water_cost_per_unit DECIMAL(10, 2),
    service_fee DECIMAL(10, 2),
    payment_cycle VARCHAR(20),
    number_of_tenants INT,
    terms TEXT,
    signed_date DATE,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_contracts_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    CONSTRAINT fk_contracts_tenant FOREIGN KEY (tenant_id) REFERENCES users(id),
    CONSTRAINT fk_contracts_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_contracts_hostel FOREIGN KEY (hostel_id) REFERENCES hostels(hostel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bật lại foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Kiểm tra kết quả
DESCRIBE contracts;
