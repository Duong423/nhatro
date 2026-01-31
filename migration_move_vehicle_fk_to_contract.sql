-- Migration: Move relationship so contracts have vehicle_id (contracts.vehicle_id -> vehicles.vehicle_id)

-- 1) Add vehicle_id column to contracts (nullable initially)
ALTER TABLE contracts
  ADD COLUMN IF NOT EXISTS vehicle_id BIGINT NULL;

-- 2) For existing vehicles, set contracts.vehicle_id to the most recent vehicle for that contract
UPDATE contracts c
LEFT JOIN (
  SELECT contract_id, MAX(vehicle_id) AS vehicle_id
  FROM vehicles
  GROUP BY contract_id
) vmap ON vmap.contract_id = c.contract_id
SET c.vehicle_id = vmap.vehicle_id;

-- 3) Add foreign key from contracts.vehicle_id -> vehicles.vehicle_id
ALTER TABLE contracts
  ADD CONSTRAINT IF NOT EXISTS fk_contract_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE SET NULL;

-- 4) Remove old foreign key and column from vehicles
ALTER TABLE vehicles
  DROP FOREIGN KEY fk_vehicle_contract;

ALTER TABLE vehicles
  DROP COLUMN contract_id;