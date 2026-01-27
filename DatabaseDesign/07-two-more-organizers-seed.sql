-- ===========================================
-- TWO ADDITIONAL ORGANIZERS (similar to organizer@matrimony.com)
-- ===========================================
-- Run AFTER 01-schema, 02-seed, 03-event-organizer-seed.
-- Adds users 15, 16 (EVENT_ORGANIZER), profiles, and events.
-- Password: "password123" (same BCrypt hash as 02/03).
-- Run ONCE; re-running may cause duplicate key errors.
-- ===========================================

USE matrimony_portal;

-- 1. Two more organizers (ids 15, 16)
INSERT INTO users (id, email, password, first_name, last_name, phone, role, status) VALUES
(15, 'organizer2@matrimony.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Meera', 'Reddy', '+919876543401', 'EVENT_ORGANIZER', 'ACTIVE'),
(16, 'organizer3@matrimony.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Arjun', 'Kapoor', '+919876543402', 'EVENT_ORGANIZER', 'ACTIVE');

-- 2. Profiles for organizers 15, 16 (required for View/Edit Profile)
INSERT INTO profiles (user_id, date_of_birth, gender, religion, caste, occupation, education, city, state, about_me, preferences) VALUES
(15, '1988-06-20', 'FEMALE', NULL, NULL, 'Event Organizer', NULL, 'Mumbai', 'Maharashtra', 'I run Mumbai Events Co. and specialize in matrimonial speed dating and cultural evenings. Over 50 events organized with great satisfaction.', NULL),
(16, '1991-03-12', 'MALE', NULL, NULL, 'Event Organizer', NULL, 'Delhi', 'Delhi', 'Experienced organizer for premium matchmaking events across NCR. Focus on dinner meetups and professional networking.', NULL);

-- 3. Events for organizer 15 (Meera)
INSERT INTO events (id, organizer_id, title, description, event_date, venue, city, state, max_participants, registration_fee, status) VALUES
(19, 15, 'Mumbai Speed Dating Night', 'Curated speed dating for professionals. Great vibe and verified profiles.', '2025-05-08 18:30:00', 'The Oberoi, Nariman Point', 'Mumbai', 'Maharashtra', 40, 800.00, 'UPCOMING'),
(20, 15, 'Cultural Evening - Mumbai', 'An evening of music, dance and meaningful introductions.', '2025-05-22 19:00:00', 'Sanskriti Hall, Andheri', 'Mumbai', 'Maharashtra', 60, 600.00, 'UPCOMING');

-- 4. Events for organizer 16 (Arjun)
INSERT INTO events (id, organizer_id, title, description, event_date, venue, city, state, max_participants, registration_fee, status) VALUES
(21, 16, 'Delhi Dinner & Mingle', 'Upscale dinner event for serious singles. Limited seats.', '2025-05-15 19:30:00', 'Indian Accent, The Lodhi', 'Delhi', 'Delhi', 30, 1500.00, 'UPCOMING'),
(22, 16, 'Gurgaon Coffee Meetup', 'Casual coffee meetup for working professionals.', '2025-05-10 11:00:00', 'Blue Tokai, Cyber City', 'Gurgaon', 'Haryana', 25, 350.00, 'UPCOMING');

-- 5. Some registrations for new events (users 3–14 from 02/03)
INSERT INTO event_registrations (user_id, event_id, registration_date, payment_status, attended, notes) VALUES
(3, 19, '2025-04-01 10:00:00', 'PAID', false, NULL),
(4, 19, '2025-04-02 11:00:00', 'PENDING', false, NULL),
(5, 19, '2025-04-03 12:00:00', 'PAID', false, NULL),
(6, 20, '2025-04-05 10:00:00', 'PAID', false, NULL),
(7, 20, '2025-04-06 11:00:00', 'PENDING', false, NULL),
(8, 21, '2025-04-08 10:00:00', 'PAID', false, NULL),
(9, 21, '2025-04-09 11:00:00', 'PENDING', false, NULL),
(10, 22, '2025-04-10 10:00:00', 'PAID', false, NULL),
(11, 22, '2025-04-11 11:00:00', 'PENDING', false, NULL);

-- ===========================================
-- VERIFICATION
-- ===========================================
-- SELECT id, email, first_name, last_name, role FROM users WHERE role = 'EVENT_ORGANIZER' ORDER BY id;
-- SELECT id, organizer_id, title, city FROM events WHERE organizer_id IN (15, 16);
