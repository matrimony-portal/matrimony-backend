# Event Management Domain

## Overview

The **event** package manages matrimonial events: creation, registration, organizer profiles, and statistics. It follows a layered structure (controller → service → repository → entity) so each class has a clear responsibility.

---

## How to Read This Module

| Layer | Folder | Purpose | Start With |
|-------|--------|---------|------------|
| **API** | `controller/` | REST endpoints; validates request, delegates to service | `EventController`, `EventOrganizerController` |
| **Business logic** | `service/` | Rules, authorization, entity↔DTO mapping | `EventServiceImpl`, `EventRegistrationServiceImpl` |
| **Data access** | `repository/` | JPA queries for `Event` and `EventRegistration` | `EventRepository`, `EventRegistrationRepository` |
| **Domain model** | `entity/` | JPA entities for `events` and `event_registrations` tables | `Event`, `EventRegistration` |
| **Contracts** | `dto/` | Request/response shapes; validation on request DTOs | `EventRequest`, `EventResponse` |

**Request flow:** `Controller` → `Service` → `Repository` → DB. **Response flow:** Entity → `mapToResponse()` in service → DTO → controller.

---

## Domain Structure

```
event/
├── controller/
│   ├── EventController.java         # /bandhan/events – CRUD, registrations, stats
│   └── EventOrganizerController.java # /bandhan/organizers – organizer-scoped APIs + profile
├── service/
│   ├── EventService.java
│   ├── EventServiceImpl.java        # Event CRUD, organizer profile, auth checks
│   ├── EventRegistrationService.java
│   └── EventRegistrationServiceImpl.java  # Register/unregister, payment, attendance, participant profile
├── repository/
│   ├── EventRepository.java
│   └── EventRegistrationRepository.java
├── entity/
│   ├── Event.java                    # events table; extends BaseEntity
│   └── EventRegistration.java     # event_registrations; user+event+payment+attendance
└── dto/
    ├── EventRequest.java           # Create/update payload (validated)
    ├── EventResponse.java          # Event in API responses
    ├── EventRegistrationResponse.java
    ├── EventStatisticsResponse.java
    ├── OrganizerProfileResponse.java    # Organizer user + profile + event stats
    ├── OrganizerProfileUpdateRequest.java
    └── ParticipantProfileResponse.java  # Registrant profile for organizer view
```

---

## API Endpoints

Base: `server.servlet.context-path=/bandhan`. Auth: JWT; roles `EVENT_ORGANIZER`, `ADMIN` where noted.

### EventController — `/bandhan/events`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/events` | Create event (`?organizerId=`) | EVENT_ORGANIZER / ADMIN |
| GET | `/events` | List all events | No |
| GET | `/events/{eventId}` | Get event by ID | No |
| GET | `/events/organizer/{organizerId}` | Events by organizer | No |
| GET | `/events/my-events?organizerId=` | Current organizer’s events | Yes |
| PUT | `/events/{eventId}` | Update event (`?organizerId=`) | Owner / ADMIN |
| PUT | `/events/{eventId}/status?status=&organizerId=` | Update status | Owner / ADMIN |
| DELETE | `/events/{eventId}?organizerId=` | Delete (soft: set CANCELLED, notify PAID) | Owner / ADMIN |
| GET | `/events/{eventId}/registrations?organizerId=` | List registrations | Owner / ADMIN |
| GET | `/events/registrations/my-events?organizerId=` | All registrations for organizer’s events | Owner / ADMIN |
| PUT | `/events/registrations/{registrationId}/payment-status?paymentStatus=&organizerId=` | PENDING/PAID/REFUNDED | Owner / ADMIN |
| PUT | `/events/registrations/{registrationId}/attendance?attended=&organizerId=` | Mark attended | Owner / ADMIN |
| GET | `/events/registrations/{registrationId}/participant-profile?organizerId=` | View registrant profile | Owner / ADMIN |
| GET | `/events/statistics?organizerId=` | Organizer stats | Owner / ADMIN |
| POST | `/events/{eventId}/register?userId=&notes=` | Register for event | Yes |
| DELETE | `/events/{eventId}/register?userId=` | Unregister | Yes |
| GET | `/events/my-registrations?userId=` | Current user’s registrations | Yes |

### EventOrganizerController — `/bandhan/organizers`

