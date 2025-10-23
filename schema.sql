-- Matrimony Portal Database Schema
-- PostgreSQL 13+ compatible

-- Create database (run this separately if needed)
-- CREATE DATABASE matrimony_portal;
-- \c matrimony_portal;

-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ===========================================
-- CORE TABLES
-- ===========================================

-- 1. users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'EVENT_ORGANIZER', 'USER')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. subscription_plans table
CREATE TABLE subscription_plans (
    id BIGSERIAL PRIMARY KEY,
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. subscriptions table
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    billing_cycle VARCHAR(10) DEFAULT 'MONTHLY' CHECK (billing_cycle IN ('MONTHLY', 'YEARLY')),
    start_date DATE NOT NULL,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    auto_renew BOOLEAN DEFAULT TRUE,
    payment_amount DECIMAL(10,2),
    payment_date TIMESTAMP,
    next_billing_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. profiles table
CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================
-- FEATURE TABLES
-- ===========================================

-- 5. events table
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    organizer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    venue VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    max_participants INTEGER,
    registration_fee DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'UPCOMING' CHECK (status IN ('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. profile_photos table
CREATE TABLE profile_photos (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    alt_text VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. media_gallery table
CREATE TABLE media_gallery (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    media_type VARCHAR(20) NOT NULL CHECK (media_type IN ('VIDEO', 'AUDIO', 'DOCUMENT')),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    title VARCHAR(255),
    description TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. messages table
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    receiver_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    CONSTRAINT chk_no_self_message CHECK (sender_id != receiver_id)
);

-- 9. user_interests table
CREATE TABLE user_interests (
    id BIGSERIAL PRIMARY KEY,
    from_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    to_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    interest_type VARCHAR(20) NOT NULL CHECK (interest_type IN ('LIKE', 'SHORTLIST', 'BLOCK')),
    compatibility_score DECIMAL(5,2) CHECK (compatibility_score >= 0 AND compatibility_score <= 100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_no_self_interest CHECK (from_user_id != to_user_id)
);

-- 10. event_registrations table
CREATE TABLE event_registrations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'REFUNDED')),
    attended BOOLEAN DEFAULT FALSE,
    notes TEXT
);

-- ===========================================
-- ADMINISTRATIVE TABLES
-- ===========================================

-- 11. payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    payment_type VARCHAR(20) NOT NULL CHECK (payment_type IN ('SUBSCRIPTION', 'EVENT_REGISTRATION', 'DONATION')),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255) UNIQUE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

-- 12. notifications table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

-- 13. notification_preferences table
CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email_notifications BOOLEAN DEFAULT TRUE,
    push_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    new_matches BOOLEAN DEFAULT TRUE,
    new_messages BOOLEAN DEFAULT TRUE,
    event_updates BOOLEAN DEFAULT TRUE,
    profile_views BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. audit_logs table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 15. user_reports table
CREATE TABLE user_reports (
    id BIGSERIAL PRIMARY KEY,
    reporter_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    reported_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    report_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'INVESTIGATING', 'RESOLVED', 'DISMISSED')),
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    CONSTRAINT chk_no_self_report CHECK (reporter_id != reported_user_id)
);

-- 16. success_stories table
CREATE TABLE success_stories (
    id BIGSERIAL PRIMARY KEY,
    couple_user1_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    couple_user2_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    story_title VARCHAR(255) NOT NULL,
    story_content TEXT NOT NULL,
    wedding_date DATE,
    photos JSONB,
    is_featured BOOLEAN DEFAULT FALSE,
    submitted_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    approved_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP
);

-- ===========================================
-- INDEXES FOR PERFORMANCE
-- ===========================================

-- Users table indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Subscriptions table indexes
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_plan_type ON subscriptions(plan_type);

-- Profiles table indexes
CREATE INDEX idx_profiles_user_id ON profiles(user_id);
CREATE INDEX idx_profiles_gender ON profiles(gender);
CREATE INDEX idx_profiles_religion ON profiles(religion);
CREATE INDEX idx_profiles_city ON profiles(city);

