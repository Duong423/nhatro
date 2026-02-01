-- Migration: Thêm giá trị RESERVED vào enum status của bảng hostels

-- Cách 1: Nếu dùng MySQL/MariaDB
ALTER TABLE hostels 
MODIFY COLUMN status ENUM('AVAILABLE', 'RESERVED', 'FULL', 'CLOSED', 'UNDER_RENOVATION') NOT NULL DEFAULT 'AVAILABLE';

-- Cách 2: Nếu dùng PostgreSQL (uncomment nếu dùng PostgreSQL)
-- ALTER TABLE hostels 
-- ALTER COLUMN status TYPE VARCHAR(50);

-- Sau đó có thể tạo lại enum type (nếu cần):
-- DROP TYPE IF EXISTS hostel_status_enum CASCADE;
-- CREATE TYPE hostel_status_enum AS ENUM ('AVAILABLE', 'RESERVED', 'FULL', 'CLOSED', 'UNDER_RENOVATION');
-- ALTER TABLE hostels 
-- ALTER COLUMN status TYPE hostel_status_enum USING status::hostel_status_enum;
