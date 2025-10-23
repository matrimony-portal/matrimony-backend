# Matrimony Portal Database Design

## Overview

This directory contains the database design and schema for the Matrimony Portal, a web application for matrimonial matchmaking. The database includes 16 tables for user management, profiles, events, messaging, and administrative functions.

## Entity-Relationship Diagram

The database design includes ER diagrams in multiple formats:

- **er_diagram.pdf** - PDF version for viewing
- **er_diagram.svg** - SVG version for web use
- **er_diagram.mwb** - MySQL Workbench file for editing

## Database Specifications

- **Database System**: MySQL 8.0+
- **Total Tables**: 16
- **Primary Keys**: BIGINT AUTO_INCREMENT
- **Data Types**: JSON for flexible data, VARCHAR for IP addresses

## Database Schema

The database includes these table categories:

### Core Tables

1. users - User accounts
2. subscription_plans - Subscription tiers
3. subscriptions - User subscriptions
4. profiles - Matrimonial profiles

### Feature Tables

5. events - Matrimonial events
6. profile_photos - User photos
7. media_gallery - Media uploads
8. messages - Private messaging
9. user_interests - Interest matching
10. event_registrations - Event signups

### Administrative Tables

11. payments - Payment records
12. notifications - System notifications
13. notification_preferences - User settings
14. audit_logs - Activity logs
15. user_reports - User reports
16. success_stories - Success stories

## Setup Instructions

### Prerequisites

- MySQL 8.0+ installed
- Database user with privileges

### Database Creation

1. Create database:

```sql
CREATE DATABASE matrimony_portal;
USE matrimony_portal;
```

2. Run schema:

```sql
SOURCE DatabaseDesign/schema.sql;
```

### Verification

```sql
SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'matrimony_portal';
```

## Sample Data

The schema includes sample data for testing:

- 5 users
- 3 subscription plans
- Sample profiles and events

## File Structure

```
DatabaseDesign/
├── README.md              # This file
├── database_design.md    # Design document
├── schema.sql            # Database schema
├── er_diagram.pdf        # ER diagram (PDF)
├── er_diagram.svg        # ER diagram (SVG)
└── er_diagram.mwb        # ER diagram (MySQL Workbench)
```
