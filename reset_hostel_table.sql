-- XÓA TOÀN BỘ DỮ LIỆU VÀ CẤU TRÚC BẢNG hostel (RESET)
-- Chạy script này trong MySQL Workbench hoặc command line

USE quanlynhatro;

-- Xóa bảng hostel nếu tồn tại
DROP TABLE IF EXISTS hostel;

-- Tạo lại bảng hostel với cấu trúc mới (ví dụ, cập nhật các cột mới nhất bạn đang dùng)
CREATE TABLE hostel (
    hostel_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    room_code VARCHAR(255),
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    area DOUBLE,
    description TEXT,
    amenities VARCHAR(255),
    room_count INT,
    max_occupancy INT,
    room_type VARCHAR(50),
    images TEXT,
    owner_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    -- Thêm các cột khác nếu bạn vừa thêm vào entity
);

-- XÓA DỮ LIỆU RÁC LIÊN QUAN (nếu cần)
DELETE FROM service_hostel;
DELETE FROM hostel WHERE 1=1;
-- Nếu có bảng liên kết khác, thêm lệnh DELETE tương ứng

-- Reset AUTO_INCREMENT nếu muốn
ALTER TABLE hostel AUTO_INCREMENT = 1;
