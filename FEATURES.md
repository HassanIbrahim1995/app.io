# Hotel Management System - Extended Features

## New Features Added

### 1. Housekeeping Task Management

Complete housekeeping module with task tracking and room status management.

#### Features:
- **Task Creation**: Create cleaning, inspection, maintenance, and turndown tasks
- **Task Assignment**: Assign tasks to housekeeping staff
- **Task Workflow**: 
  - PENDING → IN_PROGRESS → COMPLETED
  - Can be cancelled at any stage
- **Priority Levels**: LOW, MEDIUM, HIGH, URGENT
- **Room Status Integration**: Automatically updates room status based on task completion
- **Duration Tracking**: Track estimated vs actual task completion time
- **Notes & Completion Notes**: Add detailed notes during and after task execution

#### API Endpoints:
```
POST   /api/v1/housekeeping/tasks                    - Create task
GET    /api/v1/housekeeping/tasks                    - Get all tasks
GET    /api/v1/housekeeping/tasks/{id}               - Get task by ID
GET    /api/v1/housekeeping/tasks/status/{status}    - Get tasks by status
GET    /api/v1/housekeeping/tasks/room/{roomId}      - Get tasks by room
GET    /api/v1/housekeeping/tasks/assigned/{userId}  - Get user's tasks
GET    /api/v1/housekeeping/tasks/date/{date}        - Get tasks for date
POST   /api/v1/housekeeping/tasks/{id}/assign/{userId} - Assign task
POST   /api/v1/housekeeping/tasks/{id}/start         - Start task
POST   /api/v1/housekeeping/tasks/{id}/complete      - Complete task
POST   /api/v1/housekeeping/tasks/{id}/cancel        - Cancel task
DELETE /api/v1/housekeeping/tasks/{id}               - Delete task
```

#### Room Status Flow:
1. **Guest checks out** → Room status: CLEANING
2. **Cleaning task created** → Task status: PENDING
3. **Staff starts cleaning** → Task status: IN_PROGRESS
4. **Cleaning completed** → Task status: COMPLETED, Room status: AVAILABLE

### 2. Room Move Functionality

Move guests to different rooms during their stay with full history tracking.

#### Features:
- **Room Changes**: Move guests to different rooms
- **Price Adjustment**: Automatically calculate price differences
- **Charge Waiving**: Option to waive additional charges
- **Move History**: Complete audit trail of all room moves
- **Reason Tracking**: Record reasons for each move
- **Status Updates**: Automatically update room statuses

#### API Endpoints:
```
POST   /api/v1/room-moves                            - Move guest to new room
GET    /api/v1/room-moves/history/{reservationId}    - Get move history
```

#### Use Cases:
- Guest requests room upgrade
- Maintenance issue in current room
- Guest complaints (noise, view, etc.)
- Room type mismatch
- VIP upgrades

#### Example Request:
```json
{
  "reservationId": 123,
  "newRoomId": 456,
  "reason": "Guest requested quieter room",
  "waiveCharges": false
}
```

### 3. Enhanced Cancellation with Refund Policies

Intelligent cancellation system with automatic refund calculation.

#### Cancellation Policy:
- **7+ days before check-in**: 100% refund
- **3-7 days before check-in**: 50% refund
- **< 3 days before check-in**: No refund

#### Features:
- **Automatic Refund Calculation**: Based on timing of cancellation
- **Refund Tracking**: Track refund amount and processing status
- **Cancellation Reasons**: Record detailed cancellation reasons
- **Policy Display**: Show applicable policy to guests
- **Room Status Update**: Automatically free up cancelled rooms

#### Enhanced Reservation Fields:
```java
- cancellationPolicy: String
- refundAmount: BigDecimal
- refundProcessed: Boolean
- cancellationReason: String
- cancelledAt: LocalDateTime
- cancelledBy: Long
```

### 4. Group Reservations

Book multiple rooms for groups, events, conferences, and corporate bookings.

#### Features:
- **Multi-Room Booking**: Book multiple rooms in single transaction
- **Group Discounts**: Apply promotional codes to entire group
- **Individual Room Details**: Different guest details per room
- **Unified Management**: Manage all bookings under one group
- **Group Cancellation**: Cancel entire group with one action
- **Contact Management**: Central contact person for group

#### API Endpoints:
```
POST   /api/v1/group-reservations           - Create group booking
GET    /api/v1/group-reservations/{id}      - Get group details
GET    /api/v1/group-reservations           - List all groups
POST   /api/v1/group-reservations/{id}/cancel - Cancel group
```

#### Example Request:
```json
{
  "groupName": "Tech Conference 2024",
  "checkInDate": "2024-03-15",
  "checkOutDate": "2024-03-17",
  "rooms": [
    {
      "roomTypeId": 1,
      "numberOfGuests": 2,
      "numberOfAdults": 2,
      "numberOfChildren": 0,
      "guestName": "John Doe",
      "guestEmail": "john@example.com"
    },
    {
      "roomTypeId": 1,
      "numberOfGuests": 2,
      "numberOfAdults": 2,
      "numberOfChildren": 0,
      "guestName": "Jane Smith",
      "guestEmail": "jane@example.com"
    }
  ],
  "contactPersonName": "Event Coordinator",
  "contactEmail": "coordinator@techconf.com",
  "contactPhone": "+1-555-0123",
  "specialRequests": "Conference room needed",
  "promoCode": "CONFERENCE2024"
}
```

