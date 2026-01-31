-- Migration: Map existing contracts to tenants/owners and update contracts to reference them
-- WARNING: Make a DB backup before running this script (mysqldump)...

USE nhatro;

-- Disable FK checks during migration
SET FOREIGN_KEY_CHECKS = 0;

-- 1) Add temporary columns to hold the new references if they do not exist
ALTER TABLE contracts
  ADD COLUMN IF NOT EXISTS tenant_ref_id BIGINT,
  ADD COLUMN IF NOT EXISTS owner_ref_id BIGINT;

-- 2) Try to map existing contracts -> tenants via tenants.user_id = contracts.tenant_id
UPDATE contracts c
JOIN tenants t ON t.user_id = c.tenant_id
SET c.tenant_ref_id = t.tenant_id
WHERE c.tenant_id IS NOT NULL AND c.tenant_ref_id IS NULL;

-- 3) Map by name or phone if user_id mapping not found
UPDATE contracts c
JOIN tenants t ON (t.name = c.tenant_name OR t.phone = c.tenant_phone)
SET c.tenant_ref_id = t.tenant_id
WHERE c.tenant_ref_id IS NULL AND (c.tenant_name IS NOT NULL OR c.tenant_phone IS NOT NULL);

-- 4) Insert missing tenants from contract embedded info when possible (fallback)
INSERT INTO tenants (user_id, name, phone, email)
SELECT DISTINCT c.tenant_id, c.tenant_name, c.tenant_phone, c.tenant_email
FROM contracts c
WHERE c.tenant_ref_id IS NULL AND (c.tenant_name IS NOT NULL OR c.tenant_phone IS NOT NULL);

-- 5) Update again to pick up newly inserted tenants
UPDATE contracts c
JOIN tenants t ON t.user_id = c.tenant_id OR (t.name = c.tenant_name OR t.phone = c.tenant_phone)
SET c.tenant_ref_id = t.tenant_id
WHERE c.tenant_ref_id IS NULL;

-- Owner side: map by user_id
UPDATE contracts c
JOIN owners o ON o.user_id = c.owner_id
SET c.owner_ref_id = o.owner_id
WHERE c.owner_id IS NOT NULL AND c.owner_ref_id IS NULL;

-- Map owner by name/phone
UPDATE contracts c
JOIN owners o ON (o.name = c.owner_name OR o.phone = c.owner_phone)
SET c.owner_ref_id = o.owner_id
WHERE c.owner_ref_id IS NULL AND (c.owner_name IS NOT NULL OR c.owner_phone IS NOT NULL);

-- Insert missing owners from contract if possible
INSERT INTO owners (user_id, name, phone, email)
SELECT DISTINCT c.owner_id, c.owner_name, c.owner_phone, NULL
FROM contracts c
WHERE c.owner_ref_id IS NULL AND (c.owner_name IS NOT NULL OR c.owner_phone IS NOT NULL);

-- Update again to pick up newly inserted owners
UPDATE contracts c
JOIN owners o ON o.user_id = c.owner_id OR (o.name = c.owner_name OR o.phone = c.owner_phone)
SET c.owner_ref_id = o.owner_id
WHERE c.owner_ref_id IS NULL;

-- 6) Drop existing foreign keys that point to users (tenant_id, owner_id -> users.id)
--    We try to discover constraint names dynamically and drop them if present.
SELECT constraint_name INTO @fk_tenant
FROM information_schema.key_column_usage
WHERE table_schema = DATABASE() AND table_name = 'contracts' AND column_name = 'tenant_id' AND referenced_table_name = 'users' LIMIT 1;

SELECT constraint_name INTO @fk_owner
FROM information_schema.key_column_usage
WHERE table_schema = DATABASE() AND table_name = 'contracts' AND column_name = 'owner_id' AND referenced_table_name = 'users' LIMIT 1;

SET @sql = IF(@fk_tenant IS NOT NULL, CONCAT('ALTER TABLE contracts DROP FOREIGN KEY ', @fk_tenant), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(@fk_owner IS NOT NULL, CONCAT('ALTER TABLE contracts DROP FOREIGN KEY ', @fk_owner), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 7) Remove embedded columns (tenant_name, tenant_phone, tenant_email, owner_name, owner_phone) if present
ALTER TABLE contracts
  DROP COLUMN IF EXISTS tenant_name,
  DROP COLUMN IF EXISTS tenant_phone,
  DROP COLUMN IF EXISTS tenant_email,
  DROP COLUMN IF EXISTS owner_name,
  DROP COLUMN IF EXISTS owner_phone;

-- 8) Remove old tenant_id/owner_id (that referenced users) and rename tmp columns to proper names
ALTER TABLE contracts
  DROP COLUMN IF EXISTS tenant_id,
  DROP COLUMN IF EXISTS owner_id;

ALTER TABLE contracts
  CHANGE COLUMN IF EXISTS tenant_ref_id tenant_id BIGINT,
  CHANGE COLUMN IF EXISTS owner_ref_id owner_id BIGINT;

-- 9) Add foreign keys to new tables (tenants / owners)
--    Drop existing constraints with same name if present (ignore errors by checking existence)
ALTER TABLE contracts
  ADD CONSTRAINT fk_contracts_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id),
  ADD CONSTRAINT fk_contracts_owner FOREIGN KEY (owner_id) REFERENCES owners(owner_id);

-- Re-enable FK checks
SET FOREIGN_KEY_CHECKS = 1;

-- Show final structure (for manual verification)
DESCRIBE contracts;
DESCRIBE tenants;
DESCRIBE owners;

-- End of migration
