-- Matrimony Portal Database Schema
-- MySQL compatible

-- Users table for authentication and basic info
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'EVENT_ORGANIZER', 'USER') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Subscription plans table for plan definitions
CREATE TABLE subscription_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(50) NOT NULL UNIQUE,
    plan_type ENUM('FREE', 'PREMIUM', 'VIP') NOT NULL UNIQUE,
    price_monthly DECIMAL(10,2) DEFAULT 0,
    price_yearly DECIMAL(10,2) DEFAULT 0,
    max_profiles_view INT DEFAULT 10, -- FREE: 10, PREMIUM: 100, VIP: unlimited
    max_messages INT DEFAULT 5, -- FREE: 5, PREMIUM: 50, VIP: unlimited
    max_photos INT DEFAULT 3, -- FREE: 3, PREMIUM: 10, VIP: unlimited
    priority_matching BOOLEAN DEFAULT FALSE,
    advanced_filters BOOLEAN DEFAULT FALSE,
    customer_support ENUM('NONE', 'EMAIL', 'PHONE', 'PRIORITY') DEFAULT 'EMAIL',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Subscriptions table for user plans
CREATE TABLE subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    plan_id BIGINT NOT NULL,
    billing_cycle ENUM('MONTHLY', 'YEARLY') DEFAULT 'MONTHLY',
    start_date DATE NOT NULL,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    auto_renew BOOLEAN DEFAULT TRUE,
    payment_amount DECIMAL(10,2),
    payment_date TIMESTAMP NULL,
    next_billing_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id)
);

-- Profiles table for detailed matrimonial information
CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    religion VARCHAR(50),
    caste VARCHAR(50),
    occupation VARCHAR(100),
    education VARCHAR(100),
    income DECIMAL(10,2),
    marital_status ENUM('SINGLE', 'DIVORCED', 'WIDOWED') DEFAULT 'SINGLE',
    height_cm INT,
    weight_kg INT,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100) DEFAULT 'India',
    about_me TEXT,
    preferences TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Events table for matrimonial events
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organizer_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    venue VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    max_participants INT,
    registration_fee DECIMAL(10,2) DEFAULT 0,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Profile photos table
CREATE TABLE profile_photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    alt_text VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Media gallery table
CREATE TABLE media_gallery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    media_type ENUM('VIDEO', 'AUDIO', 'DOCUMENT') NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    title VARCHAR(255),
    description TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Messages table
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_different_users CHECK (sender_id != receiver_id)
);

-- User interests table
CREATE TABLE user_interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    interest_type ENUM('LIKE', 'SHORTLIST', 'BLOCK') NOT NULL,
    compatibility_score DECIMAL(5,2) CHECK (compatibility_score >= 0 AND compatibility_score <= 100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_different_users_interest CHECK (from_user_id != to_user_id)
);

-- Event registrations table
CREATE TABLE event_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status ENUM('PENDING', 'PAID', 'REFUNDED') DEFAULT 'PENDING',
    attended BOOLEAN DEFAULT FALSE,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- Payments table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    payment_type ENUM('SUBSCRIPTION', 'EVENT_REGISTRATION', 'DONATION') NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) DEFAULT 'INR',
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255) UNIQUE,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Notifications table
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Notification preferences table
CREATE TABLE notification_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    email_notifications BOOLEAN DEFAULT TRUE,
    push_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    new_matches BOOLEAN DEFAULT TRUE,
    new_messages BOOLEAN DEFAULT TRUE,
    event_updates BOOLEAN DEFAULT TRUE,
    profile_views BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Audit logs table
CREATE TABLE audit_logs (
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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- User reports table
CREATE TABLE user_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id BIGINT NOT NULL,
    reported_user_id BIGINT NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('PENDING', 'INVESTIGATING', 'RESOLVED', 'DISMISSED') DEFAULT 'PENDING',
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_different_users_report CHECK (reporter_id != reported_user_id)
);

