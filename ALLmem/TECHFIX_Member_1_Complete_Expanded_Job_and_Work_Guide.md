# TECHFIX — MEMBER 1 COMPLETE JOB & WORK GUIDE

## Backend, REST API, Database, Authentication & Security

**Project:** TECHFIX Android Application  
**Member:** Member 1  
**Primary Domain:** Backend, Cloudflare Worker API, Cloudflare D1 Database, Authentication, Authorization, Validation, Business Logic, Deployment  
**Main Users Served:** Customer, Technician, Manager, Admin, and the Android application

---

# 1. PURPOSE OF THIS DOCUMENT

This is the complete working guide for **Member 1**.

It explains:

- Exactly what Member 1 is responsible for
- What Member 1 must build and maintain
- How the Cloudflare Worker backend connects to Android
- How the Cloudflare D1 database is structured
- How authentication and JWT/token handling should work
- How role-based authorization should work
- Which API endpoints are needed by the other members
- How appointment business logic works
- How technician assignment is protected
- How repair status changes are handled
- How validation and error handling should work
- How database integrity should be maintained
- How to test the backend
- How to deploy and troubleshoot the backend
- How Member 1 integrates with Member 2 and Member 3
- What Member 1 should demonstrate during the final presentation
- A detailed Definition of Done

---

# 2. PROJECT OVERVIEW

TECHFIX is a device repair/service management Android application connected to a backend API and database.

The overall system lifecycle is:

```text
CUSTOMER
   ↓
LOGIN / REGISTER
   ↓
DEVICES
   ↓
SERVICES
   ↓
CREATE REPAIR APPOINTMENT
   ↓
REQUESTED
   ↓
ADMIN / MANAGER
   ↓
ASSIGN TECHNICIAN
   ↓
ASSIGNED
   ↓
TECHNICIAN
   ↓
DIAGNOSING
   ↓
REPAIRING
   ↓
TESTING
   ↓
COMPLETED
   ↓
CUSTOMER
   ↓
REPAIR HISTORY
```

Member 1 is responsible for making the data and rules behind this workflow work correctly.

---

# 3. TEAM RESPONSIBILITY STRUCTURE

The team has three major responsibility areas.

| Member | Main Responsibility | Main User |
|---|---|---|
| Member 1 | Backend, API, Database, Authentication, Security | All roles |
| Member 2 | Customer Repair Journey | Customer |
| Member 3 | Admin, Branch & Technician Management | Admin / Manager |

## Member 1

Owns:

```text
Cloudflare Worker
      ↓
REST API
      ↓
Authentication
      ↓
Authorization
      ↓
Business Logic
      ↓
Cloudflare D1
      ↓
Database Integrity
```

## Member 2

Consumes Member 1's APIs for:

```text
Customer
   ↓
Devices
   ↓
Services
   ↓
Appointments
   ↓
Tracking
   ↓
Repair History
```

## Member 3

Consumes Member 1's APIs for:

```text
Admin / Manager
   ↓
Dashboard
   ↓
Branches
   ↓
Technicians
   ↓
Technician Skills
   ↓
Appointment Assignment
```

---

# 4. MEMBER 1 — MAIN JOB

## One-sentence responsibility

> **Member 1 is responsible for building and maintaining the secure backend system that stores TECHFIX data, exposes the REST APIs, authenticates users, enforces roles and business rules, and connects the Android application to the database.**

---

# 5. MEMBER 1 COMPLETE RESPONSIBILITY LIST

Member 1 is responsible for:

1. Cloudflare Worker setup
2. REST API routing
3. Cloudflare D1 database
4. Database schema
5. Database relationships
6. Database constraints
7. Authentication
8. Password handling
9. JWT/token handling
10. Role-based authorization
11. API validation
12. Appointment business logic
13. Technician assignment business rules
14. Appointment status lifecycle
15. Repair status history
16. Payment-related backend logic
17. Notification-related backend logic
18. Repair image-related backend support
19. Spare-part relationships
20. Technician-service relationships
21. Error handling
22. HTTP status handling
23. Database integrity considerations
24. API testing
25. Security testing
26. Deployment
27. Backend documentation
28. Android integration support
29. Production debugging
30. Final backend verification

---

# 6. WHAT MEMBER 1 DOES NOT OWN

Member 1 provides the backend required by the other modules, but should not duplicate their Android UI work.

## Member 1 does NOT primarily own:

### Customer UI

Member 2 owns:

```text
Customer Home
Book Repair
My Devices
My Appointments
Repair Tracking
Repair History
```

### Admin UI

Member 3 owns:

```text
Admin Dashboard
Branches
Technicians
Technician Details
Technician Services
Assignment UI
```

### Android presentation

Member 1 should provide:

```text
API
JSON
Authentication
Business rules
Database
```

Member 2 and Member 3 consume those APIs in Android.

---

# 7. TECHNOLOGY STACK

## Android / Client

```text
Android Studio
Kotlin
REST API
JSON
Retrofit / HTTP client
Room where required
```

## Backend

```text
Cloudflare Workers
JavaScript / TypeScript Worker code
REST API endpoints
Authentication middleware/functions
Wrangler CLI
```

## Database

```text
Cloudflare D1
SQLite-compatible relational database
SQL
Foreign keys
Constraints
Indexes where appropriate
```

## Repository

```text
NimnaOfficial/TECHFIX
```

---

# 8. CURRENT BACKEND ARCHITECTURE

```text
┌─────────────────────────────┐
│       TECHFIX ANDROID       │
│                             │
│ Customer / Admin / Manager  │
│ Technician                  │
└──────────────┬──────────────┘
               │
               │ HTTPS + JSON
               │
               ▼
┌─────────────────────────────┐
│     CLOUDFLARE WORKER       │
│                             │
│ Routing                     │
│ Authentication              │
│ Authorization               │
│ Validation                  │
│ Business Logic              │
│ Error Handling              │
└──────────────┬──────────────┘
               │
               │ SQL
               ▼
┌─────────────────────────────┐
│        CLOUDFLARE D1        │
│                             │
│ Users                       │
│ Devices                     │
│ Services                    │
│ Branches                    │
│ Technicians                 │
│ Appointments                │
│ Payments                    │
│ Notifications               │
│ Repair History               │
│ Spare Parts                 │
└─────────────────────────────┘
```

