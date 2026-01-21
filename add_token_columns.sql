-- Thêm cột accessToken và refreshToken vào bảng users
-- Chạy script này trong MySQL Workbench hoặc command line

USE quanlynhatro;

-- Kiểm tra xem cột đã tồn tại chưa
-- Nếu chưa có, thêm vào
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS access_token VARCHAR(1000) NULL,
ADD COLUMN IF NOT EXISTS refresh_token VARCHAR(1000) NULL;

-- Xác nhận cột đã được thêm
DESCRIBE users;