-- Events table indexes
CREATE INDEX idx_events_organizer_id ON events(organizer_id);
CREATE INDEX idx_events_event_date ON events(event_date);
CREATE INDEX idx_events_city ON events(city);

-- Profile photos table indexes
CREATE INDEX idx_profile_photos_user_id ON profile_photos(user_id);
CREATE INDEX idx_profile_photos_is_primary ON profile_photos(is_primary);

-- Media gallery table indexes
CREATE INDEX idx_media_gallery_user_id ON media_gallery(user_id);

-- Messages table indexes
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_receiver_id ON messages(receiver_id);
CREATE INDEX idx_messages_is_read ON messages(is_read);

-- User interests table indexes
CREATE INDEX idx_user_interests_from_user_id ON user_interests(from_user_id);
CREATE INDEX idx_user_interests_to_user_id ON user_interests(to_user_id);
CREATE INDEX idx_user_interests_interest_type ON user_interests(interest_type);

-- Event registrations table indexes
CREATE INDEX idx_event_registrations_user_id ON event_registrations(user_id);
CREATE INDEX idx_event_registrations_event_id ON event_registrations(event_id);

-- Payments table indexes
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);

-- Notifications table indexes
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

-- Audit logs table indexes
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);

-- User reports table indexes
CREATE INDEX idx_user_reports_reporter_id ON user_reports(reporter_id);
CREATE INDEX idx_user_reports_reported_user_id ON user_reports(reported_user_id);
CREATE INDEX idx_user_reports_status ON user_reports(status);

-- Success stories table indexes
CREATE INDEX idx_success_stories_is_featured ON success_stories(is_featured);

-- ===========================================
-- TRIGGERS FOR UPDATED_AT
-- ===========================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_subscription_plans_updated_at BEFORE UPDATE ON subscription_plans FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_subscriptions_updated_at BEFORE UPDATE ON subscriptions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_profiles_updated_at BEFORE UPDATE ON profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_events_updated_at BEFORE UPDATE ON events FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_notification_preferences_updated_at BEFORE UPDATE ON notification_preferences FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ===========================================
-- SAMPLE DATA INSERTION
-- ===========================================

-- Insert sample users
INSERT INTO users (email, password, first_name, last_name, phone, role) VALUES
('admin@matrimony.com', '$2a$10$example.hash', 'System', 'Admin', '+1234567890', 'ADMIN'),
('organizer@matrimony.com', '$2a$10$example.hash', 'Event', 'Organizer', '+1234567891', 'EVENT_ORGANIZER'),
('john.doe@example.com', '$2a$10$example.hash', 'John', 'Doe', '+1234567892', 'USER'),
('jane.smith@example.com', '$2a$10$example.hash', 'Jane', 'Smith', '+1234567893', 'USER'),
('alice.johnson@example.com', '$2a$10$example.hash', 'Alice', 'Johnson', '+1234567894', 'USER');

-- Insert sample subscription plans
INSERT INTO subscription_plans (plan_name, plan_type, price_monthly, price_yearly, max_profiles_view, max_messages, max_photos, priority_matching, advanced_filters, customer_support) VALUES
('Free Plan', 'FREE', 0.00, 0.00, 10, 5, 3, false, false, 'NONE'),
('Premium Plan', 'PREMIUM', 999.00, 9999.00, 100, 50, 10, true, true, 'PHONE'),
('VIP Plan', 'VIP', 1999.00, 19999.00, -1, -1, -1, true, true, 'PRIORITY');

-- Insert sample subscriptions
INSERT INTO subscriptions (user_id, plan_id, billing_cycle, start_date, is_active) VALUES
(3, 1, 'MONTHLY', CURRENT_DATE, true),
(4, 2, 'MONTHLY', CURRENT_DATE, true),
(5, 3, 'YEARLY', CURRENT_DATE, true);

