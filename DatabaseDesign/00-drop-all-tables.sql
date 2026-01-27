-- ===========================================
-- DROP ALL TABLES (database stays)
-- ===========================================
-- Use this when DROP DATABASE is disabled (e.g. phpMyAdmin, shared hosting).
-- Run this, then 01-schema, 02-seed, 03-event-organizer-seed.
-- ===========================================

USE matrimony_portal;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS user_reports;
DROP TABLE IF EXISTS success_stories;
DROP TABLE IF EXISTS notification_preferences;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS event_registrations;
DROP TABLE IF EXISTS user_interests;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS media_gallery;
DROP TABLE IF EXISTS profile_photos;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS profiles;
DROP TABLE IF EXISTS subscriptions;
DROP TABLE IF EXISTS subscription_plans;
DROP TABLE IF EXISTS verification_tokens;
DROP TABLE IF EXISTS email_verification_tokens;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;
