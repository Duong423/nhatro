-- Migration script: Add booking system tables and update hostel table

-- 1. Add deposit_amount to hostels table
ALTER TABLE hostels 
ADD COLUMN deposit_amount DECIMAL(10, 2);

-- 2. Create bookings table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    hostel_id BIGINT NOT NULL,
    booking_date DATETIME NOT NULL,
    check_in_date DATETIME NOT NULL,
    deposit_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    customer_name VARCHAR(50),
    customer_phone VARCHAR(20),
    customer_email VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (hostel_id) REFERENCES hostels(hostel_id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_hostel_id (hostel_id),
    INDEX idx_status (status)
);

-- 3. Update payments table to add booking relationship
ALTER TABLE payments
ADD COLUMN booking_id BIGINT,
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
ADD COLUMN transaction_id VARCHAR(100),
MODIFY COLUMN payment_method VARCHAR(50),
MODIFY COLUMN invoice_id BIGINT NULL,
ADD FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
ADD INDEX idx_booking_id (booking_id),
ADD INDEX idx_transaction_id (transaction_id);

-- 4. Update existing data (if needed)
UPDATE hostels SET deposit_amount = price * 0.3 WHERE deposit_amount IS NULL;
