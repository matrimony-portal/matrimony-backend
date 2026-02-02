-- ===========================================
-- MIGRATE users: is_active → status
-- ===========================================
-- Run this ONLY if your existing DB has users.is_active (old schema).
-- Aligns users table with User entity (AccountStatus).
-- ===========================================

USE matrimony_portal;

-- Check if status column exists, add only if missing
SET @col_exists = (SELECT COUNT(*) 
                   FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = 'matrimony_portal' 
                   AND TABLE_NAME = 'users' 
                   AND COLUMN_NAME = 'status');

SET @sql_add_status = IF(@col_exists = 0, 
    'ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT ''INACTIVE''', 
    'SELECT ''status column already exists'' AS message');

PREPARE stmt FROM @sql_add_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Migrate data only if is_active column exists
SET @is_active_exists = (SELECT COUNT(*) 
                         FROM INFORMATION_SCHEMA.COLUMNS 
                         WHERE TABLE_SCHEMA = 'matrimony_portal' 
                         AND TABLE_NAME = 'users' 
                         AND COLUMN_NAME = 'is_active');

SET @sql_migrate = IF(@is_active_exists > 0, 
    'UPDATE users SET status = IF(is_active = 1, ''ACTIVE'', ''INACTIVE'')', 
    'SELECT ''No is_active column to migrate from'' AS message');

PREPARE stmt FROM @sql_migrate;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop is_active if it exists
SET @sql_drop = IF(@is_active_exists > 0, 
    'ALTER TABLE users DROP COLUMN is_active', 
    'SELECT ''is_active column does not exist'' AS message');

PREPARE stmt FROM @sql_drop;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add constraint only if it doesn't exist
SET @constraint_exists = (SELECT COUNT(*) 
                          FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
                          WHERE TABLE_SCHEMA = 'matrimony_portal' 
                          AND TABLE_NAME = 'users' 
                          AND CONSTRAINT_NAME = 'chk_users_status');

SET @sql_constraint = IF(@constraint_exists = 0, 
    'ALTER TABLE users ADD CONSTRAINT chk_users_status CHECK (status IN (''ACTIVE'', ''INACTIVE'', ''BLOCKED''))', 
    'SELECT ''Constraint chk_users_status already exists'' AS message');

PREPARE stmt FROM @sql_constraint;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
