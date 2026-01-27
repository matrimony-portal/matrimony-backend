-- ===========================================
-- FIX PASSWORD HASH (password123)
-- ===========================================
-- Use this if you updated passwords manually and login fails.
-- Sets BCrypt hash for "password123" (matches Spring BCryptPasswordEncoder).
-- Run in phpMyAdmin or MySQL CLI.
-- ===========================================

USE matrimony_portal;

-- BCrypt hash for "password123" (strength 10)
SET @hash = '$2a$10$GXrtJ5LB1pJ5CVtH7oPVguk2QqtcQ6AqVdEH2IhzG8UOJW02HVHVG';

-- Fix all seed users (02-seed, 03-event-organizer-seed)
UPDATE users SET password = @hash WHERE email IN (
  'rahul.sharma@example.com',
  'priya.verma@example.com',
  'john.doe@example.com',
  'jane.smith@example.com',
  'alice.johnson@example.com',
  'priya.agarwal@example.com',
  'amit.kumar@example.com',
  'sneha.patel@example.com',
  'vijay.reddy@example.com',
  'kavita.singh@example.com',
  'rohan.verma@example.com',
  'divya.nair@example.com',
  'sanjay.mehta@example.com',
  'organizer2@matrimony.com',
  'organizer3@matrimony.com'
);

-- Or fix only organizer:
-- UPDATE users SET password = @hash WHERE email = 'priya.verma@example.com';

-- Verify (optional):
-- SELECT id, email, LEFT(password, 29) AS hash_prefix FROM users WHERE email = 'organizer@matrimony.com';
