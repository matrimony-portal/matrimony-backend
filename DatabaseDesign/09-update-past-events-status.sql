-- ===========================================
-- UPDATE PAST EVENTS STATUS
-- ===========================================
-- Run this on existing databases where event_date is in the past but
-- status is still UPCOMING or ONGOING. Sets those to COMPLETED so
-- DB aligns with display logic (getDisplayStatus / event cards).
--
-- New seeds (02, 03, 07) use 2026 event_date so they display as UPCOMING.
-- This migration is idempotent: safe to run multiple times.
-- Run AFTER 01-schema and your seed files (02, 03, 07, etc.).
-- ===========================================

USE matrimony_portal;

-- Set status to COMPLETED for any event whose start (event_date) has passed
-- and whose status is still UPCOMING or ONGOING. Leaves CANCELLED unchanged.
UPDATE events
SET status = 'COMPLETED'
WHERE status IN ('UPCOMING', 'ONGOING')
  AND event_date < CURRENT_TIMESTAMP;

-- Verify: SELECT id, organizer_id, title, event_date, status FROM events ORDER BY event_date;