Organizer ID in path; avoids `?organizerId=` on each call.

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/organizers/{organizerId}/events` | Create event |
| GET | `/organizers/{organizerId}/events` | Organizer’s events |
| GET | `/organizers/{organizerId}/events/{eventId}` | Event by ID (must belong to organizer) |
| PUT | `/organizers/{organizerId}/events/{eventId}` | Update event |
| PUT | `/organizers/{organizerId}/events/{eventId}/status?status=` | Update status |
| DELETE | `/organizers/{organizerId}/events/{eventId}` | Delete event |
| GET | `/organizers/{organizerId}/events/{eventId}/registrations` | Event registrations |
| GET | `/organizers/{organizerId}/registrations` | All registrations for organizer’s events |
| PUT | `/organizers/{organizerId}/registrations/{registrationId}/payment-status?paymentStatus=` | Payment status |
| PUT | `/organizers/{organizerId}/registrations/{registrationId}/attendance?attended=` | Attendance |
| GET | `/organizers/{organizerId}/statistics` | Organizer statistics |
| GET | `/organizers/{organizerId}/profile` | Organizer profile (user + profile + stats) |
| PUT | `/organizers/{organizerId}/profile` | Update organizer profile |

---

## Entities

### Event (`events`)

- **Extends** `BaseEntity` (id, createdAt, updatedAt).
- **organizer** → `User` (EVENT_ORGANIZER/ADMIN).
- **eventType**: e.g. `SPEED_DATING`, `COFFEE_MEETUP`, `DINNER`, `CULTURAL`; default `SPEED_DATING`.
- **status**: `UPCOMING` | `ONGOING` | `COMPLETED` | `CANCELLED`.
- **imageUrl**: optional.

### EventRegistration (`event_registrations`)

- Does **not** extend `BaseEntity` (no `updated_at` in table).
- **user**, **event**; **registrationDate** (set on create); **paymentStatus** (`PENDING`|`PAID`|`REFUNDED`); **attended**; **notes**.

---

## Business Rules

1. **Event**
   - Only `EVENT_ORGANIZER` or `ADMIN` can create.
   - Event date should be in the future (`@Future` on `EventRequest.eventDate`).
   - `registrationFee` default 0; `maxParticipants` optional.

2. **Registration**
   - Only for `UPCOMING` events; no duplicate (user+event); must not exceed `maxParticipants`.
   - New registration → `paymentStatus = PENDING`. Organizer sets `PAID`/`REFUNDED`; on `PAID`, a notification is created.
   - `currentParticipants` in `EventResponse` = count of registrations with `PAID`.

3. **Authorization**
   - Organizer can manage only their events; `ADMIN` can manage any. Same for registrations and participant profile.

4. **Delete event**
   - Status set to `CANCELLED`; participants with `PAID` get an `EVENT_CANCELLED` notification.

---

## DTOs (Summary)

| DTO | Use |
|-----|-----|
| **EventRequest** | Create/update; validated (e.g. `@NotBlank` title, venue, city, state; `@Future` eventDate). |
| **EventResponse** | Event + organizerId, organizerName, currentParticipants, status. |
| **EventRegistrationResponse** | Registration + user and event summary, paymentStatus, attended, notes. |
| **EventStatisticsResponse** | totalEvents, active/completed/cancelled, total/pending/paid registrations, totalParticipants. |
| **OrganizerProfileResponse** | User + profile (from `profiles`) + total/upcoming/completed events. |
| **OrganizerProfileUpdateRequest** | firstName, lastName, phone, city, state, aboutMe. |
| **ParticipantProfileResponse** | Registrant’s user + profile for organizer’s view. |

---

## Database

- **Tables:** `events`, `event_registrations`.
- **FKs:** `events.organizer_id` → `users.id`; `event_registrations.user_id` → `users.id`, `event_registrations.event_id` → `events.id`.
- **Indexes:** e.g. `organizer_id`, `event_date`, `city` on `events`; `user_id`, `event_id` on `event_registrations`.

---

## Quick Test (with context-path and auth)

```bash
# List events (no auth)
curl http://localhost:8080/bandhan/events

# Create event (add -H "Authorization: Bearer <JWT>")
curl -X POST "http://localhost:8080/bandhan/events?organizerId=2" \
  -H "Content-Type: application/json" \
  -d '{"title":"Meet 2026","eventDate":"2026-03-15T18:00:00","venue":"Hall A","city":"Mumbai","state":"Maharashtra"}'

# Register (add auth)
curl -X POST "http://localhost:8080/bandhan/events/1/register?userId=3"

# Organizer profile
curl "http://localhost:8080/bandhan/organizers/2/profile"
```

---

## Related

- **auth**: `User`, roles, JWT.
- **profile**: `profiles` for organizer and participant profile data.
- **Shared**: `ResourceNotFoundException`, `UnauthorizedException`, `BaseEntity`, `ModelMapper`.
