-- Migration: Add vehicles table for managing motorbikes/vehicles

CREATE TABLE IF NOT EXISTS vehicles (
  vehicle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contract_id BIGINT NOT NULL,
  license_plates TEXT,
  created_at DATETIME,
  updated_at DATETIME,
  CONSTRAINT fk_vehicle_contract FOREIGN KEY (contract_id) REFERENCES contracts(contract_id) ON DELETE CASCADE
);
