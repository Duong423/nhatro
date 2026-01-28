-- Migration: Tạo bảng contracts cho hệ thống hợp đồng thuê nhà trọ

-- Tắt foreign key check để drop các bảng
SET FOREIGN_KEY_CHECKS = 0;

-- Drop các bảng liên quan trước (foreign key dependencies)
DROP TABLE IF EXISTS invoice_details;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS contract_services;
DROP TABLE IF EXISTS room_members;
DROP TABLE IF EXISTS contracts;

-- Bật lại foreign key check
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE contracts (
    contract_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    tenant_id BIGINT NOT NULL,
    landlord_id BIGINT NOT NULL,
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
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_contract_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_contract_tenant FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_contract_landlord FOREIGN KEY (landlord_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_contract_hostel FOREIGN KEY (hostel_id) REFERENCES hostels(hostel_id) ON DELETE CASCADE
);

-- Tạo index để tăng tốc truy vấn
CREATE INDEX idx_contract_booking ON contracts(booking_id);
CREATE INDEX idx_contract_tenant ON contracts(tenant_id);
CREATE INDEX idx_contract_landlord ON contracts(landlord_id);
CREATE INDEX idx_contract_hostel ON contracts(hostel_id);
CREATE INDEX idx_contract_status ON contracts(status);
