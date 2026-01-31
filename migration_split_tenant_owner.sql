-- Migration: Tách thông tin tenant và owner ra bảng riêng
-- 1) Tạo bảng tenants và owners
-- 2) Chuyển dữ liệu từ contracts sang tenants/owners
-- 3) Đổi lại foreign key trên contracts trỏ đến tenants/owners

USE nhatro;

SET FOREIGN_KEY_CHECKS = 0;

-- Tạo bảng tenants
CREATE TABLE IF NOT EXISTS tenants (
    tenant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tenants_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng owners
CREATE TABLE IF NOT EXISTS owners (
    owner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    cccd VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_owners_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chèn dữ liệu tenants từ contracts (distinct theo user và thông tin khách)
INSERT INTO tenants (user_id, name, phone, email)
SELECT DISTINCT tenant_id AS user_id, tenant_name AS name, tenant_phone AS phone, tenant_email AS email
FROM contracts
WHERE tenant_id IS NOT NULL;

-- Chèn dữ liệu owners từ contracts (distinct theo user và thông tin chủ)
INSERT INTO owners (user_id, name, phone, email)
SELECT DISTINCT owner_id AS user_id, owner_name AS name, owner_phone AS phone, NULL AS email
FROM contracts
WHERE owner_id IS NOT NULL;

-- Thêm cột tạm tenant_ref_id, owner_ref_id để ánh xạ
ALTER TABLE contracts ADD COLUMN tenant_ref_id BIGINT;
ALTER TABLE contracts ADD COLUMN owner_ref_id BIGINT;

-- Cập nhật tenant_ref_id dựa vào bảng tenants (kết nối bởi user_id và tên/phone lý tưởng)
UPDATE contracts c
JOIN tenants t ON t.user_id = c.tenant_id AND (t.name = c.tenant_name OR t.phone = c.tenant_phone)
SET c.tenant_ref_id = t.tenant_id;

-- Nếu có row nào chưa map được (ví dụ tên khác), fallback: map theo user_id
UPDATE contracts c
JOIN tenants t ON t.user_id = c.tenant_id
SET c.tenant_ref_id = t.tenant_id
WHERE c.tenant_ref_id IS NULL;

-- Cập nhật owner_ref_id tương tự
UPDATE contracts c
JOIN owners o ON o.user_id = c.owner_id AND (o.name = c.owner_name OR o.phone = c.owner_phone)
SET c.owner_ref_id = o.owner_id;

UPDATE contracts c
JOIN owners o ON o.user_id = c.owner_id
SET c.owner_ref_id = o.owner_id
WHERE c.owner_ref_id IS NULL;

-- Bỏ ràng buộc FK cũ trỏ tới users (tên constraint có thể khác; nếu không đúng, hãy kiểm tra và sửa)
ALTER TABLE contracts DROP FOREIGN KEY fk_contracts_tenant;
ALTER TABLE contracts DROP FOREIGN KEY fk_contracts_owner;

-- Xóa các cột thông tin embedded trong contracts (tên, phone, email)
ALTER TABLE contracts DROP COLUMN tenant_name;
ALTER TABLE contracts DROP COLUMN tenant_phone;
ALTER TABLE contracts DROP COLUMN tenant_email;
ALTER TABLE contracts DROP COLUMN owner_name;
ALTER TABLE contracts DROP COLUMN owner_phone;

-- Bỏ cột tenant_id và owner_id cũ (referencing users) và đổi tên cột tạm
ALTER TABLE contracts DROP COLUMN tenant_id;
ALTER TABLE contracts DROP COLUMN owner_id;
ALTER TABLE contracts CHANGE COLUMN tenant_ref_id tenant_id BIGINT;
ALTER TABLE contracts CHANGE COLUMN owner_ref_id owner_id BIGINT;

-- Thêm FK mới trỏ tới tenants và owners
ALTER TABLE contracts ADD CONSTRAINT fk_contracts_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id);
ALTER TABLE contracts ADD CONSTRAINT fk_contracts_owner FOREIGN KEY (owner_id) REFERENCES owners(owner_id);

SET FOREIGN_KEY_CHECKS = 1;

-- Kiểm tra cấu trúc
DESCRIBE contracts;
DESCRIBE tenants;
DESCRIBE owners;
