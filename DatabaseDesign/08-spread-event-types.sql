-- ===========================================
-- SPREAD EVENT TYPES (for existing databases)
-- ===========================================
-- Run this if your events all have event_type = 'SPEED_DATING' (default)
-- and you want to spread them across: SPEED_DATING, COFFEE_MEETUP, DINNER, CULTURAL.
--
-- Uses id % 4 to assign types evenly. Safe to run; only updates rows that
-- would change. Run AFTER 06-add-event-type-image (or when event_type exists).
-- ===========================================

USE matrimony_portal;

-- Ensure event_type column exists (no-op if 06 already applied)
SET @db = DATABASE();
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'events' AND COLUMN_NAME = 'event_type');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE events ADD COLUMN event_type VARCHAR(50) DEFAULT ''SPEED_DATING''',
  'SELECT 1 AS noop');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Spread: id % 4 => 0 CULTURAL, 1 SPEED_DATING, 2 COFFEE_MEETUP, 3 DINNER
UPDATE events SET event_type = 'CULTURAL'   WHERE id % 4 = 0;
UPDATE events SET event_type = 'SPEED_DATING'  WHERE id % 4 = 1;
UPDATE events SET event_type = 'COFFEE_MEETUP' WHERE id % 4 = 2;
UPDATE events SET event_type = 'DINNER'     WHERE id % 4 = 3;

-- Verify: SELECT id, event_type FROM events ORDER BY id;
