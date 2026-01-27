-- Matrimony Portal Database Schema
-- MySQL 8.0+ compatible
-- This file contains ONLY table definitions, indexes, and constraints
-- NO sample data - see 02-seed.sql for test data

-- Create database (run this separately if needed)
-- CREATE DATABASE matrimony_portal;
USE matrimony_portal;

-- ===========================================
-- CORE TABLES
-- ===========================================

-- 1. users table (matches User entity: status = AccountStatus)
-- email: UNIQUE already indexes it; avoid duplicate idx_users_email
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'EVENT_ORGANIZER', 'USER')),
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_role (role),
    INDEX idx_users_status (status)
);

-- 2. subscription_plans table
CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(50) UNIQUE NOT NULL,
    plan_type VARCHAR(10) UNIQUE NOT NULL CHECK (plan_type IN ('FREE', 'PREMIUM', 'VIP')),
    price_monthly DECIMAL(10,2) DEFAULT 0,
    price_yearly DECIMAL(10,2) DEFAULT 0,
    max_profiles_view INTEGER DEFAULT 10,
    max_messages INTEGER DEFAULT 5,
    max_photos INTEGER DEFAULT 3,
    priority_matching BOOLEAN DEFAULT FALSE,
    advanced_filters BOOLEAN DEFAULT FALSE,
    customer_support VARCHAR(10) DEFAULT 'EMAIL' CHECK (customer_support IN ('NONE', 'EMAIL', 'PHONE', 'PRIORITY')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. subscriptions table (user_id UNIQUE already indexed; no extra index)
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    plan_id BIGINT NOT NULL,
    billing_cycle VARCHAR(10) DEFAULT 'MONTHLY' CHECK (billing_cycle IN ('MONTHLY', 'YEARLY')),
    start_date DATE NOT NULL,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    auto_renew BOOLEAN DEFAULT TRUE,
    payment_amount DECIMAL(10,2),
    payment_date TIMESTAMP,
    next_billing_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id)
);

-- 4. profiles table (user_id UNIQUE already indexed; no extra user_id index)
CREATE TABLE IF NOT EXISTS profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    religion VARCHAR(50),
    caste VARCHAR(50),
    occupation VARCHAR(100),
    education VARCHAR(100),
    income DECIMAL(10,2),
    marital_status VARCHAR(20) DEFAULT 'SINGLE' CHECK (marital_status IN ('SINGLE', 'DIVORCED', 'WIDOWED')),
    height_cm INTEGER,
    weight_kg INTEGER,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100) DEFAULT 'India',
    about_me TEXT,
    preferences TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_profiles_gender (gender),
    INDEX idx_profiles_religion (religion),
    INDEX idx_profiles_city (city)
);

-- ===========================================
-- FEATURE TABLES
-- ===========================================

-- 5. events table (event_type, image_url match Event entity)
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organizer_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    venue VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    event_type VARCHAR(50) DEFAULT 'SPEED_DATING',
    image_url VARCHAR(500) DEFAULT NULL,
    max_participants INTEGER,
    registration_fee DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'UPCOMING' CHECK (status IN ('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_events_organizer_id (organizer_id),
    INDEX idx_events_event_date (event_date),
    INDEX idx_events_city (city)
);

-- 6. profile_photos table
CREATE TABLE IF NOT EXISTS profile_photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    alt_text VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_profile_photos_user_id (user_id),
    INDEX idx_profile_photos_is_primary (is_primary)
);

-- 7. media_gallery table
CREATE TABLE IF NOT EXISTS media_gallery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    media_type VARCHAR(20) NOT NULL CHECK (media_type IN ('VIDEO', 'AUDIO', 'DOCUMENT')),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    title VARCHAR(255),
    description TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_media_gallery_user_id (user_id)
);

-- 8. messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_no_self_message CHECK (sender_id != receiver_id),
    INDEX idx_messages_sender_id (sender_id),
    INDEX idx_messages_receiver_id (receiver_id),
    INDEX idx_messages_is_read (is_read)
);

-- 9. user_interests table
CREATE TABLE IF NOT EXISTS user_interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    interest_type VARCHAR(20) NOT NULL CHECK (interest_type IN ('LIKE', 'SHORTLIST', 'BLOCK')),
    compatibility_score DECIMAL(5,2) CHECK (compatibility_score >= 0 AND compatibility_score <= 100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_interest CHECK (from_user_id != to_user_id),
    INDEX idx_user_interests_from_user_id (from_user_id),
    INDEX idx_user_interests_to_user_id (to_user_id),
    INDEX idx_user_interests_interest_type (interest_type)
);

-- 10. event_registrations table
CREATE TABLE IF NOT EXISTS event_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'REFUNDED')),
    attended BOOLEAN DEFAULT FALSE,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_event_registrations_user_id (user_id),
    INDEX idx_event_registrations_event_id (event_id)
);

-- ===========================================
-- ADMINISTRATIVE TABLES
-- ===========================================

-- 11. payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    payment_type VARCHAR(20) NOT NULL CHECK (payment_type IN ('SUBSCRIPTION', 'EVENT_REGISTRATION', 'DONATION')),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255) UNIQUE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_payments_user_id (user_id),
    INDEX idx_payments_status (status)
);

-- 12. notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_is_read (is_read)
);

-- 13. notification_preferences table
CREATE TABLE IF NOT EXISTS notification_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    email_notifications BOOLEAN DEFAULT TRUE,
    push_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    new_matches BOOLEAN DEFAULT TRUE,
    new_messages BOOLEAN DEFAULT TRUE,
    event_updates BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 14. audit_logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_logs_user_id (user_id),
    INDEX idx_audit_logs_entity_type (entity_type)
);

-- 15. user_reports table
CREATE TABLE IF NOT EXISTS user_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id BIGINT NOT NULL,
    reported_user_id BIGINT NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('PENDING', 'INVESTIGATING', 'RESOLVED', 'DISMISSED') DEFAULT 'PENDING',
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_no_self_report CHECK (reporter_id != reported_user_id),
    INDEX idx_user_reports_reporter_id (reporter_id),
    INDEX idx_user_reports_reported_user_id (reported_user_id),
    INDEX idx_user_reports_status (status)
);

-- 16. success_stories table
CREATE TABLE IF NOT EXISTS success_stories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    couple_user1_id BIGINT NOT NULL,
    couple_user2_id BIGINT NOT NULL,
    story_title VARCHAR(255) NOT NULL,
    story_content TEXT NOT NULL,
    wedding_date DATE,
    photos JSON,
    is_featured BOOLEAN DEFAULT FALSE,
    submitted_by BIGINT,
    approved_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    FOREIGN KEY (couple_user1_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (couple_user2_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (submitted_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_success_stories_is_featured (is_featured)
);

-- 17. verification_tokens table (matches VerificationToken entity)
-- token UNIQUE already indexed; avoid duplicate idx
CREATE TABLE IF NOT EXISTS verification_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    user_id BIGINT NULL,
    token_type VARCHAR(20) NOT NULL CHECK (token_type IN ('EMAIL_VERIFICATION', 'PASSWORD_RESET')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_verification_tokens_user_id (user_id),
    INDEX idx_verification_tokens_token_type (token_type),
    INDEX idx_verification_tokens_expires_at (expires_at)
);

-- Indexes are defined inline in each CREATE TABLE above.
-- Re-running this script is safe: CREATE TABLE IF NOT EXISTS skips existing tables
-- (and their index definitions), so no "Duplicate key name" errors.