---

# 9. CURRENT BACKEND DEPLOYMENT

The deployed Worker currently used by the project is:

```text
https://techfix-api.codse251f-003.workers.dev/
```

The Worker is connected to the Cloudflare D1 database.

The database connection has previously been verified successfully.

---

# 10. DATABASE — COMPLETE RESPONSIBILITY

Member 1 owns the database structure and must understand how every important table connects.

Current application tables include:

```text
users
device_categories
devices
services
branches
technicians
technician_services
appointments
repair_status_history
repair_images
spare_parts
service_parts
branch_spare_parts
payments
notifications
```

Cloudflare may also expose internal tables such as:

```text
_cf_KV
```

Do not treat Cloudflare internal tables as normal application entities.

---

# 11. USERS TABLE

The users table stores all application users.

Important fields:

```text
id
first_name
last_name
email
phone
password_hash
role
profile_image_url
is_active
created_at
updated_at
```

The role is constrained to:

```text
CUSTOMER
TECHNICIAN
MANAGER
ADMIN
```

The email is unique.

Passwords are represented by a password hash, not plain text.

---

# 12. USER ROLES

The system contains four roles:

```text
CUSTOMER
TECHNICIAN
MANAGER
ADMIN
```

These roles must remain logically separate.

Do not automatically treat:

```text
ADMIN == MANAGER
```

unless the backend explicitly defines them as equivalent for a particular operation.

---

# 13. ROLE RESPONSIBILITIES

## CUSTOMER

Typical customer operations:

```text
Login
View own devices
Create own appointments
View own appointments
Track own repairs
View own repair history
```

Customer must NOT be allowed to:

```text
Assign technicians
Manage branches
Manage technicians
Change another customer's appointment
Access management-only data
```

## TECHNICIAN

Typical technician operations:

```text
View assigned repairs
Update permitted repair statuses
Add permitted repair information
```

Technician does not automatically receive Admin privileges.

## MANAGER

Management capabilities may include:

```text
View dashboard
View branches
View technicians
View appointments
Assign technicians
Manage operational resources
```

## ADMIN

Administrative capabilities may include:

```text
System administration
Management operations
Technician management
Branch management
```

The exact permissions must be enforced explicitly.

---

# 14. AUTHENTICATION

Authentication answers:

> "Who is this user?"

Typical flow:

```text
Android
   ↓
POST /api/login
   ↓
Worker
   ↓
Find user
   ↓
Verify password
   ↓
Create token
   ↓
Return token + user information
   ↓
Android stores token securely
```

---

# 15. LOGIN API

Known endpoint:

```http
POST /api/login
```

Example request:

```json
{
  "email": "user@example.com",
  "password": "USER_PASSWORD"
}
```

A successful response should provide the authenticated user's information and authentication token according to the implemented project contract.

Never return:

```text
password_hash
```

to Android.

---

# 16. PASSWORD SECURITY

Passwords must never be stored as plain text.

Bad:

```text
password = "mypassword123"
```

Good:

```text
password_hash = "<secure password hash>"
```

Backend process:

```text
Receive password over HTTPS
        ↓
Find user
        ↓
Verify password against hash
        ↓
Issue token
```

---

# 17. JWT / TOKEN RESPONSIBILITY

Protected requests use:

```http
Authorization: Bearer <TOKEN>
```

Example:

```http
GET /api/technicians
Authorization: Bearer eyJ...
```

Backend responsibilities:

1. Read Authorization header.
2. Extract Bearer token.
3. Validate token.
4. Identify user.
5. Determine role.
6. Check permissions.
7. Reject invalid requests.

---

# 18. AUTHENTICATION VS AUTHORIZATION

## Authentication

```text
Who are you?
```

Example:

```text
Token identifies the authenticated user.
```

## Authorization

```text
Are you allowed to do this?
```

Example:

```text
CUSTOMER attempts technician assignment
→ reject
```

Member 1 must implement both.

---

# 19. AUTHORIZATION MODEL

Conceptually:

```text
Endpoint
   ↓
Authentication required?
   ↓
Token valid?
   ↓
Role allowed?
   ↓
Resource ownership allowed?
   ↓
Business rule valid?
   ↓
Execute operation
```

For:

```text
PUT /api/appointments/{id}/assign
```

the backend should check:

```text
Valid token?
ADMIN or MANAGER?
Appointment exists?
Technician exists?
Technician eligible?
Assignment permitted?
```

---

# 20. CORE API ROUTES

Known/important routes include:

```http
POST /api/login

GET /api/appointments
GET /api/appointments/{id}
POST /api/appointments
GET /api/appointments/{id}/history

GET /api/admin/dashboard

GET /api/branches
GET /api/branches/{id}

GET /api/technicians
GET /api/technicians/{id}/services
PUT /api/technicians/{id}/services

PUT /api/appointments/{id}/assign
```

The exact deployed route must always match the actual Worker implementation.

---

# 21. API CONTRACT

For every endpoint document:

```text
HTTP Method
Endpoint
Purpose
Authentication
Allowed roles
Request body
Response body
Success status
Error statuses
Validation rules
```

Example:

```text
PUT /api/appointments/{id}/assign

Authentication:
Required

Roles:
ADMIN / MANAGER

Request:
{
  "technician_id": "TECH-001"
}
```

---

# 22. APPOINTMENT CREATION

Known endpoint:

```http
POST /api/appointments
```

Example:

```json
{
  "device_id": "DEVICE_ID",
  "service_id": "SVC-001",
  "branch_id": "BR-001",
  "requested_date": "2026-08-25",
  "requested_time": "10:30",
  "customer_latitude": 6.9271,
  "customer_longitude": 79.8612,
  "problem_description": "Laptop screen is cracked and showing display lines."
}
```

Server-owned information should be generated/derived by the backend:

```text
customer_id
appointment ID
appointment number
estimated price
created_at
updated_at
```

Do not blindly trust Android for these values.

---

# 23. APPOINTMENT CREATION FLOW