-- Success stories table
CREATE TABLE success_stories (
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
    approved_at TIMESTAMP NULL,
    FOREIGN KEY (couple_user1_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (couple_user2_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (submitted_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_different_couple_users CHECK (couple_user1_id != couple_user2_id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_subscription_plans_plan_type ON subscription_plans(plan_type);
CREATE INDEX idx_subscription_plans_is_active ON subscription_plans(is_active);
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_plan_id ON subscriptions(plan_id);
CREATE INDEX idx_subscriptions_is_active ON subscriptions(is_active);
CREATE INDEX idx_subscriptions_next_billing_date ON subscriptions(next_billing_date);
CREATE INDEX idx_profiles_user_id ON profiles(user_id);
CREATE INDEX idx_profiles_gender ON profiles(gender);
CREATE INDEX idx_profiles_religion ON profiles(religion);
CREATE INDEX idx_profiles_city ON profiles(city);
CREATE INDEX idx_events_organizer_id ON events(organizer_id);
CREATE INDEX idx_events_event_date ON events(event_date);
CREATE INDEX idx_events_city ON events(city);
CREATE INDEX idx_profile_photos_user_id ON profile_photos(user_id);
CREATE INDEX idx_profile_photos_is_primary ON profile_photos(is_primary);
CREATE INDEX idx_media_gallery_user_id ON media_gallery(user_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_receiver_id ON messages(receiver_id);
CREATE INDEX idx_messages_is_read ON messages(is_read);
CREATE INDEX idx_user_interests_from_user_id ON user_interests(from_user_id);
CREATE INDEX idx_user_interests_to_user_id ON user_interests(to_user_id);
CREATE INDEX idx_user_interests_interest_type ON user_interests(interest_type);
CREATE INDEX idx_event_registrations_user_id ON event_registrations(user_id);
CREATE INDEX idx_event_registrations_event_id ON event_registrations(event_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_user_reports_reporter_id ON user_reports(reporter_id);
CREATE INDEX idx_user_reports_reported_user_id ON user_reports(reported_user_id);
CREATE INDEX idx_user_reports_status ON user_reports(status);
CREATE INDEX idx_success_stories_is_featured ON success_stories(is_featured);

-- Sample data for testing
-- Note: In production, passwords should be properly hashed
INSERT INTO users (email, password, first_name, last_name, phone, role) VALUES
('admin@matrimony.com', '$2a$10$example.hash', 'System', 'Admin', '+1234567890', 'ADMIN'),
('organizer@matrimony.com', '$2a$10$example.hash', 'Event', 'Organizer', '+1234567891', 'EVENT_ORGANIZER'),
('john.doe@example.com', '$2a$10$example.hash', 'John', 'Doe', '+1234567892', 'USER'),
('jane.smith@example.com', '$2a$10$example.hash', 'Jane', 'Smith', '+1234567893', 'USER'),
('alice.johnson@example.com', '$2a$10$example.hash', 'Alice', 'Johnson', '+1234567894', 'USER');

-- Sample subscription plans
INSERT INTO subscription_plans (plan_name, plan_type, price_monthly, price_yearly, max_profiles_view, max_messages, max_photos, priority_matching, advanced_filters, customer_support) VALUES
('Free Plan', 'FREE', 0.00, 0.00, 10, 5, 3, false, false, 'NONE'),
('Premium Plan', 'PREMIUM', 999.00, 9999.00, 100, 50, 10, true, true, 'PHONE'),
('VIP Plan', 'VIP', 1999.00, 19999.00, NULL, NULL, NULL, true, true, 'PRIORITY');

INSERT INTO subscriptions (user_id, plan_id, billing_cycle, start_date, is_active, auto_renew, next_billing_date) VALUES
(1, 3, 'MONTHLY', '2024-01-01', true, true, '2024-02-01'), -- Admin on VIP
(2, 2, 'YEARLY', '2024-01-01', true, true, '2025-01-01'), -- Organizer on Premium
(3, 1, 'MONTHLY', '2024-01-01', true, false, NULL), -- Free user
(4, 2, 'MONTHLY', '2024-01-01', true, true, '2024-02-01'), -- Paid user on Premium
(5, 2, 'MONTHLY', '2024-01-01', true, true, '2024-02-01'); -- Another Premium user

INSERT INTO profiles (user_id, date_of_birth, gender, religion, occupation, city, state, about_me, preferences, is_verified) VALUES
(3, '1990-05-15', 'MALE', 'Hindu', 'Software Engineer', 'Mumbai', 'Maharashtra', 'Looking for a life partner who shares similar values and interests.', 'Seeking an educated, caring partner with good family values.', true),
(4, '1988-03-22', 'FEMALE', 'Christian', 'Doctor', 'Delhi', 'Delhi', 'Passionate about medicine and helping others. Looking for someone who understands my dedication to my profession.', 'Looking for a supportive partner who respects my career and shares family-oriented values.', true),
(5, '1992-08-10', 'FEMALE', 'Hindu', 'Teacher', 'Pune', 'Maharashtra', 'Love teaching and helping children grow. Looking for someone who values education and family.', 'Seeking a kind-hearted, educated man who respects women and family values.', false);

INSERT INTO events (organizer_id, title, description, event_date, venue, city, state, max_participants, registration_fee) VALUES
(2, 'Matrimony Meet & Greet', 'A wonderful opportunity to meet potential life partners in a comfortable environment.', '2024-02-15 18:00:00', 'Grand Ballroom, Taj Hotel', 'Mumbai', 'Maharashtra', 100, 500.00),
(2, 'Speed Dating Event', 'Fast-paced matchmaking event for busy professionals.', '2024-03-10 19:00:00', 'Convention Center', 'Delhi', 'Delhi', 50, 300.00);

INSERT INTO profile_photos (user_id, file_name, file_path, file_size, mime_type, is_primary, sort_order, alt_text) VALUES
(3, 'john_profile.jpg', '/uploads/photos/john_profile.jpg', 2048576, 'image/jpeg', true, 1, 'John Doe profile photo'),
(4, 'jane_profile.jpg', '/uploads/photos/jane_profile.jpg', 1536000, 'image/jpeg', true, 1, 'Jane Smith profile photo');

INSERT INTO messages (sender_id, receiver_id, subject, content, is_read) VALUES
(3, 4, 'Hello from John', 'Hi Jane, I came across your profile and would like to know more about you.', false),
(4, 3, 'Re: Hello from John', 'Hi John, thank you for your message. I''d be happy to chat.', true);

INSERT INTO user_interests (from_user_id, to_user_id, interest_type, compatibility_score, notes) VALUES
(3, 4, 'LIKE', 85.5, 'Great compatibility match based on shared values'),
(4, 3, 'SHORTLIST', 88.2, 'Very interested, good educational background'),
(5, 3, 'LIKE', 72.1, 'Decent match, similar location');

INSERT INTO event_registrations (user_id, event_id, payment_status, attended) VALUES
(3, 1, 'PAID', false),
(4, 1, 'PAID', false),
(5, 2, 'PENDING', false);

INSERT INTO payments (user_id, payment_type, amount, payment_method, transaction_id, status) VALUES
(3, 'SUBSCRIPTION', 999.00, 'Credit Card', 'TXN_001', 'COMPLETED'),
(4, 'EVENT_REGISTRATION', 500.00, 'UPI', 'TXN_002', 'COMPLETED'),
(5, 'SUBSCRIPTION', 1999.00, 'Net Banking', 'TXN_003', 'COMPLETED');

INSERT INTO notifications (user_id, notification_type, title, message, is_read) VALUES
(3, 'NEW_MATCH', 'New Match Found!', 'You have a new potential match. Check it out!', false),
(4, 'NEW_MESSAGE', 'New Message', 'You have received a new message from John.', true);

INSERT INTO notification_preferences (user_id, email_notifications, push_notifications, sms_notifications, new_matches, new_messages) VALUES
(3, true, true, false, true, true),
(4, true, true, true, true, true),
(5, false, true, false, true, false);

INSERT INTO success_stories (couple_user1_id, couple_user2_id, story_title, story_content, wedding_date, is_featured, submitted_by, approved_by) VALUES
(3, 4, 'Our Love Story', 'We met through this platform and instantly connected. After several meetings and getting to know each other, we decided to spend our lives together. The platform helped us find our perfect match!', '2024-01-15', true, 3, 1);
