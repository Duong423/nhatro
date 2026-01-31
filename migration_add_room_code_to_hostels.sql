-- Migration: add room_code to hostels

USE quanlynhatro;

-- Add room_code column to hostels table if not exists
ALTER TABLE hostels
    ADD COLUMN room_code VARCHAR(255) DEFAULT NULL;