```text
POST /api/appointments
        ↓
Authenticate customer
        ↓
Validate request
        ↓
Validate device ownership
        ↓
Validate service
        ↓
Validate branch
        ↓
Validate date/time
        ↓
Calculate/obtain estimated price
        ↓
Create appointment
        ↓
Generate appointment number
        ↓
Set REQUESTED
        ↓
Create history record
        ↓
Return appointment
```

---

# 24. APPOINTMENT OWNERSHIP

A customer should only access their own appointments.

```text
Authenticated user
        ↓
user.id
        ↓
appointment.customer_id
        ↓
MATCH?
   ↙        ↘
 YES         NO
 ↓            ↓
ALLOW       DENY
```

This prevents cross-customer data access.

---

# 25. APPOINTMENT LIFECYCLE

The main lifecycle is:

```text
REQUESTED
    ↓
ASSIGNED
    ↓
DIAGNOSING
    ↓
REPAIRING
    ↓
TESTING
    ↓
COMPLETED
```

---

# 26. STATUS TRANSITION VALIDATION

Valid sequence:

```text
REQUESTED → ASSIGNED
ASSIGNED → DIAGNOSING
DIAGNOSING → REPAIRING
REPAIRING → TESTING
TESTING → COMPLETED
```

The backend should reject invalid transitions unless explicitly supported.

Example:

```text
COMPLETED → REQUESTED
```

should not be accepted by default.

---

# 27. REPAIR STATUS HISTORY

Every important status change should be recorded.

Conceptually:

```text
Appointment
   ↓
Status change
   ↓
repair_status_history
```

Example timeline:

```text
REQUESTED
Appointment created by customer

ASSIGNED
Technician assigned

DIAGNOSING
Diagnosis started

REPAIRING
Repair started

TESTING
Testing started

COMPLETED
Repair completed
```

---

# 28. HISTORY RECORD

A history record can contain:

```text
id
appointment_id
status
note
changed_by
created_at
```

The API may also return actor information:

```text
changed_by_first_name
changed_by_last_name
changed_by_role
```

---

# 29. TECHNICIAN ASSIGNMENT

Known endpoint:

```http
PUT /api/appointments/{id}/assign
```

Example:

```json
{
  "technician_id": "TECH-001"
}
```

This is a management operation.

Customer must not be allowed to call it successfully.

---

# 30. TECHNICIAN ASSIGNMENT VALIDATION

Before assignment:

```text
1. Request authenticated
2. Role authorized
3. Appointment exists
4. Technician exists
5. Technician is eligible
6. Availability permits assignment
7. Branch rules satisfied where applicable
8. Service compatibility satisfied where applicable
9. Appointment is assignable
10. Database update succeeds
11. Appointment becomes ASSIGNED
12. History entry created
```

---

# 31. TECHNICIAN AVAILABILITY

Known statuses:

```text
AVAILABLE
BUSY
OFF_DUTY
ON_LEAVE
```

Normally, only:

```text
AVAILABLE
```

technicians should be eligible for a new assignment where the business rules require availability.

Android may filter the list for convenience, but the backend must perform the final check.

---

# 32. TECHNICIAN SERVICE COMPATIBILITY

Technician-service relationships are stored through:

```text
technician_services
```

Conceptually:

```text
Technician
    │
    ├── Service A
    ├── Service B
    └── Service C
```

Example:

```text
Appointment:
Laptop Screen Replacement

Technician:
Mobile Phone Repair only

→ Not suitable
```

The exact compatibility rule must follow the implemented backend specification.

---

# 33. BRANCH RELATIONSHIP

Technicians belong to branches.

```text
Branch
   │
   ├── Technician 1
   ├── Technician 2
   └── Technician 3
```

Appointments also reference branches.

This enables branch-aware assignment.

---

# 34. DASHBOARD API

Known endpoint:

```http
GET /api/admin/dashboard
```

Dashboard values should come from real database data.

Typical metrics:

```text
Total Revenue
Pending Requests
Active Repairs
Available Technicians
```

---

# 35. ACTIVE REPAIRS

The project's active repair statuses are:

```text
DIAGNOSING
REPAIRING
TESTING
```

Therefore:

```text
Active Repairs =
DIAGNOSING + REPAIRING + TESTING appointments
```

---

# 36. PENDING REQUESTS

Pending requests normally include appointments requiring management action, especially:

```text
REQUESTED
```

The exact dashboard definition should follow the current API contract.

---

# 37. REVENUE

The defined dashboard revenue rule is:

```text
SUM(payment amount)
WHERE payment status = PAID
```

Do not count:

```text
PENDING
FAILED
CANCELLED
```

as completed revenue.

---

# 38. DATABASE RELATIONSHIPS

Important relationships:

```text
User
 ├── Devices
 ├── Appointments
 └── Notifications
```

```text
Device
 └── Device Category
```

```text
Appointment
 ├── Customer
 ├── Device
 ├── Service
 ├── Branch
 ├── Technician
 ├── Status History
 ├── Repair Images
 └── Payment
```

```text
Technician
 ├── Branch
 └── Services
```

```text
Service
 └── Device Category
```

```text
Spare Part
 ├── Service Parts
 └── Branch Spare Parts
```

---

# 39. DATABASE INTEGRITY

Use:

```text
PRIMARY KEY
UNIQUE
FOREIGN KEY
CHECK
NOT NULL
DEFAULT
TIMESTAMP
```

Known unique values include:

```text
users.email
appointments.appointment_number
technicians.employee_code
payments.transaction_reference
```

---

# 40. FOREIGN KEYS

Examples:

```text
appointments.customer_id → users.id
devices.category_id → device_categories.id
technicians.branch_id → branches.id
appointments.service_id → services.id
```

Foreign keys help prevent invalid relationships and orphaned data.

---

# 41. CHECK CONSTRAINTS

Protect enumerated values such as:

```text
user role
appointment status
payment method
payment status
technician availability
```

This gives the database another layer of validation.

---

# 42. BACKEND VALIDATION

Android validation is for user experience.

Backend validation is for security and correctness.

Never rely only on Android.

Example:

