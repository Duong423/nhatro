-- Migration: Revert customer_id to NOT NULL in bookings table
-- This requires authentication for booking creation

USE nhatro;

-- Modify customer_id column back to NOT NULL
ALTER TABLE bookings 
MODIFY COLUMN customer_id BIGINT NOT NULL;

-- Verify the change
DESCRIBE bookings;
