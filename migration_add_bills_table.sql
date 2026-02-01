-- Migration: Add bills table for monthly billing system
-- Date: 2026-02-01

CREATE TABLE bills (
    bill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    room_code VARCHAR(50),
    billing_month INT NOT NULL,
    billing_year INT NOT NULL,
    room_price DECIMAL(15, 2) NOT NULL,
    electricity_cost DECIMAL(15, 2) DEFAULT 0,
    water_cost DECIMAL(15, 2) DEFAULT 0,
    service_cost DECIMAL(15, 2) DEFAULT 0,
    total_amount DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    due_date DATE,
    payment_date DATETIME,
    note VARCHAR(500),
    payment_method VARCHAR(50),
    transaction_code VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contracts(contract_id) ON DELETE CASCADE,
    UNIQUE KEY unique_contract_month_year (contract_id, billing_month, billing_year)
);

-- Add indexes for better query performance
CREATE INDEX idx_bills_status ON bills(status);
CREATE INDEX idx_bills_due_date ON bills(due_date);
CREATE INDEX idx_bills_room_code ON bills(room_code);
CREATE INDEX idx_bills_billing_period ON bills(billing_year, billing_month);