```text
Android says:
technician = TECH-001

Backend verifies:
TECH-001 exists
AVAILABLE
Correct branch where required
Correct service capability
Assignment permitted
```

---

# 43. INPUT VALIDATION

Validate:

```text
Email
Password
IDs
Dates
Times
Problem descriptions
Coordinates
Service IDs
Branch IDs
Technician IDs
Payment values
Status values
```

Examples:

```text
Negative price → reject
Invalid service ID → reject
Missing required field → reject
Invalid date → reject
```

---

# 44. ERROR HANDLING

Use predictable responses.

Example:

```json
{
  "success": false,
  "message": "Technician not found"
}
```

Useful HTTP statuses:

```text
200 OK
201 Created
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
500 Internal Server Error
```

---

# 45. 401 VS 403

## 401

Authentication failed or is missing.

Examples:

```text
Missing token
Invalid token
Expired token
```

## 403

User is authenticated but not permitted.

Example:

```text
CUSTOMER → technician assignment
```

---

# 46. 404 NOT FOUND

Use when a requested resource does not exist.

Example:

```text
GET /api/technicians/TECH-999
```

when that technician does not exist.

---

# 47. 409 CONFLICT

Useful for business conflicts such as:

```text
Duplicate email
Duplicate employee code
Busy technician assignment
Conflicting appointment
```

Use only when consistent with the project's API design.

---

# 48. 500 INTERNAL SERVER ERROR

Do not expose sensitive implementation details.

Bad:

```text
SQL query failed:
SELECT * FROM users WHERE ...
```

Better:

```json
{
  "success": false,
  "message": "Internal server error"
}
```

---

# 49. JSON RESPONSE DESIGN

Keep responses consistent.

Success:

```json
{
  "success": true,
  "data": {}
}
```

Error:

```json
{
  "success": false,
  "message": "Something went wrong"
}
```

Consistency makes Android integration much easier.

---

# 50. SQL SAFETY

Use parameterized/prepared queries.

Conceptually:

```text
SQL
+
bind(parameters)
```

Avoid concatenating user input into SQL.

Bad:

```text
"SELECT * FROM users WHERE email = '" + email + "'"
```

Better:

```text
Prepared statement
.bind(email)
```

---

# 51. SECRETS

Never commit:

```text
Cloudflare API tokens
JWT signing secrets
Passwords
Private keys
Payment secrets
Credentials
```

to GitHub.

Never put backend secrets inside Android source code.

Use environment/secret mechanisms appropriate to the deployment.

---

# 52. ROLE SPOOFING PROTECTION

Never trust a client-provided role.

Bad:

```json
{
  "role": "ADMIN"
}
```

Better:

```text
Token
 ↓
Authenticated user
 ↓
Database/server-side role
 ↓
Authorization
```

---

# 53. CUSTOMER ID SPOOFING PROTECTION

Do not trust:

```json
{
  "customer_id": "SOMEONE_ELSE"
}
```

for customer-owned operations.

Instead:

```text
Authenticated token
       ↓
Server identifies user
       ↓
Server sets/uses customer_id
```

---

# 54. PAYMENT SECURITY

The backend should not blindly trust:

```text
payment_status = PAID
```

sent by Android.

Payment state should be updated according to the project's actual payment confirmation process.

---

# 55. NOTIFICATION BACKEND

The database contains:

```text
notifications
```

The backend may support events such as:

```text
Appointment created
Technician assigned
Repair status changed
Repair completed
Payment updated
```

Android is responsible for displaying the notification UI.

---

# 56. REPAIR IMAGE BACKEND

The database contains:

```text
repair_images
```

Backend responsibilities may include:

```text
Associate image with appointment
Store image metadata/reference
Return authorized image information
```

Actual camera/gallery UI can remain in the appropriate Android module.

---

# 57. SPARE PARTS BACKEND

Tables include:

```text
spare_parts
service_parts
branch_spare_parts
```

Conceptually:

```text
SERVICE
   ↓
SERVICE_PARTS
   ↓
SPARE_PARTS
```

and:

```text
BRANCH
   ↓
BRANCH_SPARE_PARTS
   ↓
SPARE_PARTS
```

Member 1 maintains the data relationships and APIs required by the application.

---

# 58. API DEVELOPMENT WORKFLOW

For every new endpoint:

```text
1. Define purpose
2. Define HTTP method
3. Define endpoint
4. Define authentication
5. Define allowed roles
6. Define request
7. Define validation
8. Define SQL
9. Define response
10. Define errors
11. Test success
12. Test failure
13. Test security
14. Connect Android
15. Document endpoint
```

---

# 59. RECOMMENDED BACKEND STRUCTURE

A clean Worker can conceptually separate:

```text
src/
├── routes/
├── middleware/
├── auth/
├── services/
├── database/
├── validators/
├── utils/
└── index.ts
```

The exact structure can differ.

The important thing is separation of responsibilities.

---

# 60. DO NOT CREATE ONE GIANT BACKEND FILE

Avoid an architecture where everything lives inside one huge file:

```text
Routes
Auth
SQL
Validation
Business logic
Responses
Everything
```

As the project grows, separate logical responsibilities.

---

# 61. API DOCUMENTATION FORMAT

For every API document:

```text
Endpoint
Method
Purpose
Authentication
Allowed roles
Request
Response
Success status
Errors
Notes
```

Example:

```text
PUT /api/appointments/{id}/assign

Purpose:
Assign technician.

Authentication:
Required.

Roles:
ADMIN / MANAGER

Request:
{
  "technician_id": "TECH-001"
}

Success:
200

Possible errors:
401
403
404
409
500
```

---

# 62. ANDROID TOKEN INTEGRATION

Typical flow:

```text
Login
 ↓
Receive token
 ↓
Store securely
 ↓
Attach token to protected requests
```

Header:

```http
Authorization: Bearer <TOKEN>
```

A common Android HTTP interceptor can attach the token consistently.

---

# 63. INTEGRATION WITH MEMBER 2

Member 1 provides:

```text
Login
Devices
Services
Appointments
Appointment details
Repair history
```

Member 2 builds:

```text
Customer UI
```

Architecture:

