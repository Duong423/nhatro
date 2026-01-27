-- Add status column to hostels table
ALTER TABLE hostels 
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE';

-- Add index for better query performance
CREATE INDEX idx_hostels_status ON hostels(status);
