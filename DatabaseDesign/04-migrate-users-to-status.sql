-- ===========================================
-- MIGRATE users: is_active → status
-- ===========================================
-- Run this ONLY if your existing DB has users.is_active (old schema).
-- Aligns users table with User entity (AccountStatus).
-- ===========================================

USE matrimony_portal;

-- Add status, backfill from is_active, drop is_active
ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE';
UPDATE users SET status = IF(is_active = 1, 'ACTIVE', 'INACTIVE');
ALTER TABLE users DROP COLUMN is_active;
ALTER TABLE users ADD CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED'));

-- Optional: add index for status (skip if idx_users_status already exists to avoid duplicate key)
-- CREATE INDEX idx_users_status ON users(status);