```text
Member 1 API
      ↓
Retrofit
      ↓
Member 2 Repository
      ↓
ViewModel
      ↓
UI
```

---

# 64. INTEGRATION WITH MEMBER 3

Member 1 provides:

```text
Dashboard
Branches
Technicians
Technician services
Appointment management
Assignment
```

Member 3 builds:

```text
Admin/Manager UI
```

Architecture:

```text
Member 1 API
      ↓
Retrofit
      ↓
Member 3 Repository
      ↓
ViewModel
      ↓
Admin UI
```

---

# 65. API DEBUGGING WORKFLOW

When Android reports:

```text
Endpoint not found
```

check:

```text
1. Route path
2. HTTP method
3. Route ordering
4. Path parameter
5. Worker deployment
6. Authentication
7. Request body
```

---

# 66. ROUTE ORDER BUGS

Be careful with dynamic routes.

For example:

```text
/api/appointments/{id}
```

can accidentally intercept other paths depending on the router implementation.

Ensure specific routes are handled correctly before generic dynamic routes where necessary.

---

# 67. MISSING-BRACE / NESTED-ROUTE BUGS

One missing closing brace in Worker code can accidentally place one route inside another block.

Symptoms:

```text
Route appears in source
but returns Endpoint Not Found
```

Check:

```text
Braces
if/switch blocks
route nesting
deployment
```

---

# 68. DATABASE DEBUGGING

When an API says:

```text
Technician not found
```

check:

```text
1. Technician ID
2. Record exists in D1
3. Correct database
4. SQL query
5. Route parameter parsing
6. Identifier type
7. Authorization filters
```

Do not change the database before proving the database is the problem.

---

# 69. API TESTING WITH REAL DATA

Use real IDs for normal tests.

Examples:

```text
TECH-001
SVC-001
BR-001
```

Use fake IDs only when intentionally testing failure cases.

---

# 70. CURL / API CLIENT TESTING

Example:

```bash
curl https://techfix-api.codse251f-003.workers.dev/
```

Authenticated request:

```bash
curl   -H "Authorization: Bearer YOUR_TOKEN"   https://techfix-api.codse251f-003.workers.dev/api/technicians
```

POST example:

```bash
curl -X POST   -H "Content-Type: application/json"   -H "Authorization: Bearer YOUR_TOKEN"   -d '{"example":"value"}'   https://techfix-api.codse251f-003.workers.dev/api/example
```

Always use the exact API contract.

---

# 71. TESTING STRATEGY

Test four major areas:

```text
1. Normal functionality
2. Validation
3. Authorization/security
4. Database persistence
```

---

# 72. AUTHENTICATION TESTS

Test:

```text
Valid login
Invalid password
Unknown email
Missing email
Missing password
Inactive user
Invalid token
Missing token
Expired token
```

Expected:

```text
Valid → success
Invalid credentials → rejection
Invalid/missing token → 401
```

---

# 73. AUTHORIZATION TESTS

Test:

```text
Customer → customer endpoint
Customer → admin endpoint
Customer → technician assignment
Technician → admin endpoint
Manager → management endpoint
Admin → management endpoint
```

Expected:

```text
Allowed → success
Disallowed → 403
```

---

# 74. APPOINTMENT CREATION TESTS

Test:

```text
Valid appointment
Invalid device
Device owned by another customer
Invalid service
Invalid branch
Invalid date
Invalid time
Missing problem description
Invalid coordinates
```

---

# 75. APPOINTMENT ACCESS TESTS

Test:

```text
Customer A → Customer A appointment
Customer A → Customer B appointment
Admin → appointment
Manager → appointment
Technician → assigned appointment
Technician → unrelated appointment
```

Cross-customer access must be rejected.

---

# 76. TECHNICIAN ASSIGNMENT TESTS

Test:

```text
Valid technician
Nonexistent technician
Busy technician
Off-duty technician
On-leave technician
Wrong branch
Wrong service capability
Nonexistent appointment
Completed appointment
Customer attempts assignment
Technician attempts unauthorized assignment
```

---

# 77. STATUS CHANGE TESTS

Test:

```text
REQUESTED → ASSIGNED
ASSIGNED → DIAGNOSING
DIAGNOSING → REPAIRING
REPAIRING → TESTING
TESTING → COMPLETED
```

Then test invalid transitions.

Example:

```text
COMPLETED → REPAIRING
```

should normally fail.

---

# 78. HISTORY TESTS

After every status change verify:

```text
Appointment status updated
History row created
Correct appointment_id
Correct status
Correct changed_by
Correct timestamp
```

---

# 79. DATABASE TESTS

Verify:

```text
Foreign keys
Unique constraints
CHECK constraints
Required fields
Default values
No orphan records
No duplicate records
```

---

# 80. DASHBOARD TESTS

Verify dashboard values against database results.

Example:

```text
Active Repairs
=
DIAGNOSING + REPAIRING + TESTING
```

Revenue:

```text
PAID payments only
```

---

# 81. ERROR RESPONSE TESTS

Verify:

```text
400
401
403
404
409
500
```

Responses should remain:

```text
Consistent
Readable
Non-sensitive
```

---

# 82. REAL END-TO-END TEST

The strongest backend test is:

```text
1. Customer logs in
        ↓
2. Customer creates appointment
        ↓
3. Appointment = REQUESTED
        ↓
4. Admin/Manager logs in
        ↓
5. Admin retrieves technicians
        ↓
6. Admin assigns technician
        ↓
7. Appointment = ASSIGNED
        ↓
8. Technician updates status
        ↓
9. DIAGNOSING
        ↓
10. REPAIRING
        ↓
11. TESTING
        ↓
12. COMPLETED
        ↓
13. History contains all changes
        ↓
14. Customer retrieves appointment
        ↓
15. Customer sees updated status/history
```

---

# 83. CURRENT TECHFIX END-TO-END WORKFLOW

The project has previously demonstrated:

```text
Customer
   ↓
Authentication
   ↓
Device
   ↓
Service
   ↓
Branch
   ↓
Appointment
   ↓
Manager/Admin
   ↓
Technician Assignment
   ↓
Diagnosis
   ↓
Repair
   ↓
Testing
   ↓
Completed
```