#### Response:
```json
{
  "groupReservation": {
    "id": 1,
    "groupNumber": "GRP-ABC12345",
    "groupName": "Tech Conference 2024",
    "status": "PENDING",
    "totalRooms": 2,
    "totalAmount": 1500.00,
    "paidAmount": 0.00,
    "discountAmount": 150.00
  },
  "reservations": [
    {
      "id": 101,
      "reservationNumber": "RES-XYZ789",
      "roomNumber": "201",
      "roomType": "Deluxe Double",
      "status": "PENDING"
    },
    {
      "id": 102,
      "reservationNumber": "RES-ABC456",
      "roomNumber": "202",
      "roomType": "Deluxe Double",
      "status": "PENDING"
    }
  ],
  "totalRooms": 2,
  "totalAmount": 1500.00,
  "message": "Group reservation created successfully"
}
```

## Database Schema Updates

### New Tables:

#### `group_reservations`
- Tracks group booking information
- Links multiple reservations together
- Stores contact and billing information

#### `room_move_history`
- Complete audit trail of room changes
- Tracks from/to rooms
- Records reason and charges

### Updated Tables:

#### `reservations`
- Added `group_reservation_id` (foreign key)
- Added `cancellation_policy`
- Added `refund_amount`
- Added `refund_processed`

#### `housekeeping_tasks`
- Enhanced with duration tracking
- Priority levels
- Completion notes

## Frontend Components

### New Pages:
- `HousekeepingPage` - Complete housekeeping dashboard
- Task list with filtering
- Create/edit task dialogs
- Real-time status updates

### New Services:
- `housekeepingService.ts` - API integration for tasks
- Task CRUD operations
- Status management

## Security & Permissions

### Role-Based Access:

**Housekeeping Endpoints:**
- `HOUSEKEEPING`, `MANAGER`, `ADMIN` - Can view and manage tasks
- `HOUSEKEEPING` - Can start/complete assigned tasks
- `MANAGER`, `ADMIN` - Can create, assign, and delete tasks

**Room Move Endpoints:**
- `FRONT_DESK`, `MANAGER`, `ADMIN` - Can move guests

**Group Reservations:**
- `FRONT_DESK`, `MANAGER`, `ADMIN` - Can create group bookings

## Business Logic

### Housekeeping Task Auto-Creation:
- Automatically create cleaning task when guest checks out
- Set priority based on next reservation timing
- Assign based on staff availability

### Room Status Management:
```
Check-out → CLEANING → Task Completed → AVAILABLE
Maintenance Issue → MAINTENANCE → Task Completed → AVAILABLE
```

### Price Adjustments for Room Moves:
1. Calculate remaining nights
2. Get price difference between rooms
3. Add/subtract from total amount
4. Record in move history

### Group Discount Calculation:
1. Calculate total for all rooms
2. Apply group discount if available
3. Distribute discount proportionally
4. Update individual reservation amounts

## API Documentation

All new endpoints are documented in Swagger UI:
- Visit: `http://localhost:8080/swagger-ui.html`
- Interactive testing available
- Request/response examples
- Authentication required for protected endpoints

## Testing

### Manual Testing:

#### Housekeeping:
```bash
# Create task
curl -X POST http://localhost:8080/api/v1/housekeeping/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "taskType": "CLEANING",
    "title": "Daily Cleaning",
    "priority": "MEDIUM",
    "scheduledDate": "2024-01-15T10:00:00"
  }'

# Start task
curl -X POST http://localhost:8080/api/v1/housekeeping/tasks/1/start \
  -H "Authorization: Bearer YOUR_TOKEN"

# Complete task
curl -X POST http://localhost:8080/api/v1/housekeeping/tasks/1/complete \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d "completionNotes=Room cleaned and inspected"
```

#### Room Move:
```bash
curl -X POST http://localhost:8080/api/v1/room-moves \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reservationId": 123,
    "newRoomId": 456,
    "reason": "Guest complaint",
    "waiveCharges": true
  }'
```

## Deployment Notes

- All new features work with existing Kubernetes setup
- No additional infrastructure required
- Database migrations will create new tables automatically
- Backward compatible with existing data

## Future Enhancements

1. **Mobile App for Housekeeping Staff**
   - Push notifications for new tasks
   - Quick task status updates
   - Photo upload for issues

2. **AI-Powered Task Scheduling**
   - Optimize staff assignments
   - Predict cleaning duration
   - Smart priority assignment

3. **Guest Self-Service Room Changes**
   - Allow guests to request upgrades
   - Show available upgrade options
   - Instant approval for loyalty members

4. **Advanced Group Management**
   - Room blocking for future events
   - Payment splitting options
   - Group check-in/out

5. **Housekeeping Analytics**
   - Staff performance metrics
   - Average cleaning times
   - Equipment usage tracking
   - Supply inventory management

---

For implementation details, see the source code in:
- Backend: `backend/src/main/java/com/hotel/management/`
- Frontend: `frontend/src/pages/admin/HousekeepingPage.tsx`
