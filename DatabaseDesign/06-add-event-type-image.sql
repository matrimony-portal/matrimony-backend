-- Add event_type and image_url to events table
-- Run after 01-schema. Run once; re-running will error if columns exist.

USE matrimony_portal;

ALTER TABLE events ADD COLUMN event_type VARCHAR(50) DEFAULT 'SPEED_DATING';
ALTER TABLE events ADD COLUMN image_url VARCHAR(500) DEFAULT NULL;