This should remain the core integration path.

---

# 84. EXAMPLE TECHNICIAN ASSIGNMENT

Example appointment:

```text
TF-20260823085851-75BF50A8
```

Assignment endpoint:

```http
PUT /api/appointments/55f77027-e70e-4238-82d4-1ef9bb07b306/assign
```

Body:

```json
{
  "technician_id": "TECH-001"
}
```

Backend flow:

```text
Validate management authorization
        ↓
Find appointment
        ↓
Find technician
        ↓
Validate assignment
        ↓
Update appointment
        ↓
Create history
        ↓
Return success
```

---

# 85. EXAMPLE TECHNICIAN DATA

A known example:

```text
TECH-001
Employee Code: TF-T001
Name: Nimal Fernando
Specialization: Laptop Hardware
Branch: TechFix Colombo
```

Other technicians exist.

Do not hard-code these values into Android.

---

# 86. NO HARD-CODED BUSINESS DATA

Avoid:

```kotlin
val technicians = listOf(
    Technician("TECH-001", "Nimal Fernando")
)
```

for production functionality.

Use:

```text
Android
   ↓
GET /api/technicians
   ↓
Worker
   ↓
D1
   ↓
Real data
```

---

# 87. WRANGLER RESPONSIBILITY

Member 1 should understand Wrangler commands such as:

```bash
npx wrangler whoami
npx wrangler deploy
```

D1 operations may include:

```bash
npx wrangler d1 execute techfix-db
```

and, when authentication/permissions are correct:

```bash
npx wrangler d1 export techfix-db --remote --output=techfix-db.sql
```

Use commands appropriate to the installed Wrangler version and current Cloudflare configuration.

---

# 88. D1 DATABASE BINDING

Conceptually:

```text
Worker
   ↓
DB binding
   ↓
techfix-db
```

Worker code accesses the configured D1 binding through its environment.

For example:

```text
env.DB
```

Do not hard-code database credentials.

---

# 89. DATABASE MIGRATION WORKFLOW

For schema changes:

```text
1. Define change
2. Test change
3. Apply migration
4. Test existing APIs
5. Test Android integration
6. Document change
```

Avoid undocumented production schema changes.

---

# 90. BACKUP / EXPORT

Database exports are useful for:

```text
Backup
Testing
Migration
Submission
Debugging
Documentation
```

If export fails because of Cloudflare authentication, check API token permissions and account configuration.

Do not repeatedly change application code to solve an authentication problem with Wrangler.

---

# 91. DEPLOYMENT CHECKLIST

Before deployment:

```text
[ ] Worker builds
[ ] Routes correct
[ ] Secrets configured
[ ] D1 binding correct
[ ] Authentication works
[ ] Authorization works
[ ] SQL works
[ ] Errors handled
[ ] No secrets in Git
[ ] APIs tested
```

After deployment:

```text
[ ] Health endpoint
[ ] Login
[ ] Protected endpoint
[ ] Appointment
[ ] Technician
[ ] Assignment
[ ] Status update
[ ] History
```

---

# 92. PRODUCTION DATABASE SAFETY

Before important schema/data changes:

```text
1. Understand the change
2. Back up/export where appropriate
3. Test query
4. Apply change
5. Verify existing endpoints
```

Never run destructive SQL against production casually.

---

# 93. PERFORMANCE

Reasonable backend practices:

```text
Avoid unnecessary queries
Select only needed fields
Use indexes where appropriate
Avoid repeated database calls
Use prepared statements
Keep responses reasonably sized
```

---

# 94. POTENTIAL DATABASE INDEXES

Depending on actual query patterns, useful indexes may include:

```text
users.email
appointments.customer_id
appointments.status
appointments.branch_id
appointments.technician_id
repair_status_history.appointment_id
technicians.branch_id
technicians.availability_status
```

Indexes should be based on actual schema and workload.

---

# 95. OFFLINE / ROOM RELATIONSHIP

Android may cache:

```text
Branches
Technicians
Appointments
Other appropriate data
```

Member 1 remains responsible for the server-side source of truth.

Architecture:

```text
Cloudflare D1
      ↓
Worker API
      ↓
Android
      ↓
Room cache
```

---

# 96. API AS SOURCE OF TRUTH

Dynamic business information should come from the backend:

```text
Technician availability
Branch information
Appointment status
Assignment
Prices
Payment status
```

Android cache can become stale, so the app should refresh appropriately.

---

# 97. TEAM COMMUNICATION

Whenever Member 1 changes an API, tell the other members:

```text
Endpoint changed
Request changed
Response changed
Authentication changed
Status code changed
Database field changed
```

Example:

```text
Backend update:

PUT /api/appointments/{id}/assign

Request:
{
  "technician_id": "TECH-001"
}

Requires:
ADMIN or MANAGER token

Result:
Appointment becomes ASSIGNED.
```

---

# 98. API CHANGE MANAGEMENT

Avoid silently changing:

```text
Field names
Endpoint names
Request structure
Response structure
Status values
Authentication requirements
```

If a breaking change is necessary:

```text
1. Tell the team
2. Update API documentation
3. Update Android integration
4. Run end-to-end tests
```

---

# 99. GIT RESPONSIBILITY

Good backend commit messages:

```text
feat: add appointment creation endpoint
fix: validate technician assignment
feat: add appointment status history
fix: enforce customer appointment ownership
feat: add admin dashboard metrics
fix: reject invalid status transitions
```

Avoid:

```text
update
fix
changes
stuff
```

---

# 100. GIT SECURITY

Before committing, check for:

```text
.env
API tokens
Credentials
Private keys
JWT secrets
Database credentials
```

If a real secret is committed:

```text
Remove it
Rotate it
```

Deleting it from the latest file is not enough if it remains in Git history.

---

# 101. MEMBER 1 DAILY DEVELOPMENT WORKFLOW

A practical work session:

