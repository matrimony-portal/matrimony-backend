# Event Organizer API Documentation

## Overview

This document describes the RESTful APIs for Event Organizer operations in the Matrimony Portal. All endpoints are prefixed with `/api/organizers/{organizerId}`.

**Base URL**: `http://localhost:8080` (development)

**Authentication**: All endpoints require EVENT_ORGANIZER or ADMIN role (implement authentication as needed)

---

## Table of Contents

1. [Event Management APIs](#event-management-apis)
2. [Registration Management APIs](#registration-management-apis)
3. [Statistics & Analytics APIs](#statistics--analytics-apis)
4. [Request/Response Formats](#requestresponse-formats)
5. [Error Handling](#error-handling)

---

## Event Management APIs

### 1. Create Event

Create a new event for the organizer.

**Endpoint**: `POST /api/organizers/{organizerId}/events`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID

**Request Body**:
```json
{
  "title": "Speed Dating Event - Mumbai",
  "description": "Join us for an exciting speed dating event in Mumbai.",
  "eventDate": "2025-02-15T18:00:00",
  "venue": "Grand Hotel, Bandra",
  "city": "Mumbai",
  "state": "Maharashtra",
  "maxParticipants": 50,
  "registrationFee": 500.00
}
```

**Response**: `201 Created`
```json
{
  "id": 1,
  "organizerId": 1,
  "organizerName": "John Doe",
  "title": "Speed Dating Event - Mumbai",
  "description": "Join us for an exciting speed dating event in Mumbai.",
  "eventDate": "2025-02-15T18:00:00",
  "venue": "Grand Hotel, Bandra",
  "city": "Mumbai",
  "state": "Maharashtra",
  "maxParticipants": 50,
  "currentParticipants": 0,
  "registrationFee": 500.00,
  "status": "UPCOMING",
  "createdAt": "2025-01-26T10:00:00",
  "updatedAt": "2025-01-26T10:00:00"
}
```

---

### 2. Get All Organizer Events

Retrieve all events organized by the specified organizer.

**Endpoint**: `GET /api/organizers/{organizerId}/events`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "organizerId": 1,
    "organizerName": "John Doe",
    "title": "Speed Dating Event - Mumbai",
    "description": "Join us for an exciting speed dating event in Mumbai.",
    "eventDate": "2025-02-15T18:00:00",
    "venue": "Grand Hotel, Bandra",
    "city": "Mumbai",
    "state": "Maharashtra",
    "maxParticipants": 50,
    "currentParticipants": 15,
    "registrationFee": 500.00,
    "status": "UPCOMING",
    "createdAt": "2025-01-26T10:00:00",
    "updatedAt": "2025-01-26T10:00:00"
  }
]
```

---

### 3. Get Event by ID

Retrieve a specific event by ID (must belong to the organizer).

**Endpoint**: `GET /api/organizers/{organizerId}/events/{eventId}`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID
- `eventId` (Long, required): Event ID

**Response**: `200 OK`
```json
{
  "id": 1,
  "organizerId": 1,
  "organizerName": "John Doe",
  "title": "Speed Dating Event - Mumbai",
  "description": "Join us for an exciting speed dating event in Mumbai.",
  "eventDate": "2025-02-15T18:00:00",
  "venue": "Grand Hotel, Bandra",
  "city": "Mumbai",
  "state": "Maharashtra",
  "maxParticipants": 50,
  "currentParticipants": 15,
  "registrationFee": 500.00,
  "status": "UPCOMING",
  "createdAt": "2025-01-26T10:00:00",
  "updatedAt": "2025-01-26T10:00:00"
}
```

**Error Response**: `403 Forbidden` - If event doesn't belong to the organizer

---

### 4. Update Event

Update event details (must belong to the organizer).

**Endpoint**: `PUT /api/organizers/{organizerId}/events/{eventId}`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID
- `eventId` (Long, required): Event ID

**Request Body**:
```json
{
  "title": "Updated Speed Dating Event - Mumbai",
  "description": "Updated description for the event",
  "eventDate": "2025-02-20T18:00:00",
  "venue": "Grand Hotel, Bandra",
  "city": "Mumbai",
  "state": "Maharashtra",
  "maxParticipants": 60,
  "registrationFee": 600.00
}
```

**Response**: `200 OK` (Same format as Get Event by ID)

---

### 5. Update Event Status

Update event status (must belong to the organizer).

**Endpoint**: `PUT /api/organizers/{organizerId}/events/{eventId}/status`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID
- `eventId` (Long, required): Event ID

**Query Parameters**:
- `status` (String, required): New status - `UPCOMING`, `ONGOING`, `COMPLETED`, `CANCELLED`

**Example**: `PUT /api/organizers/1/events/1/status?status=ONGOING`

**Response**: `200 OK` (Same format as Get Event by ID)

---

### 6. Delete Event

Delete an event (must belong to the organizer).

**Endpoint**: `DELETE /api/organizers/{organizerId}/events/{eventId}`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID
- `eventId` (Long, required): Event ID

**Response**: `204 No Content`

---

## Registration Management APIs

### 7. Get Event Registrations

Get all registrations for a specific event.

**Endpoint**: `GET /api/organizers/{organizerId}/events/{eventId}/registrations`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID
- `eventId` (Long, required): Event ID

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "userId": 10,
    "userName": "Jane Smith",
    "userEmail": "jane@example.com",
    "eventId": 1,
    "eventTitle": "Speed Dating Event - Mumbai",
    "registrationDate": "2025-01-20T12:00:00",
    "paymentStatus": "PAID",
    "attended": false,
    "notes": "Looking forward to the event"
  }
]
```

---

### 8. Get All Organizer Registrations

Get all registrations for all events organized by the organizer.

**Endpoint**: `GET /api/organizers/{organizerId}/registrations`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID

**Response**: `200 OK` (Same format as Get Event Registrations, but includes all events)

---

### 9. Update Registration Payment Status

Update payment status of a registration.

**Endpoint**: `PUT /api/organizers/{organizerId}/registrations/{registrationId}/payment-status`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID
- `registrationId` (Long, required): Registration ID

**Query Parameters**:
- `paymentStatus` (String, required): Payment status - `PENDING`, `PAID`, `REFUNDED`

**Example**: `PUT /api/organizers/1/registrations/1/payment-status?paymentStatus=PAID`

**Response**: `200 OK`
```json
{
  "id": 1,
  "userId": 10,
  "userName": "Jane Smith",
  "userEmail": "jane@example.com",
  "eventId": 1,
  "eventTitle": "Speed Dating Event - Mumbai",
  "registrationDate": "2025-01-20T12:00:00",
  "paymentStatus": "PAID",
  "attended": false,
  "notes": "Looking forward to the event"
}
```

---

### 10. Update Registration Attendance

Update attendance status of a registration.

**Endpoint**: `PUT /api/organizers/{organizerId}/registrations/{registrationId}/attendance`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID
- `registrationId` (Long, required): Registration ID

**Query Parameters**:
- `attended` (Boolean, required): Attendance status - `true` or `false`

**Example**: `PUT /api/organizers/1/registrations/1/attendance?attended=true`

**Response**: `200 OK` (Same format as Update Registration Payment Status)

---

## Statistics & Analytics APIs

### 11. Get Organizer Statistics

Get event statistics and analytics for the organizer dashboard.

**Endpoint**: `GET /api/organizers/{organizerId}/statistics`

**Path Parameters**:
- `organizerId` (Long, required): Organizer user ID

**Response**: `200 OK`
```json
{
  "totalEvents": 15,
  "activeEvents": 5,
  "completedEvents": 8,
  "cancelledEvents": 2,
  "totalRegistrations": 450,
  "pendingRegistrations": 120,
  "paidRegistrations": 300,
  "totalParticipants": 330
}
```

---

## Request/Response Formats

### EventRequest (for Create/Update Event)

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| title | String | Yes | Max 255 chars | Event title |
| description | String | No | Max 5000 chars | Event description |
| eventDate | LocalDateTime | Yes | Must be in future | Event date and time |
| venue | String | Yes | Max 255 chars | Event venue |
| city | String | Yes | Max 100 chars | Event city |
| state | String | Yes | Max 100 chars | Event state |
| maxParticipants | Integer | No | Positive or zero | Maximum participants |
| registrationFee | BigDecimal | No | Positive or zero | Registration fee |

### EventResponse

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Event ID |
| organizerId | Long | Organizer user ID |
| organizerName | String | Organizer name |
| title | String | Event title |
| description | String | Event description |
| eventDate | LocalDateTime | Event date and time |
| venue | String | Event venue |
| city | String | Event city |
| state | String | Event state |
| maxParticipants | Integer | Maximum participants |
| currentParticipants | Integer | Current registered participants |
| registrationFee | BigDecimal | Registration fee |
| status | String | Event status (UPCOMING, ONGOING, COMPLETED, CANCELLED) |
| createdAt | LocalDateTime | Creation timestamp |
| updatedAt | LocalDateTime | Last update timestamp |

### EventRegistrationResponse

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Registration ID |
| userId | Long | User ID |
| userName | String | User name |
| userEmail | String | User email |
| eventId | Long | Event ID |
| eventTitle | String | Event title |
| registrationDate | LocalDateTime | Registration timestamp |
| paymentStatus | String | Payment status (PENDING, PAID, REFUNDED) |
| attended | Boolean | Attendance status |
| notes | String | Additional notes |

### EventStatisticsResponse

| Field | Type | Description |
|-------|------|-------------|
| totalEvents | Long | Total number of events |
| activeEvents | Long | Number of active events |
| completedEvents | Long | Number of completed events |
| cancelledEvents | Long | Number of cancelled events |
| totalRegistrations | Long | Total registrations across all events |
| pendingRegistrations | Long | Registrations with pending payment |
| paidRegistrations | Long | Registrations with paid status |
| totalParticipants | Long | Total unique participants |

---

## Error Handling

### Common HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Request successful, no response body |
| 400 | Bad Request - Invalid request data |
| 403 | Forbidden - Access denied (event doesn't belong to organizer) |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error - Server error |

### Error Response Format

```json
{
  "timestamp": "2025-01-26T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/organizers/1/events"
}
```

---

## Postman Collection

A complete Postman collection is available at:
`Event_Organizer_API_Postman_Collection.json`

### Import Instructions

1. Open Postman
2. Click "Import" button
3. Select the `Event_Organizer_API_Postman_Collection.json` file
4. Update the `baseUrl` variable if needed (default: `http://localhost:8080`)
5. Update path variables (`organizerId`, `eventId`, etc.) as needed

### Environment Variables

Create a Postman environment with:
- `baseUrl`: `http://localhost:8080` (or your server URL)
- `organizerId`: Your organizer user ID (default: 1)

---

## Example Usage

### Complete Workflow

1. **Create an Event**
   ```bash
   POST /api/organizers/1/events
   ```

2. **Get All Events**
   ```bash
   GET /api/organizers/1/events
   ```

3. **Get Event Registrations**
   ```bash
   GET /api/organizers/1/events/1/registrations
   ```

4. **Update Payment Status**
   ```bash
   PUT /api/organizers/1/registrations/1/payment-status?paymentStatus=PAID
   ```

5. **Update Attendance**
   ```bash
   PUT /api/organizers/1/registrations/1/attendance?attended=true
   ```

6. **Get Statistics**
   ```bash
   GET /api/organizers/1/statistics
   ```

---

## Notes

- All date/time fields use ISO 8601 format: `yyyy-MM-ddTHH:mm:ss`
- All monetary values are in decimal format (e.g., `500.00`)
- Event status values: `UPCOMING`, `ONGOING`, `COMPLETED`, `CANCELLED`
- Payment status values: `PENDING`, `PAID`, `REFUNDED`
- All endpoints require proper authentication and authorization
- The organizer must own the event/resource to perform operations on it

---

## Support

For issues or questions, please refer to the main project documentation or contact the development team.
