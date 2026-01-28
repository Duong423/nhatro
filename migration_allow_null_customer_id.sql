-- Migration: Allow NULL for customer_id in bookings table
-- This allows guest bookings without user login

USE nhatro;

-- Modify customer_id column to allow NULL
ALTER TABLE bookings 
MODIFY COLUMN customer_id BIGINT NULL;

-- Verify the change
DESCRIBE bookings;
