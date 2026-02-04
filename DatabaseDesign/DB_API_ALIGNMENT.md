# Database vs API Alignment

This document summarizes how the current schema and migrations align with the backend entities and APIs.

---

## Overall: Structure is correct

The **01-schema.sql** and migrations are correctly structured for the APIs. A few migrations must be **applied in order** for everything to work.

---

## Tables used by event/organizer APIs

| Table | Schema (01-schema) | Entity / Usage | Status |
|-------|--------------------|----------------|--------|
| **users** | id, email, password, first_name, last_name, phone, role, status, created_at, updated_at | `User` entity | OK. `status` matches `AccountStatus` (ACTIVE, INACTIVE, BLOCKED). |
| **events** | id, organizer_id, title, description, event_date, venue, city, state, **event_type, image_url**, max_participants, registration_fee, status, created_at, updated_at | `Event` entity | OK. **01-schema** includes event_type, image_url. For older DBs, run **06-add-event-type-image.sql**. |
| **event_registrations** | id, user_id, event_id, registration_date, payment_status, attended, notes | `EventRegistration` entity | OK. payment_status (PENDING, PAID, REFUNDED) matches. |
| **profiles** | id, user_id, date_of_birth, gender, religion, caste, occupation, education, income, marital_status, height_cm, weight_kg, city, state, country, about_me, preferences, **citizenship, college, company**, is_verified, created_at, updated_at | `Profile` entity; native queries: getOrganizerProfile, getParticipantProfile, updateOrganizerProfile | OK. **01-schema** includes citizenship, college, company. For existing DBs created before this, run **08-profiles-citizenship-college-company.sql**. |
| **notifications** | id, user_id, notification_type, title, message, is_read, action_url, created_at, read_at | Native INSERT: EVENT_REQUEST_ACCEPTED, EVENT_CANCELLED | OK. Inserts use (user_id, notification_type, title, message, is_read); others are optional or defaulted. |

---

## Required migrations (run in this order)

1. **01-schema.sql** – base tables  
2. **02-seed.sql** – initial data  
3. **03-event-organizer-seed.sql** – extra events, users, registrations  
4. **04-migrate-users-to-status.sql** – **only if** the DB still has `is_active` (e.g. from an old `schema.sql`). **01-schema already has `status`**; skip 04 for a fresh 01-schema install.  
5. **05-fix-password-hash-password123.sql** – if you use `password123` in seeds  
6. **06-add-event-type-image.sql** – **required** for DBs created from an older 01-schema. **01-schema** now includes `event_type` and `image_url` in `events`.  
7. **07-two-more-organizers-seed.sql** – optional extra organizers  
8. **08-profiles-citizenship-college-company.sql** – only for **existing** DBs whose `profiles` table was created before citizenship/college/company were in 01-schema. **01-schema** now includes these columns; skip 08 for fresh installs.

---

## Quick check: are `event_type` and `image_url` on `events`?

If **06-add-event-type-image.sql** has not been run, JPA will fail when reading/writing `event_type` or `image_url` on `Event`.

Run:

```sql
USE matrimony_portal;
SHOW COLUMNS FROM events LIKE 'event_type';
SHOW COLUMNS FROM events LIKE 'image_url';
```

- If both return a row: you’re fine.  
- If either returns empty: run **06-add-event-type-image.sql** (or the idempotent version below).

---

## Idempotent “ensure event_type and image_url” script

If 06 was already applied, re-running it can error. This version only adds columns when they are missing:

```sql
-- 06-add-event-type-image-optional.sql (idempotent)
USE matrimony_portal;

-- Add only if missing (MySQL 8.0+)
SET @dbname = DATABASE();
SET @tablename = 'events';

SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'event_type');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE events ADD COLUMN event_type VARCHAR(50) DEFAULT ''SPEED_DATING''', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'image_url');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE events ADD COLUMN image_url VARCHAR(500) DEFAULT NULL', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
```

---

## 01-schema updated

**01-schema.sql** has been updated to include `event_type` and `image_url` in the `events` table. **New** databases created from it do not need 06.

For **existing** databases created from the previous 01-schema (without these columns), run either:

- **06-add-event-type-image.sql** (once), or  
- **06-add-event-type-image-idempotent.sql** (safe to run multiple times).

---

## Other entity–schema alignment

| Table | Entity | Notes |
|-------|--------|-------|
| **user_interests** | `Interest` | OK. `InterestType` enum: LIKE, SHORTLIST, BLOCK, PASS (matches 01-schema CHECK). Entity has `compatibility_score`, `notes` matching DB. |

---

## Summary

- You do **not** need to restructure the database for the current APIs.  
- Run **06-add-event-type-image.sql** only if `events` does not have `event_type` and `image_url` (e.g. created from an older 01-schema).  
- Run **08-profiles-citizenship-college-company.sql** only if `profiles` does not have `citizenship`, `college`, `company`.  
- Use **04-migrate-users-to-status.sql** only when migrating from an old schema that had `is_active`; it is not needed when starting from **01-schema.sql**.
