-- Add event_type and image_url to events if missing (idempotent)
-- Safe to run multiple times. Use this if 06-add-event-type-image.sql fails with "Duplicate column".

USE matrimony_portal;

-- MySQL: add columns only if they don't exist (avoids error on re-run)
SET @db = DATABASE();

SET @add_event_type = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'events' AND COLUMN_NAME = 'event_type');
SET @sql = IF(@add_event_type = 0,
  'ALTER TABLE events ADD COLUMN event_type VARCHAR(50) DEFAULT ''SPEED_DATING''',
  'SELECT 1 AS noop');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @add_image_url = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'events' AND COLUMN_NAME = 'image_url');
SET @sql = IF(@add_image_url = 0,
  'ALTER TABLE events ADD COLUMN image_url VARCHAR(500) DEFAULT NULL',
  'SELECT 1 AS noop');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