```text
1. Pull latest code
        ↓
2. Check current branch
        ↓
3. Check backend status
        ↓
4. Check database connection
        ↓
5. Pick one backend task
        ↓
6. Implement
        ↓
7. Test success
        ↓
8. Test failure
        ↓
9. Test authorization
        ↓
10. Test persistence
        ↓
11. Update documentation
        ↓
12. Commit
        ↓
13. Push
        ↓
14. Inform team
```

---

# 102. FINAL BACKEND INTEGRATION ARCHITECTURE

```text
                    MEMBER 1
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
     WORKER          REST API       SECURITY
                                  AUTH / JWT
        │
        ▼
  BUSINESS LOGIC
        │
        ▼
   CLOUDFLARE D1
        │
        ├── Users
        ├── Devices
        ├── Services
        ├── Branches
        ├── Technicians
        ├── Appointments
        ├── History
        ├── Payments
        ├── Notifications
        └── Spare Parts
```

---

# 103. MEMBER 1 DELIVERABLES

## Backend

```text
[ ] Cloudflare Worker
[ ] REST API
[ ] Correct routing
[ ] Error handling
[ ] Validation
```

## Database

```text
[ ] D1 database
[ ] Schema
[ ] Relationships
[ ] Constraints
[ ] Test/seed data
```

## Authentication

```text
[ ] Login
[ ] Password verification
[ ] Token generation
[ ] Token validation
[ ] Role handling
```

## Authorization

```text
[ ] CUSTOMER permissions
[ ] TECHNICIAN permissions
[ ] MANAGER permissions
[ ] ADMIN permissions
```

## Core repair workflow

```text
[ ] Appointment creation
[ ] Appointment retrieval
[ ] Technician assignment
[ ] Status updates
[ ] Status history
```

## Management support

```text
[ ] Dashboard API
[ ] Branch API
[ ] Technician API
[ ] Technician services API
```

## Extended backend

```text
[ ] Payments
[ ] Notifications
[ ] Repair images
[ ] Spare parts
```

where required by the final project scope.

---

# 104. DEFINITION OF DONE — AUTHENTICATION

```text
[ ] Login endpoint works
[ ] Passwords are not stored in plain text
[ ] Invalid credentials are rejected
[ ] Token is generated correctly
[ ] Token can be validated
[ ] Invalid/expired token is rejected
[ ] Password hash is never returned
```

---

# 105. DEFINITION OF DONE — AUTHORIZATION

```text
[ ] CUSTOMER role enforced
[ ] TECHNICIAN role enforced
[ ] MANAGER role enforced
[ ] ADMIN role enforced
[ ] Protected endpoints require authentication
[ ] Unauthorized roles receive 403
[ ] Customer ownership is checked
```

---

# 106. DEFINITION OF DONE — DATABASE

```text
[ ] Tables exist
[ ] Primary keys work
[ ] Foreign keys work
[ ] Unique constraints work
[ ] CHECK constraints work
[ ] Required fields work
[ ] Default values work
[ ] Relationships are correct
[ ] No unnecessary duplicate data
```

---

# 107. DEFINITION OF DONE — APPOINTMENTS

```text
[ ] Customer can create appointment
[ ] Appointment starts REQUESTED
[ ] Appointment number is generated
[ ] Device is validated
[ ] Service is validated
[ ] Branch is validated
[ ] Customer ownership enforced
[ ] Appointment can be retrieved
[ ] Unauthorized access rejected
```

---

# 108. DEFINITION OF DONE — ASSIGNMENT

```text
[ ] Admin/Manager can assign
[ ] Customer cannot assign
[ ] Technician must exist
[ ] Appointment must exist
[ ] Availability checked
[ ] Assignment rules checked
[ ] Appointment becomes ASSIGNED
[ ] History is recorded
```

---

# 109. DEFINITION OF DONE — REPAIR LIFECYCLE

```text
[ ] REQUESTED
[ ] ASSIGNED
[ ] DIAGNOSING
[ ] REPAIRING
[ ] TESTING
[ ] COMPLETED
```

Also:

```text
[ ] Invalid transitions rejected
[ ] Important transitions recorded
[ ] Actor recorded
[ ] Timestamp recorded
```

---

# 110. DEFINITION OF DONE — API QUALITY

```text
[ ] Correct HTTP methods
[ ] Correct HTTP status codes
[ ] Consistent JSON
[ ] Clear errors
[ ] No sensitive information in errors
[ ] Input validation
[ ] Authentication
[ ] Authorization
```

---

# 111. DEFINITION OF DONE — SECURITY

```text
[ ] No plain-text passwords
[ ] No secrets in Git
[ ] SQL injection protection
[ ] Token validation
[ ] Role validation
[ ] Ownership validation
[ ] Payment validation
[ ] Status validation
[ ] Assignment validation
```

---

# 112. DEFINITION OF DONE — DEPLOYMENT

```text
[ ] Worker deployed
[ ] D1 connected
[ ] Production API responds
[ ] Login tested
[ ] Protected endpoint tested
[ ] Appointment tested
[ ] Assignment tested
[ ] Status tested
[ ] History tested
```

---

# 113. FINAL END-TO-END TEST SCRIPT

## Step 1 — Customer Login

```text
POST /api/login
```

Verify:

```text
Token returned
Correct user returned
Role = CUSTOMER
```

## Step 2 — Create Appointment

```text
POST /api/appointments
```

Verify:

```text
success = true
status = REQUESTED
appointment number exists
```

## Step 3 — Read Appointment

```text
GET /api/appointments/{id}
```

Verify:

```text
Customer
Device
Service
Branch
Status
Price
```

## Step 4 — Read History

```text
GET /api/appointments/{id}/history
```

Verify:

```text
REQUESTED history entry
```

## Step 5 — Management Login

```text
POST /api/login
```

Verify:

```text
Role = MANAGER or ADMIN
```

## Step 6 — Retrieve Technicians

```text
GET /api/technicians
```

Verify:

```text
Technician list
Availability
Branch
Specialization
```

## Step 7 — Assign Technician

```text
PUT /api/appointments/{id}/assign
```

Body:

```json
{
  "technician_id": "TECH-001"
}
```

Verify:

```text
Appointment = ASSIGNED
```

## Step 8 — Check History

```text
GET /api/appointments/{id}/history
```

Verify:

