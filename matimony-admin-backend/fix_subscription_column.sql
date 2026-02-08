-- Fix subscription column type from TINYINT to VARCHAR
ALTER TABLE users MODIFY COLUMN subscription VARCHAR(50) NOT NULL;

-- If you have existing data with ordinal values (0, 1), update them to string values:
-- UPDATE users SET subscription = 'FREE' WHERE subscription = '0';
-- UPDATE users SET subscription = 'PAID' WHERE subscription = '1';