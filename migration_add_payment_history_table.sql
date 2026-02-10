-- Migration: Tạo bảng payment_history để lưu lịch sử thanh toán
-- Bảng này lưu dữ liệu denormalized (snapshot) tại thời điểm thanh toán thành công
-- giúp truy vấn nhanh mà không cần join nhiều bảng

CREATE TABLE IF NOT EXISTS payment_history (
    payment_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Liên kết đến hóa đơn gốc
    bill_id BIGINT NOT NULL,
    bill_id_ref BIGINT NOT NULL,
    contract_id BIGINT NOT NULL,
    room_code VARCHAR(50),
    
    -- Thông tin kỳ thanh toán
    billing_month INT NOT NULL,
    billing_year INT NOT NULL,
    
    -- Chi tiết số tiền (snapshot tại thời điểm thanh toán)
    room_price DECIMAL(15,2) NOT NULL,
    electricity_cost DECIMAL(15,2),
    water_cost DECIMAL(15,2),
    service_cost DECIMAL(15,2),
    total_amount DECIMAL(15,2) NOT NULL,
    
    -- Thông tin thanh toán
    payment_method VARCHAR(50),
    transaction_code VARCHAR(100),
    payment_date DATETIME NOT NULL,
    due_date DATE,
    
    -- Thông tin người thuê (snapshot)
    tenant_id BIGINT,
    tenant_name VARCHAR(100),
    tenant_phone VARCHAR(20),
    
    -- Thông tin chủ nhà (snapshot)
    owner_id BIGINT NOT NULL,
    owner_name VARCHAR(100),
    owner_phone VARCHAR(20),
    
    -- Ghi chú
    note VARCHAR(500),
    
    -- Audit fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key
    CONSTRAINT fk_payment_history_bill FOREIGN KEY (bill_id) REFERENCES bills(bill_id),
    
    -- Index để tối ưu truy vấn
    INDEX idx_payment_history_owner (owner_id),
    INDEX idx_payment_history_tenant (tenant_id),
    INDEX idx_payment_history_room_code (room_code),
    INDEX idx_payment_history_billing_period (billing_month, billing_year),
    INDEX idx_payment_history_payment_date (payment_date),
    INDEX idx_payment_history_bill_ref (bill_id_ref)
);
