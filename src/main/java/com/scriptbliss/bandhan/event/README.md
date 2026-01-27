# Event Management Domain

## Overview

The Event Management domain handles all matrimonial event operations including event creation, registration management, and organizer dashboard functionality.

## Domain Structure

```
event/
├── controller/          # REST endpoints
│   └── EventController.java
├── service/            # Business logic
│   ├── EventService.java
│   ├── EventServiceImpl.java
│   ├── EventRegistrationService.java
│   └── EventRegistrationServiceImpl.java
├── repository/         # Data access
│   ├── EventRepository.java
│   └── EventRegistrationRepository.java
├── entity/             # JPA entities
│   ├── Event.java
│   └── EventRegistration.java
└── dto/                # Request/Response DTOs
    ├── EventRequest.java
    ├── EventResponse.java
    ├── EventRegistrationResponse.java
    └── EventStatisticsResponse.java
```

## API Endpoints

All endpoints are prefixed with `/api/events`

### Event Management (Organizer)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/events` | Create new event | Yes (EVENT_ORGANIZER/ADMIN) |
| GET | `/api/events` | Get all events | No |
| GET | `/api/events/{id}` | Get event by ID | No |
| GET | `/api/events/my-events` | Get organizer's events | Yes |
| GET | `/api/events/organizer/{id}` | Get events by organizer | No |
| PUT | `/api/events/{id}` | Update event | Yes (Owner/ADMIN) |
| PUT | `/api/events/{id}/status` | Update event status | Yes (Owner/ADMIN) |
| DELETE | `/api/events/{id}` | Delete event | Yes (Owner/ADMIN) |

### Event Registration (Users)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/events/{id}/register` | Register for event | Yes |
| DELETE | `/api/events/{id}/register` | Unregister from event | Yes |
| GET | `/api/events/my-registrations` | Get user's registrations | Yes |

### Registration Management (Organizer)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/events/{id}/registrations` | Get event registrations | Yes (Owner/ADMIN) |
| GET | `/api/events/registrations/my-events` | Get all organizer registrations | Yes |
| PUT | `/api/events/registrations/{id}/payment-status` | Update payment status | Yes (Owner/ADMIN) |
| PUT | `/api/events/registrations/{id}/attendance` | Update attendance | Yes (Owner/ADMIN) |
| GET | `/api/events/statistics` | Get organizer statistics | Yes |

## Entities

### Event
- Represents a matrimonial event
- Status: UPCOMING, ONGOING, COMPLETED, CANCELLED
- Linked to organizer (User with EVENT_ORGANIZER role)

### EventRegistration
- Tracks user registrations for events
- Payment Status: PENDING, PAID, REFUNDED
- Tracks attendance

## Business Rules

1. **Event Creation**
   - Only EVENT_ORGANIZER or ADMIN can create events
   - Event date must be in the future
   - Registration fee defaults to 0 (free events)

2. **Event Registration**
   - Users can only register for UPCOMING events
   - Cannot register if event is full (maxParticipants reached)
   - Cannot register twice for same event
   - Registration creates PENDING payment status

3. **Authorization**
   - Organizers can only manage their own events
   - ADMIN can manage any event
   - Users can only view/manage their own registrations

## DTOs

### EventRequest
- Used for create/update operations
- Validated with Jakarta Validation
- Required fields: title, eventDate, venue, city, state

### EventResponse
- Includes organizer information
- Includes current participant count
- Includes all event details

### EventRegistrationResponse
- Includes user and event information
- Includes payment and attendance status

### EventStatisticsResponse
- Aggregated statistics for organizer dashboard
- Total events, registrations, participants

## Service Layer

### EventService
- Handles CRUD operations for events
- Manages authorization checks
- Maps entities to DTOs

### EventRegistrationService
- Manages user registrations
- Handles payment status updates
- Provides statistics for organizers
- Validates registration rules

## Repository Layer

### EventRepository
- Custom queries for events by organizer, status, city
- Upcoming events query

### EventRegistrationRepository
- Queries for registrations by user, event, organizer
- Count queries for statistics

## Testing

```bash
# Test event creation
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Event",
    "description": "Test Description",
    "eventDate": "2025-12-31T18:00:00",
    "venue": "Test Venue",
    "city": "Mumbai",
    "state": "Maharashtra",
    "maxParticipants": 100,
    "registrationFee": 500.00
  }' \
  -G --data-urlencode "organizerId=2"

# Get all events
curl http://localhost:8080/api/events

# Register for event
curl -X POST "http://localhost:8080/api/events/1/register?userId=3"
```

## Integration

### Frontend Integration
- Event service: `matrimony-frontend/src/services/eventService.js`
- Components:
  - `CreateEvent.jsx` - Event creation form
  - `OrganizerEvents.jsx` - Organizer dashboard
  - `Events.jsx` - Public event listing

### Database
- Tables: `events`, `event_registrations`
- Foreign keys to `users` table
- Indexes on organizer_id, event_date, city

## Future Enhancements

- [ ] Event image upload
- [ ] Event categories/tags
- [ ] Event search and filtering
- [ ] Email notifications for registrations
- [ ] Event waitlist functionality
- [ ] Event cancellation with refunds
- [ ] Event analytics and reporting

## Related Domains

- **auth**: User authentication and roles
- **payment**: Payment processing for registrations
- **notification**: Event-related notifications
