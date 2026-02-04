-- Add citizenship, college, company to profiles (idempotent)
USE matrimony_portal;

-- Check and add citizenship
SET @col_exists = (SELECT COUNT(*) 
                   FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = 'matrimony_portal' 
                   AND TABLE_NAME = 'profiles' 
                   AND COLUMN_NAME = 'citizenship');

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE profiles ADD COLUMN citizenship VARCHAR(100)', 
    'SELECT ''citizenship already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add college
SET @col_exists = (SELECT COUNT(*) 
                   FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = 'matrimony_portal' 
                   AND TABLE_NAME = 'profiles' 
                   AND COLUMN_NAME = 'college');

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE profiles ADD COLUMN college VARCHAR(200)', 
    'SELECT ''college already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add company
SET @col_exists = (SELECT COUNT(*) 
                   FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = 'matrimony_portal' 
                   AND TABLE_NAME = 'profiles' 
                   AND COLUMN_NAME = 'company');

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE profiles ADD COLUMN company VARCHAR(200)', 
    'SELECT ''company already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