-- Insert sample profiles
INSERT INTO profiles (user_id, date_of_birth, gender, religion, caste, occupation, education, city, state, about_me, preferences) VALUES
(3, '1990-05-15', 'MALE', 'Hindu', 'Brahmin', 'Software Engineer', 'Bachelor of Technology', 'Mumbai', 'Maharashtra', 'I am a software engineer looking for a life partner who shares similar values.', 'Looking for a caring and understanding partner.'),
(4, '1992-08-20', 'FEMALE', 'Hindu', 'Kayastha', 'Teacher', 'Master of Arts', 'Delhi', 'Delhi', 'I am a teacher passionate about education and family values.', 'Seeking a supportive partner who values family and education.'),
(5, '1988-12-10', 'MALE', 'Christian', 'Catholic', 'Doctor', 'Doctor of Medicine', 'Chennai', 'Tamil Nadu', 'I am a doctor dedicated to helping others and building a loving family.', 'Looking for a compassionate partner who shares my commitment to service.');

-- Insert sample events
INSERT INTO events (organizer_id, title, description, event_date, venue, city, state, max_participants, registration_fee) VALUES
(2, 'Matrimony Meetup 2024', 'A grand matrimonial event bringing together eligible singles from across the city.', '2024-12-15 18:00:00', 'Grand Ballroom, Taj Hotel', 'Mumbai', 'Maharashtra', 200, 500.00),
(2, 'Speed Dating Event', 'Fast-paced speed dating event for busy professionals.', '2024-11-20 19:00:00', 'Lounge Bar, ITC Grand', 'Delhi', 'Delhi', 50, 1000.00);

-- Insert sample messages
INSERT INTO messages (sender_id, receiver_id, subject, content) VALUES
(3, 4, 'Hello from John', 'Hi Jane, I came across your profile and would like to know more about you.'),
(4, 3, 'Re: Hello from John', 'Hello John, thank you for reaching out. I am interested in learning more about you as well.');

-- Insert sample user interests
INSERT INTO user_interests (from_user_id, to_user_id, interest_type, compatibility_score, notes) VALUES
(3, 4, 'LIKE', 85.5, 'Great compatibility match based on shared interests.'),
(4, 3, 'SHORTLIST', 88.0, 'Very promising candidate for further interaction.');

-- Insert sample event registrations
INSERT INTO event_registrations (user_id, event_id, payment_status) VALUES
(3, 1, 'PAID'),
(4, 1, 'PENDING'),
(5, 2, 'PAID');

-- Insert sample payments
INSERT INTO payments (user_id, payment_type, amount, payment_method, transaction_id, status) VALUES
(3, 'SUBSCRIPTION', 999.00, 'Credit Card', 'TXN_001', 'COMPLETED'),
(4, 'EVENT_REGISTRATION', 500.00, 'UPI', 'TXN_002', 'COMPLETED'),
(5, 'SUBSCRIPTION', 1999.00, 'Net Banking', 'TXN_003', 'COMPLETED');

-- Insert sample notifications
INSERT INTO notifications (user_id, notification_type, title, message, action_url) VALUES
(3, 'NEW_MATCH', 'New Match Found!', 'You have a new potential match. Check it out!', '/matches'),
(4, 'NEW_MESSAGE', 'New Message Received', 'You have received a new message from John.', '/messages');

-- Insert sample notification preferences
INSERT INTO notification_preferences (user_id, email_notifications, push_notifications, sms_notifications, new_matches, new_messages, event_updates) VALUES
(3, true, true, false, true, true, true),
(4, true, true, true, true, true, false),
(5, false, true, false, true, false, true);

-- Insert sample success stories
INSERT INTO success_stories (couple_user1_id, couple_user2_id, story_title, story_content, wedding_date, is_featured, submitted_by) VALUES
(3, 4, 'Our Love Story', 'We met through this platform and instantly connected. After several meaningful conversations, we knew we had found our perfect match. Our wedding was a beautiful celebration of love and commitment.', '2024-10-15', true, 3);
