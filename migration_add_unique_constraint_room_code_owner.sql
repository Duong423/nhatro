-- Migration: Thêm ràng buộc UNIQUE cho (owner_id, room_code)
-- Đảm bảo mỗi owner chỉ có duy nhất 1 roomCode

-- Xóa các bản ghi trùng lặp nếu có (giữ lại bản ghi có ID nhỏ nhất)
DELETE h1 FROM hostels h1
INNER JOIN hostels h2 
WHERE h1.owner_id = h2.owner_id 
  AND h1.room_code = h2.room_code 
  AND h1.hostel_id > h2.hostel_id;

-- Thêm ràng buộc UNIQUE cho (owner_id, room_code)
ALTER TABLE hostels 
ADD CONSTRAINT uk_owner_room_code UNIQUE (owner_id, room_code);
