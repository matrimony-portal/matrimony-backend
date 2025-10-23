# Matrimony Portal Database Design

## Table of Contents

1. [Introduction](#1-introduction)
2. [System Requirements](#2-system-requirements)
3. [Database Design](#3-database-design)
   - 3.1 [Entity-Relationship Model](#31-entity-relationship-model)
   - 3.2 [Tables](#32-tables)
   - 3.3 [Relationships](#33-relationships)
   - 3.4 [Constraints and Business Rules](#34-constraints-and-business-rules)
   - 3.5 [Indexes](#35-indexes)
4. [Implementation](#4-implementation)
   - 4.1 [SQL Schema](#41-sql-schema)
   - 4.2 [Sample Data](#42-sample-data)

## 1. Introduction

The Matrimony Portal is a comprehensive web application designed to facilitate matrimonial matchmaking in India. This database design document outlines the complete data architecture for the platform, supporting user registration, profile management, matching algorithms, messaging, event organization, and administrative functions.

### Purpose

This document serves as the blueprint for the MySQL database that will power the Matrimony Portal backend. It defines 16 core tables covering all major functionality areas including user management, subscriptions, profiles, events, media, communications, and administrative features.

### Scope

The database design supports:

- User authentication and role-based access (Admin, Event Organizer, Regular User)
- Subscription-based service tiers (Free, Premium, VIP)
- Detailed matrimonial profiles with personal, professional, and preference information
- Event management and registration system
- Private messaging between users
- Interest expression and compatibility matching
- Media upload and management
- Notification and reporting systems
- Audit logging for security and compliance
- Success story showcase

### Target Audience

- Backend developers implementing the Spring Boot application
- Database administrators setting up MySQL
- QA engineers testing data integrity
- Product managers understanding data relationships

## 2. System Requirements

### Functional Requirements

- **User Management**: Registration, authentication, profile creation, and management
- **Subscription System**: Tiered pricing with feature limitations and billing cycles
- **Profile Matching**: Compatibility scoring and interest management
- **Communication**: Private messaging and notification systems
- **Event Management**: Event creation, registration, and attendance tracking
- **Media Management**: Photo and document upload with organization
- **Administrative**: User reporting, audit logging, and content moderation
- **Analytics**: Success story collection and featured content

### Non-Functional Requirements

- **Performance**: Support for 10,000+ concurrent users with sub-second query response times
- **Scalability**: Horizontal scaling capability for growing user base
- **Security**: Data encryption, access controls, and audit trails
- **Data Integrity**: Comprehensive constraints and validation rules
- **Availability**: 99.9% uptime with backup and recovery procedures
- **Compliance**: GDPR compliance for user data protection

### Assumptions

- MySQL 8.0+ as the database platform
- Spring Boot with JPA/Hibernate for ORM
- JSON support for flexible data storage
- Indian market focus with INR currency
- Mobile-responsive web application

## 3. Database Design

### 3.1 Entity-Relationship Model

The database follows a normalized relational design with the following key relationships:

#### Core Relationships

- **users** ↔ **profiles** (1:1): Each user has exactly one profile
- **users** ↔ **subscriptions** (1:1): Each user has one active subscription
- **subscription_plans** → **subscriptions** (1:many): Plans can have multiple subscribers

#### User Interaction Relationships

- **users** → **messages** (1:many): Users can send/receive multiple messages
- **users** → **user_interests** (1:many): Users can express interest in multiple profiles
- **users** → **profile_photos** (1:many): Users can have multiple photos
- **users** → **media_gallery** (1:many): Users can upload multiple media files

#### Event Management Relationships

- **users** → **events** (1:many): Event organizers can create multiple events
- **users** → **event_registrations** (1:many): Users can register for multiple events
- **events** → **event_registrations** (1:many): Events can have multiple registrations

#### Administrative Relationships

- **users** → **payments** (1:many): Users can have multiple payment records
- **users** → **notifications** (1:many): Users can receive multiple notifications
- **users** → **user_reports** (1:many): Users can file/subject to multiple reports
- **users** → **audit_logs** (1:many): User actions are logged
- **users** → **success_stories** (1:many): Users can be featured in success stories

#### Relationship Types

- **One-to-One (1:1)**: users-profiles, users-subscriptions, users-notification_preferences
- **One-to-Many (1:many)**: All other relationships where one entity relates to multiple others
- **Self-Referencing**: messages (sender/receiver), user_interests (from/to), user_reports (reporter/reported)

### 3.2 Tables

#### Core Tables

##### 1. users

**Purpose**: User authentication and basic information

| Column     | Type         | Constraints                | Description                              |
| ---------- | ------------ | -------------------------- | ---------------------------------------- |
| id         | BIGINT       | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each user          |
| email      | VARCHAR(255) | UNIQUE, NOT NULL           | User email address (login credential)    |
| password   | VARCHAR(255) | NOT NULL                   | Hashed password                          |
| first_name | VARCHAR(100) | NOT NULL                   | User's first name                        |
| last_name  | VARCHAR(100) | NOT NULL                   | User's last name                         |
| phone      | VARCHAR(20)  |                            | Contact phone number                     |
| role       | ENUM         | NOT NULL                   | User role (ADMIN, EVENT_ORGANIZER, USER) |
| is_active  | BOOLEAN      | DEFAULT TRUE               | Account activation status                |
| created_at | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Account creation timestamp               |
| updated_at | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Last update timestamp                    |

**Key Features**:

- Authentication: email (unique), password (hashed)
- Profile: first_name, last_name, phone
- Access: role (ADMIN, EVENT_ORGANIZER, USER), is_active

##### 2. subscription_plans

**Purpose**: Defines available subscription tiers

| Column            | Type          | Constraints                | Description                                  |
| ----------------- | ------------- | -------------------------- | -------------------------------------------- |
| id                | BIGINT        | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each plan              |
| plan_name         | VARCHAR(50)   | UNIQUE, NOT NULL           | Display name of the plan                     |
| plan_type         | ENUM          | UNIQUE, NOT NULL           | Plan type (FREE, PREMIUM, VIP)               |
| price_monthly     | DECIMAL(10,2) | DEFAULT 0                  | Monthly subscription price                   |
| price_yearly      | DECIMAL(10,2) | DEFAULT 0                  | Yearly subscription price                    |
| max_profiles_view | INTEGER       | DEFAULT 10                 | Maximum profiles viewable per month          |
| max_messages      | INTEGER       | DEFAULT 5                  | Maximum messages sendable per month          |
| max_photos        | INTEGER       | DEFAULT 3                  | Maximum photos uploadable                    |
| priority_matching | BOOLEAN       | DEFAULT FALSE              | Priority in matching algorithms              |
| advanced_filters  | BOOLEAN       | DEFAULT FALSE              | Access to advanced search filters            |
| customer_support  | ENUM          | DEFAULT 'EMAIL'            | Support level (NONE, EMAIL, PHONE, PRIORITY) |
| is_active         | BOOLEAN       | DEFAULT TRUE               | Plan availability status                     |
| created_at        | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP  | Plan creation timestamp                      |
| updated_at        | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP  | Last update timestamp                        |

**Key Features**:

- Plans: FREE (₹0), PREMIUM (₹999/month), VIP (₹1999/month)
- Features: max_profiles_view, max_messages, max_photos, priority_matching, advanced_filters, customer_support
- Limits: FREE: 10 profiles/5 messages/3 photos, PREMIUM: 100/50/10, VIP: unlimited

##### 3. subscriptions

**Purpose**: User subscription management

| Column            | Type          | Constraints                          | Description                              |
| ----------------- | ------------- | ------------------------------------ | ---------------------------------------- |
| id                | BIGINT        | PRIMARY KEY AUTO_INCREMENT           | Unique identifier for each subscription  |
| user_id           | BIGINT        | UNIQUE, NOT NULL, FK → users.id      | Reference to the subscribing user        |
| plan_id           | BIGINT        | NOT NULL, FK → subscription_plans.id | Reference to the subscription plan       |
| billing_cycle     | ENUM          | DEFAULT 'MONTHLY'                    | Billing frequency (MONTHLY, YEARLY)      |
| start_date        | DATE          | NOT NULL                             | Subscription start date                  |
| end_date          | DATE          |                                      | Subscription end date (null for ongoing) |
| is_active         | BOOLEAN       | DEFAULT TRUE                         | Current subscription status              |
| auto_renew        | BOOLEAN       | DEFAULT TRUE                         | Auto-renewal preference                  |
| payment_amount    | DECIMAL(10,2) |                                      | Last payment amount                      |
| payment_date      | TIMESTAMP     |                                      | Last payment timestamp                   |
| next_billing_date | DATE          |                                      | Next billing date                        |
| created_at        | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP            | Subscription creation timestamp          |
| updated_at        | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP            | Last update timestamp                    |

**Key Features**:

- Billing: Monthly/Yearly cycles with auto-renewal
- Status: Active/Inactive with next billing dates
- Relationship: Links users to subscription_plans

##### 4. profiles

**Purpose**: Detailed matrimonial profile information

| Column         | Type          | Constraints                     | Description                                |
| -------------- | ------------- | ------------------------------- | ------------------------------------------ |
| id             | BIGINT        | PRIMARY KEY AUTO_INCREMENT      | Unique identifier for each profile         |
| user_id        | BIGINT        | UNIQUE, NOT NULL, FK → users.id | Reference to the user owning the profile   |
| date_of_birth  | DATE          | NOT NULL                        | User's date of birth                       |
| gender         | ENUM          | NOT NULL                        | User's gender (MALE, FEMALE, OTHER)        |
| religion       | VARCHAR(50)   |                                 | User's religion                            |
| caste          | VARCHAR(50)   |                                 | User's caste/community                     |
| occupation     | VARCHAR(100)  |                                 | User's profession                          |
| education      | VARCHAR(100)  |                                 | User's educational qualification           |
| income         | DECIMAL(10,2) |                                 | User's annual income                       |
| marital_status | ENUM          | DEFAULT 'SINGLE'                | Marital status (SINGLE, DIVORCED, WIDOWED) |
| height_cm      | INTEGER       |                                 | Height in centimeters                      |
| weight_kg      | INTEGER       |                                 | Weight in kilograms                        |
| city           | VARCHAR(100)  |                                 | Current city of residence                  |
| state          | VARCHAR(100)  |                                 | Current state of residence                 |
| country        | VARCHAR(100)  | DEFAULT 'India'                 | Country of residence                       |
| about_me       | TEXT          |                                 | Self-description/bio                       |
| preferences    | TEXT          |                                 | Partner preferences                        |
| is_verified    | BOOLEAN       | DEFAULT FALSE                   | Profile verification status                |
| created_at     | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP       | Profile creation timestamp                 |
| updated_at     | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP       | Last update timestamp                      |

**Key Features**:

- Personal: date_of_birth, gender, religion, caste, occupation, education, income
- Physical: height_cm, weight_kg, city, state, country
- Content: about_me, preferences, is_verified

#### Feature Tables

##### 5. events

**Purpose**: Matrimonial events organized by EVENT_ORGANIZER role users

| Column           | Type          | Constraints                | Description                                            |
| ---------------- | ------------- | -------------------------- | ------------------------------------------------------ |
| id               | BIGINT        | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each event                       |
| organizer_id     | BIGINT        | NOT NULL, FK → users.id    | Reference to the event organizer                       |
| title            | VARCHAR(255)  | NOT NULL                   | Event title                                            |
| description      | TEXT          |                            | Detailed event description                             |
| event_date       | TIMESTAMP     | NOT NULL                   | Scheduled event date and time                          |
| venue            | VARCHAR(255)  | NOT NULL                   | Event venue/location details                           |
| city             | VARCHAR(100)  | NOT NULL                   | Event city                                             |
| state            | VARCHAR(100)  | NOT NULL                   | Event state                                            |
| max_participants | INTEGER       |                            | Maximum number of participants allowed                 |
| registration_fee | DECIMAL(10,2) | DEFAULT 0                  | Event registration fee                                 |
| status           | ENUM          | DEFAULT 'UPCOMING'         | Event status (UPCOMING, ONGOING, COMPLETED, CANCELLED) |
| created_at       | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP  | Event creation timestamp                               |
| updated_at       | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP  | Last update timestamp                                  |

**Key Features**:

- Details: title, description, event_date, venue, location
- Capacity: max_participants, registration_fee
- Status: UPCOMING, ONGOING, COMPLETED, CANCELLED

##### 6. profile_photos

**Purpose**: User profile images

| Column      | Type         | Constraints                | Description                               |
| ----------- | ------------ | -------------------------- | ----------------------------------------- |
| id          | BIGINT       | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each photo          |
| user_id     | BIGINT       | NOT NULL, FK → users.id    | Reference to the user owning the photo    |
| file_name   | VARCHAR(255) | NOT NULL                   | Original file name                        |
| file_path   | VARCHAR(500) | NOT NULL                   | Server file path                          |
| file_size   | BIGINT       | NOT NULL                   | File size in bytes                        |
| mime_type   | VARCHAR(100) | NOT NULL                   | MIME type (e.g., image/jpeg)              |
| is_primary  | BOOLEAN      | DEFAULT FALSE              | Whether this is the primary profile photo |
| sort_order  | INTEGER      | DEFAULT 0                  | Display order for multiple photos         |
| alt_text    | VARCHAR(255) |                            | Alternative text for accessibility        |
| uploaded_at | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Upload timestamp                          |

**Key Features**:

- Metadata: file_name, file_path, file_size, mime_type
- Display: is_primary, sort_order, alt_text

##### 7. media_gallery

**Purpose**: Additional media files (videos, audio, documents)

| Column      | Type         | Constraints                | Description                            |
| ----------- | ------------ | -------------------------- | -------------------------------------- |
| id          | BIGINT       | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each media file  |
| user_id     | BIGINT       | NOT NULL, FK → users.id    | Reference to the user owning the media |
| media_type  | ENUM         | NOT NULL                   | Type of media (VIDEO, AUDIO, DOCUMENT) |
| file_name   | VARCHAR(255) | NOT NULL                   | Original file name                     |
| file_path   | VARCHAR(500) | NOT NULL                   | Server file path                       |
| file_size   | BIGINT       | NOT NULL                   | File size in bytes                     |
| mime_type   | VARCHAR(100) | NOT NULL                   | MIME type                              |
| title       | VARCHAR(255) |                            | Media title                            |
| description | TEXT         |                            | Media description                      |
| uploaded_at | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Upload timestamp                       |

**Key Features**:

- Types: VIDEO, AUDIO, DOCUMENT
- Content: title, description, file metadata

##### 8. messages

**Purpose**: Private messaging between users

| Column      | Type         | Constraints                | Description                        |
| ----------- | ------------ | -------------------------- | ---------------------------------- |
| id          | BIGINT       | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each message |
| sender_id   | BIGINT       | NOT NULL, FK → users.id    | Reference to the message sender    |
| receiver_id | BIGINT       | NOT NULL, FK → users.id    | Reference to the message receiver  |
| subject     | VARCHAR(255) |                            | Message subject line               |
| content     | TEXT         | NOT NULL                   | Message content                    |
| is_read     | BOOLEAN      | DEFAULT FALSE              | Read status                        |
| sent_at     | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Message sent timestamp             |
| read_at     | TIMESTAMP    |                            | Message read timestamp             |

**Key Features**:

- Communication: sender_id, receiver_id, subject, content
- Status: is_read, sent_at, read_at
- Constraints: Cannot message self

##### 9. user_interests

**Purpose**: User interactions and compatibility

| Column              | Type         | Constraints                | Description                                |
| ------------------- | ------------ | -------------------------- | ------------------------------------------ |
| id                  | BIGINT       | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each interest record |
| from_user_id        | BIGINT       | NOT NULL, FK → users.id    | Reference to the user expressing interest  |
| to_user_id          | BIGINT       | NOT NULL, FK → users.id    | Reference to the target user               |
| interest_type       | ENUM         | NOT NULL                   | Type of interest (LIKE, SHORTLIST, BLOCK)  |
| compatibility_score | DECIMAL(5,2) |                            | Compatibility score (0-100)                |
| notes               | TEXT         |                            | Personal notes about the interest          |
| created_at          | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Interest creation timestamp                |

**Key Features**:

- Types: LIKE, SHORTLIST, BLOCK
- Features: compatibility_score (0-100), personal notes
- Constraints: Cannot interact with self

##### 10. event_registrations

**Purpose**: User registrations for events

| Column            | Type        | Constraints                | Description                              |
| ----------------- | ----------- | -------------------------- | ---------------------------------------- |
| id                | BIGINT      | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each registration  |
| user_id           | BIGINT      | NOT NULL, FK → users.id    | Reference to the registering user        |
| event_id          | BIGINT      | NOT NULL, FK → events.id   | Reference to the event                   |
| registration_date | TIMESTAMP   | DEFAULT CURRENT_TIMESTAMP  | Registration timestamp                   |
| payment_status    | ENUM        | DEFAULT 'PENDING'          | Payment status (PENDING, PAID, REFUNDED) |
| attended          | BOOLEAN     | DEFAULT FALSE              | Attendance status                        |
| notes             | TEXT        |                            | Additional registration notes            |

**Key Features**:

- Status: payment_status (PENDING, PAID, REFUNDED), attended
- Tracking: registration_date, notes

#### Administrative Tables

##### 11. payments

**Purpose**: Payment transaction records

| Column         | Type          | Constraints                | Description                                                  |
| -------------- | ------------- | -------------------------- | ------------------------------------------------------------ |
| id             | BIGINT        | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each payment                           |
| user_id        | BIGINT        | NOT NULL, FK → users.id    | Reference to the paying user                                 |
| payment_type   | ENUM          | NOT NULL                   | Type of payment (SUBSCRIPTION, EVENT_REGISTRATION, DONATION) |
| amount         | DECIMAL(10,2) | NOT NULL                   | Payment amount                                               |
| currency       | VARCHAR(3)    | DEFAULT 'INR'              | Currency code                                                |
| payment_method | VARCHAR(50)   |                            | Payment method used                                          |
| transaction_id | VARCHAR(255)  | UNIQUE                     | Unique transaction identifier                                |
| status         | ENUM          | NOT NULL                   | Payment status (PENDING, COMPLETED, FAILED, REFUNDED)        |
| payment_date   | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP  | Payment timestamp                                            |
| notes          | TEXT          |                            | Additional payment notes                                     |

**Key Features**:

- Types: SUBSCRIPTION, EVENT_REGISTRATION, DONATION
- Status: PENDING, COMPLETED, FAILED, REFUNDED
- Tracking: transaction_id (unique), payment_method

##### 12. notifications

**Purpose**: User notification system

| Column            | Type         | Constraints                | Description                             |
| ----------------- | ------------ | -------------------------- | --------------------------------------- |
| id                | BIGINT       | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each notification |
| user_id           | BIGINT       | NOT NULL, FK → users.id    | Reference to the recipient user         |
| notification_type | VARCHAR(50)  | NOT NULL                   | Type of notification                    |
| title             | VARCHAR(255) | NOT NULL                   | Notification title                      |
| message           | TEXT         | NOT NULL                   | Notification message content            |
| is_read           | BOOLEAN      | DEFAULT FALSE              | Read status                             |
| action_url        | VARCHAR(500) |                            | URL for notification action             |
| created_at        | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Notification creation timestamp         |
| read_at           | TIMESTAMP    |                            | Notification read timestamp             |

**Key Features**:

- Content: notification_type, title, message, action_url
- Status: is_read, created_at, read_at

##### 13. notification_preferences

**Purpose**: User notification settings

| Column              | Type      | Constraints                     | Description                                  |
| ------------------- | --------- | ------------------------------- | -------------------------------------------- |
| id                  | BIGINT    | PRIMARY KEY AUTO_INCREMENT      | Unique identifier for each preference record |
| user_id             | BIGINT    | UNIQUE, NOT NULL, FK → users.id | Reference to the user                        |
| email_notifications | BOOLEAN   | DEFAULT TRUE                    | Email notification preference                |
| push_notifications  | BOOLEAN   | DEFAULT TRUE                    | Push notification preference                 |
| sms_notifications   | BOOLEAN   | DEFAULT FALSE                   | SMS notification preference                  |
| new_matches         | BOOLEAN   | DEFAULT TRUE                    | New matches notifications                    |
| new_messages        | BOOLEAN   | DEFAULT TRUE                    | New messages notifications                   |
| event_updates       | BOOLEAN   | DEFAULT TRUE                    | Event update notifications                   |
| profile_views       | BOOLEAN   | DEFAULT FALSE                   | Profile view notifications                   |
| created_at          | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP       | Preferences creation timestamp               |
| updated_at          | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP       | Last update timestamp                        |

**Key Features**:

- Channels: email, push, SMS
- Events: new_matches, new_messages, event_updates, profile_views

##### 14. audit_logs

**Purpose**: Security and compliance logging

| Column      | Type         | Constraints                | Description                                         |
| ----------- | ------------ | -------------------------- | --------------------------------------------------- |
| id          | BIGINT       | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each audit log                |
| user_id     | BIGINT       | FK → users.id              | Reference to the user (nullable for system actions) |
| action      | VARCHAR(100) | NOT NULL                   | Action performed                                    |
| entity_type | VARCHAR(50)  | NOT NULL                   | Type of entity affected                             |
| entity_id   | BIGINT       |                            | ID of the affected entity                           |
| old_values  | JSON         |                            | Previous values before change                       |
| new_values  | JSON         |                            | New values after change                             |
| ip_address  | VARCHAR(45)  |                            | IP address of the user                              |
| user_agent  | TEXT         |                            | User agent string                                   |
| created_at  | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Log creation timestamp                              |

**Key Features**:

- Tracking: user_id, action, entity_type, entity_id
- Data: old_values, new_values (JSON)
- Context: ip_address, user_agent

##### 15. user_reports

**Purpose**: User reporting system for inappropriate content

| Column           | Type        | Constraints                | Description                                                 |
| ---------------- | ----------- | -------------------------- | ----------------------------------------------------------- |
| id               | BIGINT      | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each report                           |
| reporter_id      | BIGINT      | NOT NULL, FK → users.id    | Reference to the reporting user                             |
| reported_user_id | BIGINT      | NOT NULL, FK → users.id    | Reference to the reported user                              |
| report_type      | VARCHAR(50) | NOT NULL                   | Type of report                                              |
| description      | TEXT        | NOT NULL                   | Detailed report description                                 |
| status           | ENUM        | DEFAULT 'PENDING'          | Report status (PENDING, INVESTIGATING, RESOLVED, DISMISSED) |
| admin_notes      | TEXT        |                            | Administrator notes                                         |
| created_at       | TIMESTAMP   | DEFAULT CURRENT_TIMESTAMP  | Report creation timestamp                                   |
| resolved_at      | TIMESTAMP   |                            | Report resolution timestamp                                 |

**Key Features**:

- Process: reporter_id, reported_user_id, report_type, description
- Resolution: status (PENDING, INVESTIGATING, RESOLVED, DISMISSED), admin_notes
- Constraints: Cannot report self

##### 16. success_stories

**Purpose**: Featured success stories and testimonials

| Column          | Type         | Constraints                | Description                              |
| --------------- | ------------ | -------------------------- | ---------------------------------------- |
| id              | BIGINT       | PRIMARY KEY AUTO_INCREMENT | Unique identifier for each success story |
| couple_user1_id | BIGINT       | NOT NULL, FK → users.id    | Reference to first couple member         |
| couple_user2_id | BIGINT       | NOT NULL, FK → users.id    | Reference to second couple member        |
| story_title     | VARCHAR(255) | NOT NULL                   | Story title                              |
| story_content   | TEXT         | NOT NULL                   | Full story content                       |
| wedding_date    | DATE         |                            | Wedding date                             |
| photos          | JSON         |                            | Array of photo URLs/metadata             |
| is_featured     | BOOLEAN      | DEFAULT FALSE              | Featured status                          |
| submitted_by    | BIGINT       | FK → users.id              | Reference to submitter (nullable)        |
| approved_by     | BIGINT       | FK → users.id              | Reference to approver (nullable)         |
| created_at      | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  | Story creation timestamp                 |
| approved_at     | TIMESTAMP    |                            | Story approval timestamp                 |

**Key Features**:

- Content: couple_user1_id, couple_user2_id, story_title, story_content
- Media: photos (JSON array), wedding_date
- Status: is_featured, submitted_by, approved_by

### 3.3 Relationships

#### Foreign Key Constraints

- **profiles.user_id** → **users.id** (CASCADE on delete)
- **subscriptions.user_id** → **users.id** (CASCADE on delete)
- **events.organizer_id** → **users.id** (RESTRICT on delete - prevent deleting organizers with events)
- **profile_photos.user_id** → **users.id** (CASCADE on delete)
- **media_gallery.user_id** → **users.id** (CASCADE on delete)
- **messages.sender_id** → **users.id** (RESTRICT on delete)
- **messages.receiver_id** → **users.id** (RESTRICT on delete)
- **user_interests.from_user_id** → **users.id** (CASCADE on delete)
- **user_interests.to_user_id** → **users.id** (CASCADE on delete)
- **event_registrations.user_id** → **users.id** (CASCADE on delete)
- **event_registrations.event_id** → **events.id** (CASCADE on delete)
- **payments.user_id** → **users.id** (RESTRICT on delete)
- **notifications.user_id** → **users.id** (CASCADE on delete)
- **notification_preferences.user_id** → **users.id** (CASCADE on delete)
- **audit_logs.user_id** → **users.id** (SET NULL on delete)
- **user_reports.reporter_id** → **users.id** (RESTRICT on delete)
- **user_reports.reported_user_id** → **users.id** (RESTRICT on delete)
- **success_stories.couple_user1_id** → **users.id** (RESTRICT on delete)
- **success_stories.couple_user2_id** → **users.id** (RESTRICT on delete)
- **success_stories.submitted_by** → **users.id** (SET NULL on delete)
- **success_stories.approved_by** → **users.id** (SET NULL on delete)

#### Relationship Types

- **One-to-One (1:1)**: users ↔ profiles (each user has exactly one profile)
- **One-to-One (1:1)**: users ↔ subscriptions (each user has one subscription record)
- **One-to-One (1:1)**: users ↔ notification_preferences (each user has notification preferences)
- **Many-to-One (1:many)**: events → users (many events can be organized by one event organizer)
- **Many-to-One (1:many)**: profile_photos → users (many photos per user)
- **Many-to-One (1:many)**: media_gallery → users (many media files per user)
- **Many-to-One (1:many)**: messages → users (sender and receiver relationships)
- **Many-to-One (1:many)**: user_interests → users (from_user and to_user relationships)
- **Many-to-One (1:many)**: event_registrations → users (many registrations per user)
- **Many-to-One (1:many)**: event_registrations → events (many registrations per event)
- **Many-to-One (1:many)**: payments → users (many payments per user)
- **Many-to-One (1:many)**: notifications → users (many notifications per user)
- **Many-to-One (1:many)**: audit_logs → users (many logs per user, nullable)
- **Many-to-One (1:many)**: user_reports → users (reporter and reported user relationships)
- **Many-to-One (1:many)**: success_stories → users (multiple relationships for couple, submitter, approver)
- **Self-Referencing**: messages (sender/receiver), user_interests (from/to), user_reports (reporter/reported)

### 3.4 Constraints and Business Rules

#### Data Integrity

- Email addresses must be unique across all users
- User roles are restricted to predefined options (ADMIN, EVENT_ORGANIZER, USER)
- Subscription plans are restricted to predefined options (FREE, PREMIUM, VIP)
- Each user can have only one profile and one subscription record (enforced by UNIQUE constraints)
- Gender values are restricted to predefined options (MALE, FEMALE, OTHER)
- Marital status values are restricted to predefined options (SINGLE, DIVORCED, WIDOWED)
- Event status values are restricted to predefined options (UPCOMING, ONGOING, COMPLETED, CANCELLED)
- Media types are restricted to predefined options (VIDEO, AUDIO, DOCUMENT)
- Interest types are restricted to predefined options (LIKE, SHORTLIST, BLOCK)
- Payment types are restricted to predefined options (SUBSCRIPTION, EVENT_REGISTRATION, DONATION)
- Payment statuses are restricted to predefined options (PENDING, COMPLETED, FAILED, REFUNDED)
- Report statuses are restricted to predefined options (PENDING, INVESTIGATING, RESOLVED, DISMISSED)
- Registration payment statuses are restricted to predefined options (PENDING, PAID, REFUNDED)
- Country defaults to 'India' for the target market
- Only users with EVENT_ORGANIZER role can create events
- Event organizers cannot be deleted if they have associated events
- Users cannot send messages to themselves
- Users cannot express interest in themselves
- Users cannot report themselves
- Compatibility scores must be between 0 and 100 if provided
- File sizes must be reasonable (application-level validation)
- Transaction IDs must be unique for payments

#### Validation Rules

- All required fields (email, password, names, role, date_of_birth, gender) must be provided
- Email format should be validated at application level
- Date of birth should be a valid past date
- Subscription start_date should be current or future date
- End_date should be after start_date if provided
- Income should be a positive value if provided
- Height and weight should be reasonable positive values if provided
- Event_date should be in the future for new events
- Registration_fee should be non-negative
- Max_participants should be positive if specified
- File uploads should have reasonable size limits (application-level validation)
- Image files should be validated for correct MIME types
- Message content should not be empty and within length limits
- Payment amounts should be positive
- Compatibility scores should be between 0 and 100
- Wedding dates in success stories should be in the past
- IP addresses should be valid IPv4 or IPv6 format
- URLs should be properly formatted

### 3.5 Indexes

For optimal query performance, the following indexes are recommended:

1. `idx_users_email` on users(email) - for login lookups
2. `idx_users_role` on users(role) - for role-based queries
3. `idx_subscriptions_user_id` on subscriptions(user_id) - for subscription lookups
4. `idx_subscriptions_plan_type` on subscriptions(plan_type) - for plan-based queries
5. `idx_profiles_user_id` on profiles(user_id) - for profile lookups by user
6. `idx_profiles_gender` on profiles(gender) - for gender-based searches
7. `idx_profiles_religion` on profiles(religion) - for religion-based searches
8. `idx_profiles_city` on profiles(city) - for location-based searches
9. `idx_events_organizer_id` on events(organizer_id) - for organizer's events
10. `idx_events_event_date` on events(event_date) - for upcoming events
11. `idx_events_city` on events(city) - for location-based event searches
12. `idx_profile_photos_user_id` on profile_photos(user_id) - for user's photos
13. `idx_profile_photos_is_primary` on profile_photos(is_primary) - for primary photo queries
14. `idx_media_gallery_user_id` on media_gallery(user_id) - for user's media
15. `idx_messages_sender_id` on messages(sender_id) - for sent messages
16. `idx_messages_receiver_id` on messages(receiver_id) - for received messages
17. `idx_messages_is_read` on messages(is_read) - for unread messages
18. `idx_user_interests_from_user_id` on user_interests(from_user_id) - for user's interests
19. `idx_user_interests_to_user_id` on user_interests(to_user_id) - for received interests
20. `idx_user_interests_interest_type` on user_interests(interest_type) - for interest type queries
21. `idx_event_registrations_user_id` on event_registrations(user_id) - for user's registrations
22. `idx_event_registrations_event_id` on event_registrations(event_id) - for event registrations
23. `idx_payments_user_id` on payments(user_id) - for user's payments
24. `idx_payments_status` on payments(status) - for payment status queries
25. `idx_notifications_user_id` on notifications(user_id) - for user's notifications
26. `idx_notifications_is_read` on notifications(is_read) - for unread notifications
27. `idx_audit_logs_user_id` on audit_logs(user_id) - for user's audit logs
28. `idx_audit_logs_entity_type` on audit_logs(entity_type) - for entity type queries
29. `idx_user_reports_reporter_id` on user_reports(reporter_id) - for user's reports
30. `idx_user_reports_reported_user_id` on user_reports(reported_user_id) - for reports against user
31. `idx_user_reports_status` on user_reports(status) - for report status queries
32. `idx_success_stories_is_featured` on success_stories(is_featured) - for featured stories

## 4. Implementation

### 4.1 SQL Schema

The complete SQL schema for creating all tables with constraints and indexes can be found in the `schema.sql` file in this repository.

#### Key Schema Features:

- **Data Types**: Uses MySQL-specific types like BIGINT AUTO_INCREMENT, JSON, ENUM, and VARCHAR for IP addresses
- **Constraints**: ENUM constraints for data validation instead of CHECK constraints
- **Foreign Keys**: Proper referential integrity with CASCADE/RESTRICT/SET NULL actions
- **Indexes**: Performance indexes for optimal query execution
- **Auto-Update**: Automatic `updated_at` timestamp updates with ON UPDATE CURRENT_TIMESTAMP
- **JSON Support**: Native JSON data type for flexible data storage

#### Database Setup Instructions:

1. Create the database:

```sql
CREATE DATABASE matrimony_portal;
USE matrimony_portal;
```

2. Run the schema.sql file to create all tables, indexes, and sample data

3. Verify the installation:

```sql
SELECT table_name FROM information_schema.tables WHERE table_schema = 'matrimony_portal';
```

### 4.2 Sample Data

Sample data is included in the `schema.sql` file and covers all 16 tables with realistic test data including:

- **Users**: Admin, Event Organizer, and regular users with different roles
- **Subscription Plans**: FREE, PREMIUM, and VIP tiers with appropriate limits
- **Profiles**: Detailed matrimonial profiles with personal and professional information
- **Events**: Sample matrimonial events with different venues and capacities
- **Interactions**: Messages, interests, and compatibility scores between users
- **Transactions**: Payment records for subscriptions and event registrations
- **Administrative Data**: Notifications, reports, and success stories

#### Testing the Sample Data:

```sql
-- Verify table counts
SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'profiles', COUNT(*) FROM profiles
UNION ALL
SELECT 'events', COUNT(*) FROM events
UNION ALL
SELECT 'messages', COUNT(*) FROM messages;

-- Test relationships
SELECT u.first_name, u.last_name, p.city, p.occupation
FROM users u
JOIN profiles p ON u.id = p.user_id
WHERE u.role = 'USER';
```

---

This comprehensive database design provides a solid foundation for the Matrimony Portal, supporting all current requirements while allowing for future scalability and feature enhancements. The normalized schema ensures data integrity, performance optimizations enable efficient querying, and the security measures protect sensitive user data for a high-traffic matrimonial platform.