```text
REQUESTED
ASSIGNED
```

## Step 9 — Repair Progress

Move through:

```text
DIAGNOSING
REPAIRING
TESTING
COMPLETED
```

## Step 10 — Verify Final History

Expected:

```text
REQUESTED
ASSIGNED
DIAGNOSING
REPAIRING
TESTING
COMPLETED
```

---

# 114. FINAL DEMONSTRATION FOR MEMBER 1

During the presentation, demonstrate:

## 1. Architecture

```text
Android
   ↓
Cloudflare Worker
   ↓
Cloudflare D1
```

## 2. Authentication

```text
Login
→ Password verification
→ Token
→ Authorization header
```

## 3. Authorization

Explain:

```text
CUSTOMER
TECHNICIAN
MANAGER
ADMIN
```

and why they are separated.

## 4. Database

Show:

```text
users
devices
services
branches
technicians
appointments
repair_status_history
payments
```

## 5. Business logic

Demonstrate:

```text
REQUESTED
→ ASSIGNED
→ DIAGNOSING
→ REPAIRING
→ TESTING
→ COMPLETED
```

## 6. Security

Explain:

```text
Password hashing
Token validation
Role authorization
Ownership checks
SQL parameterization
Input validation
```

---

# 115. STRONG LECTURER EXPLANATION

If asked:

> "What exactly did Member 1 do?"

Use:

> **"I developed and maintained the backend layer of TECHFIX using Cloudflare Workers and Cloudflare D1. I implemented the REST APIs, database structure and relationships, authentication and role-based authorization, validation, appointment business logic, technician assignment, repair status transitions, status history, and backend integration for the Android application. I also tested the APIs for successful, invalid and unauthorized operations and deployed the backend."**

---

# 116. IF ASKED WHY BACKEND SECURITY IS IMPORTANT

Answer:

> **"The Android application is a client and cannot be trusted as the final authority. Therefore authentication, authorization, ownership checks, validation, payment state, technician assignment and repair status rules are enforced on the backend."**

---

# 117. IF ASKED WHY USE D1

Answer:

> **"Cloudflare D1 provides a relational SQL database that fits the relationships between users, devices, services, branches, technicians, appointments and repair history, while allowing the Worker API to access the database through the Cloudflare environment."**

---

# 118. IF ASKED WHY USE TOKENS

Answer:

> **"After successful login, the backend issues an authentication token. Android sends that token with protected requests, allowing the backend to identify the user and enforce permissions without sending the password with every request."**

---

# 119. IF ASKED ABOUT CUSTOMER DATA SECURITY

Answer:

> **"The backend does not rely on an appointment ID alone. It authenticates the user and checks whether that user is allowed to access the requested appointment. This prevents one customer from accessing another customer's repair information."**

---

# 120. IF ASKED ABOUT TECHNICIAN ASSIGNMENT

Answer:

> **"The assignment endpoint is protected so only authorized management roles can perform it. The backend checks the appointment, technician, availability and applicable assignment rules before changing the appointment to ASSIGNED and recording the change in repair history."**

---

# 121. IF ASKED ABOUT STATUS HISTORY

Answer:

> **"The current appointment status tells us where the repair is now, while repair_status_history records how it got there. This allows customers and management users to see the complete repair timeline."**

---

# 122. IF ASKED WHAT HAPPENS IF ANDROID IS MODIFIED

Answer:

> **"The backend still validates the request. A modified Android client cannot simply change its role to ADMIN or mark a payment as PAID because the server is responsible for authentication, authorization and business rules."**

---

# 123. MEMBER 1 ↔ MEMBER 2 ↔ MEMBER 3

```text
                 MEMBER 1
          Backend / API / Database
                     │
          ┌──────────┴──────────┐
          │                     │
          ▼                     ▼
      MEMBER 2              MEMBER 3
      Customer              Admin /
      Journey               Manager
          │                     │
          ▼                     ▼
 Customer UI             Management UI
```

Member 1 provides the common backend foundation.

---

# 124. FINAL MEMBER 1 CHECKLIST

## Backend

```text
[ ] Worker deployed
[ ] API routes working
[ ] API responses consistent
[ ] Errors handled
[ ] Validation implemented
```

## Database

```text
[ ] D1 connected
[ ] Tables correct
[ ] Relationships correct
[ ] Constraints correct
[ ] Queries tested
```

## Authentication

```text
[ ] Login works
[ ] Password hashing works
[ ] Token works
[ ] Invalid token rejected
```

## Authorization

```text
[ ] Customer protected
[ ] Technician protected
[ ] Manager protected
[ ] Admin protected
[ ] Ownership enforced
```

## Appointments

```text
[ ] Create
[ ] Read
[ ] Assignment
[ ] Status updates
[ ] History
```

## Extended functionality

```text
[ ] Payments
[ ] Notifications
[ ] Repair images
[ ] Spare parts
```

## Security

```text
[ ] SQL injection protection
[ ] No secrets in Git
[ ] No password leaks
[ ] Input validation
[ ] Role validation
```

## Testing

```text
[ ] Success cases
[ ] Failure cases
[ ] Unauthorized cases
[ ] Database persistence
[ ] Android integration
```

## Documentation

```text
[ ] API documentation
[ ] Database documentation
[ ] Authentication documentation
[ ] Deployment documentation
[ ] Testing documentation
```

---

# 125. FINAL DEFINITION OF MEMBER 1

> **Member 1 owns the TECHFIX backend foundation. The job is not simply to create API endpoints. The job is to make the complete server-side system secure, reliable, validated, connected to the database, and usable by every other team member.**

Remember it as:

```text
DATABASE
   +
API
   +
AUTHENTICATION
   +
AUTHORIZATION
   +
VALIDATION
   +
BUSINESS LOGIC
   +
SECURITY
   +
TESTING
   +
DEPLOYMENT
   =
MEMBER 1
```

---

# 126. FINAL ONE-LINE SUMMARY

> **Member 1 builds the secure backend and database system that powers the entire TECHFIX application and provides reliable APIs for the Customer, Technician, Manager and Admin modules.**

---

# END OF MEMBER 1 GUIDE
