# TECHFIX — Member 4 Complete Expanded Job & Work Guide

## Spare Parts + Payment + Reports

**Project:** TECHFIX — Android Mobile Application  
**Module:** Mobile Application Development (MAD)  
**Member:** Member 4  
**Main Responsibility:** Spare Parts + Payment + Reports  
**Platform:** Android  
**Backend:** Cloudflare Worker / REST API  
**Database:** Cloudflare D1  
**Team Size:** 4 members

---

# 1. MEMBER 4 — EXACT ROLE

Member 4 is responsible for the **Spare Parts, Payment, and Reports module** of the TECHFIX Android application.

The coursework states that TechFix must support management of **repair service appointments, device categories, repair prices, spare-parts availability, technicians, sample repaired-device images, and branch operations**. It also states that the system should support **repair handling and payment processing**. Customers should also be able to view their previous repair history.

Therefore, Member 4's work should turn the spare-parts, payment, and reporting requirements into complete, usable Android screens and workflows.

### Member 4 owns these major areas

1. **Spare Parts Management**
2. **Spare Parts Availability**
3. **Repair Parts Usage / Parts Allocation**
4. **Payment Processing UI and Payment Status**
5. **Payment History / Receipts**
6. **Management Reports**
7. **Report Filtering and Summary**
8. **Offline/local caching where appropriate**
9. **Integration with the backend created by Member 1**
10. **Testing and documentation for these features**

> Important: Member 4 should not duplicate the backend ownership of Member 1. Member 1 owns the common backend foundation, authentication, API infrastructure, and database/business rules. Member 4 consumes those APIs and builds the Android-side functionality for spare parts, payments, and reports.

---

# 2. COURSEWORK REQUIREMENTS THAT SUPPORT MEMBER 4

The official coursework describes TECHFIX as a repair-shop application where customers can book repairs and track them while management can manage:

- Repair appointments
- Device categories
- Repair prices
- Spare parts availability
- Technicians
- Payments
- Branch operations

The coursework also specifically says that repair requests should be assigned to a suitable branch where technicians and required spare parts are available, and that the system should support repair handling and payment processing.

The coursework further requires every team member to be responsible for at least one UI and encourages technologies such as:

- Locations / Map GPS
- Web Services & Remote Data
- Complex Data Models & Adaptors
- Camera & Image Integrations
- SQLite / Offline Application

For the 70%+ band, the marking scheme specifically rewards robust functionality, additional useful features, advanced Android technologies, clean reusable code, efficient data management, and strong error handling.

---

# 3. MEMBER 4 — BIG PICTURE

Member 4 can be understood as the person responsible for the **financial and inventory side of the repair operation**.

The complete concept is:

```text
                  TECHFIX
                     |
          +----------+----------+
          |                     |
     Spare Parts             Payments
          |                     |
   +------+-------+       +-----+------+
   |              |       |            |
Inventory      Usage    Pay Bill    History
   |              |       |            |
Availability   Repair   Receipt      Status
   |
Low Stock
   |
Reports
   |
+--+-----------------------------+
|                                |
Inventory Reports          Revenue Reports
Payment Reports            Repair/Parts Reports
Branch Reports             Summary Dashboard
```

---

# 4. MEMBER 4 MAIN UI SCREENS

Member 4 should have clearly identifiable Android UIs.

Recommended screens:

```text
Member 4
│
├── Spare Parts Dashboard
│
├── Spare Parts List
│
├── Spare Part Details
│
├── Add Spare Part
│
├── Edit Spare Part
│
├── Stock Adjustment
│
├── Low Stock / Availability
│
├── Repair Parts
│
├── Payment Screen
│
├── Payment Details
│
├── Payment History
│
├── Receipt
│
├── Reports Dashboard
│
├── Revenue Report
│
├── Payment Report
│
├── Spare Parts Report
│
└── Branch / Repair Summary Report
```

The exact final number of screens can be adjusted to the team's navigation design, but Member 4 must clearly demonstrate ownership of at least one UI and preferably several related UIs.

---

# 5. MODULE A — SPARE PARTS MANAGEMENT

## 5.1 Purpose

The spare-parts module manages the parts that TECHFIX uses when repairing laptops, desktops, mobile phones, and other supported devices.

Examples:

- Laptop LCD
- Laptop keyboard
- Laptop battery
- SSD
- RAM
- Mobile display
- Mobile battery
- Charging port
- Cooling fan
- Power adapter

The system should allow management to know:

- What parts exist
- Which branch has them
- How many are available
- Their price
- Whether stock is low
- Which repair uses which part

---

# 6. SPARE PART DATA MODEL

A recommended spare-part model is:

```kotlin
data class SparePart(
    val id: String,
    val partCode: String,
    val name: String,
    val description: String?,
    val category: String?,
    val compatibleDeviceType: String?,
    val unitPrice: Double,
    val quantity: Int,
    val minimumStock: Int,
    val branchId: String?,
    val supplier: String?,
    val imageUrl: String?,
    val status: String
)
```

Possible status values:

```text
IN_STOCK
LOW_STOCK
OUT_OF_STOCK
INACTIVE
```

Do not add fields to the production API unless the team agrees on the database/API design.

---

# 7. SPARE PARTS LIST UI

## Purpose

Display all available spare parts in a searchable and filterable list.

### Recommended card

Each spare-part card can show:

```text
-----------------------------------------
Laptop Display
Part Code: LCD-001

Compatible: Laptop
Price: LKR 25,000

Stock: 4
Branch: Colombo

[In Stock]
-----------------------------------------
```

### UI features

- Search
- Filter by branch
- Filter by category
- Filter by stock status
- Sort by price
- Sort by quantity
- Pull-to-refresh
- Loading indicator
- Empty state
- Error state

---

# 8. SEARCH AND FILTER

The search field should support:

```text
Part name
Part code
Category
Compatible device
```

Example:

```text
Search: "battery"
```

Results:

```text
Laptop Battery
Mobile Battery
Dell Battery
Lenovo Battery
```

### Stock filters

```text
All
In Stock
Low Stock
Out of Stock
```

This makes the module more useful than a simple CRUD screen.

---

# 9. SPARE PART DETAILS SCREEN

When the user selects a spare part, display:

```text
Spare Part Details

Name:
Laptop Display

Part Code:
LCD-001

Category:
Display

Compatible Device:
Laptop

Unit Price:
LKR 25,000

Available Quantity:
4

Minimum Stock:
2

Branch:
Colombo

Status:
IN STOCK
```

Optional:

- Image
- Description
- Supplier
- Last updated
- Stock movement history

---

# 10. ADD SPARE PART

If the management role is allowed to create parts, provide:

```text
Part Name
Part Code
Description
Category
Compatible Device
Unit Price
Initial Quantity
Minimum Stock
Branch
Image
```

### Validation

Do not allow:

```text
Empty part name
Empty part code
Negative price
Negative quantity
Negative minimum stock
Invalid branch
Duplicate part code
```

Example:

```kotlin
if (name.isBlank()) {
    showError("Part name is required")
}

if (unitPrice < 0) {
    showError("Price cannot be negative")
}

if (quantity < 0) {
    showError("Quantity cannot be negative")
}
```

---

# 11. EDIT SPARE PART

The edit screen should allow authorized management users to change information such as:

- Name
- Description
- Price
- Minimum stock
- Compatibility
- Branch
- Status

Do not silently modify historical payment records when a spare-part price changes.

Historical transactions should preserve the price that was actually charged.

---

# 12. STOCK ADJUSTMENT

Stock should not only be editable through a normal text field.

A better design is a **Stock Adjustment** operation.

Example:

```text
Current Stock: 10

Adjustment:
+5

Reason:
New shipment

New Stock:
15
```

Another example:

```text
Current Stock: 10

Adjustment:
-2

Reason:
Used for appointment TF-001

New Stock:
8
```

Possible reasons:

```text
PURCHASE
REPAIR_USAGE
DAMAGED
RETURNED
MANUAL_ADJUSTMENT
TRANSFER
```

This gives the application a more professional inventory workflow.

---

# 13. LOW-STOCK LOGIC

Define:

```text
if quantity == 0
    OUT_OF_STOCK

else if quantity <= minimumStock
    LOW_STOCK

else
    IN_STOCK
```

Example:

```text
Quantity = 0
→ OUT OF STOCK

Quantity = 2
Minimum = 3
→ LOW STOCK

Quantity = 8
Minimum = 3
→ IN STOCK
```

Low-stock parts should be clearly visible to management.

---

# 14. BRANCH-BASED SPARE PARTS

TECHFIX has two branches in the coursework:

```text
Colombo
Galle
```

Spare parts may therefore need branch-level availability.

Example:

```text
Laptop Display
----------------
Colombo: 4
Galle: 0
```

This becomes important because the coursework says repair requests should be assigned to a branch where the required technicians and spare parts are available.

Member 4 should therefore make branch stock information easy to understand.

---

# 15. REPAIR PARTS / PART USAGE

A repair may require one or more spare parts.

Example:

```text
Appointment:
TF-2026-001

Laptop Screen Replacement

Parts:
1 × Laptop LCD
1 × Display Cable
```

The application should be able to display:

```text
Required Parts

Laptop LCD
Qty: 1
Unit Price: LKR 25,000

Display Cable
Qty: 1
Unit Price: LKR 3,500
```

Total:

```text
Parts Total = LKR 28,500
```

---

# 16. IMPORTANT STOCK RULE

When a part is officially consumed by a repair, the backend should be responsible for enforcing the final stock update.

Android should never assume that a stock update succeeded just because the button was pressed.

Correct workflow:

```text
Android
   ↓
Request "Use Part"
   ↓
Worker API
   ↓
Validate appointment
   ↓
Validate part
   ↓
Check stock
   ↓
Transaction
   ↓
Decrease stock
   ↓
Create usage record
   ↓
Return success
   ↓
Android refreshes stock
```

If stock is insufficient:

```text
API
 ↓
409 Conflict / appropriate error
 ↓
Android
 ↓
"Insufficient stock"
```

The exact HTTP status should follow the team's backend API contract.

---

# 17. MODULE B — PAYMENT

## 17.1 Purpose

TECHFIX needs payment processing as part of the repair workflow.

Member 4 owns the Android payment UI, payment status display, payment history, and receipt/report presentation.

Member 1 should provide or integrate the backend payment endpoints and database logic.

---

# 18. PAYMENT WORKFLOW

Recommended workflow:

```text
Repair Completed
      ↓
Final Price Calculated
      ↓
Payment Requested
      ↓
Customer Opens Payment
      ↓
Payment Method Selected
      ↓
Payment Processed
      ↓
Backend Verifies Result
      ↓
Payment Saved
      ↓
Appointment Payment Status Updated
      ↓
Receipt Generated/Displayed
```

---

# 19. PAYMENT SCREEN

Example:

```text
-----------------------------------------
PAYMENT

Appointment
TF-2026-001

Service
Laptop Screen Replacement

Service Charge
LKR 20,000

Spare Parts
LKR 28,500

Additional Charges
LKR 0

-----------------------------------------
TOTAL
LKR 48,500
-----------------------------------------

Payment Method

○ Cash
○ Card
○ Online Payment

[ PAY NOW ]
-----------------------------------------
```

The actual payment methods depend on the team's agreed implementation.

---

# 20. DO NOT FAKE PAYMENT SUCCESS

A very important implementation rule:

Do not simply do:

```kotlin
button.setOnClickListener {
    showSuccess("Payment Successful")
}
```

That is only a UI simulation.

For a proper implementation:

```text
Android
  ↓
POST /api/payments
  ↓
Backend
  ↓
Validate appointment + amount
  ↓
Process / record payment
  ↓
Return payment result
  ↓
Android displays result
```

If the team uses a sandbox/test payment gateway, use its test environment rather than real financial credentials.

---

# 21. PAYMENT METHODS

Possible implementation:

### Option A — Payment Gateway

Use a supported sandbox/test environment.

```text
Customer
 ↓
Payment UI
 ↓
Gateway Sandbox
 ↓
Payment Result
 ↓
Backend
 ↓
D1
```

### Option B — Demonstration Payment

If the coursework/team design does not require a real external gateway, implement a clearly labelled test/demo payment flow.

Example:

```text
Payment Method:
TEST CARD

Result:
SUCCESS
```

Never present a fake payment flow as a real financial transaction.

---

# 22. PAYMENT MODEL

Recommended model:

```kotlin
data class Payment(
    val id: String,
    val appointmentId: String,
    val customerId: String?,
    val amount: Double,
    val paymentMethod: String,
    val status: String,
    val transactionReference: String?,
    val paidAt: String?,
    val createdAt: String
)
```

Possible statuses:

```text
PENDING
PAID
FAILED
CANCELLED
REFUNDED
```

The backend should be the source of truth for final payment status.

---

# 23. PAYMENT HISTORY

Customers should be able to see previous payments.

Example:

```text
Payment History

---------------------------------
TF-2026-001
Laptop Screen Replacement

LKR 48,500
PAID
23 Aug 2026
---------------------------------

TF-2026-002
Mobile Battery Replacement

LKR 8,500
PAID
28 Aug 2026
---------------------------------
```

Management reports can use the same payment data but with broader filtering.

---

# 24. PAYMENT DETAILS

Payment details should show:

```text
Payment ID
Appointment Number
Customer
Branch
Service
Parts Cost
Service Cost
Total
Payment Method
Payment Status
Transaction Reference
Payment Date
```

Example:

```text
Payment Status:
PAID

Transaction:
TXN-20260823-001

Amount:
LKR 48,500
```

---

# 25. RECEIPT

Member 4 should provide a clean receipt view.

Example:

```text
              TECHFIX
        Repair Service Centre

Appointment: TF-2026-001
Date: 23 Aug 2026

Customer:
John Perera

Service:
Laptop Screen Replacement

Parts:
Laptop LCD       LKR 25,000
Display Cable    LKR 3,500

Service Charge   LKR 20,000
--------------------------------
TOTAL            LKR 48,500
--------------------------------

Payment:
PAID

Transaction:
TXN-20260823-001

Thank you for choosing TECHFIX.
```

Optional advanced feature:

```text
[ SAVE RECEIPT ]
[ SHARE RECEIPT ]
[ PRINT ]
```

If implemented, ensure Android storage/sharing follows modern Android permission and URI rules.

---

# 26. PAYMENT SECURITY

Never store:

```text
Full card number
CVV
Card PIN
Payment password
```

in the TECHFIX database.

The app should only retain safe transaction information required by the system, such as:

```text
Payment ID
Amount
Status
Method
Gateway/reference ID
Timestamp
Appointment ID
```

Sensitive payment credentials should remain with the payment provider.

---

# 27. MODULE C — REPORTS

## 27.1 Purpose

Reports give management a high-level understanding of:

- Revenue
- Payments
- Spare-parts usage
- Stock
- Repairs
- Branch performance

This is where Member 4 turns raw payment and inventory data into useful management information.

---

# 28. REPORT DASHBOARD

Recommended screen:

```text
REPORTS

---------------------------------
Total Revenue
LKR 850,000
---------------------------------

Paid Transactions
42
---------------------------------

Pending Payments
5
---------------------------------

Parts Used
87
---------------------------------

Low Stock Items
6
---------------------------------

Out of Stock
2
---------------------------------
```

Add:

```text
Date Range
Branch
Payment Status
```

---

# 29. REVENUE REPORT

Revenue report example:

```text
Revenue Report

Period:
01 Aug 2026 — 31 Aug 2026

Total Revenue:
LKR 850,000

Paid:
LKR 800,000

Pending:
LKR 50,000
```

Important:

**Total revenue should be calculated from successful/paid transactions according to the team's agreed backend definition.**

Do not count failed or cancelled payments as completed revenue.

---

# 30. PAYMENT REPORT

Filters:

```text
Date
Branch
Payment Method
Payment Status
```

Example:

```text
Payment Report

PAID
42 payments
LKR 800,000

PENDING
5 payments
LKR 50,000

FAILED
3 payments
LKR 20,000
```

---

# 31. SPARE PARTS REPORT

Show:

```text
Total Parts
Low Stock
Out of Stock
Total Stock Units
Estimated Stock Value
Most Used Parts
```

Example:

```text
Spare Parts Report

Total Part Types: 35
Low Stock: 6
Out of Stock: 2

Most Used:
1. Laptop LCD — 14
2. Mobile Battery — 11
3. Charging Port — 9
```

---

# 32. BRANCH REPORT

Because TECHFIX operates multiple branches, reports can be grouped by branch.

Example:

```text
Branch Performance

COLOMBO
Revenue: LKR 500,000
Repairs: 45
Parts Used: 52

GALLE
Revenue: LKR 350,000
Repairs: 31
Parts Used: 35
```

This should consume backend data rather than hard-coded values.

---

# 33. DATE FILTER

Recommended options:

```text
Today
This Week
This Month
Last Month
Custom Range
```

For custom range:

```text
From: 01/08/2026
To:   31/08/2026
```

Then request filtered data from the API.

---

# 34. REPORT VISUALIZATION

A good Android report screen can include charts.

Examples:

### Revenue over time

```text
Revenue
  |
  |             *
  |       *     | *
  |   *   |  *  | |
  |___|___|__|__|_|____ Date
```

### Payment status

```text
PAID
████████████████

PENDING
████

FAILED
██
```

### Parts usage

```text
LCD             ██████████████
Battery         ███████████
Charging Port   █████████
Keyboard        ██████
```

Charts are optional from the coursework wording, but they are a strong enhancement for the reports module if implemented cleanly.

---

# 35. REPORT API DESIGN

The exact endpoint names must match the backend implemented by Member 1.

A possible API structure is:

```text
GET /api/reports/summary

GET /api/reports/revenue

GET /api/reports/payments

GET /api/reports/spare-parts

GET /api/reports/branches
```

Example query:

```text
GET /api/reports/revenue?from=2026-08-01&to=2026-08-31
```

Example branch filter:

```text
GET /api/reports/revenue?branch_id=BR-001
```

These are **recommended endpoint designs**, not claims that they already exist in the current backend. Member 4 and Member 1 must agree on the actual API contract before implementation.

---

# 36. RETROFIT API SERVICE

Recommended Android structure:

```kotlin
interface ReportsApiService {

    @GET("api/reports/summary")
    suspend fun getSummary(): ReportSummaryResponse

    @GET("api/reports/revenue")
    suspend fun getRevenueReport(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("branch_id") branchId: String? = null
    ): RevenueReportResponse

    @GET("api/reports/payments")
    suspend fun getPaymentReport(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("status") status: String? = null
    ): PaymentReportResponse
}
```

The final paths and response models must match Member 1's actual Worker routes.

---

# 37. SPARE PART API SERVICE

Recommended structure:

```kotlin
interface SparePartsApiService {

    @GET("api/spare-parts")
    suspend fun getSpareParts(): SparePartsResponse

    @GET("api/spare-parts/{id}")
    suspend fun getSparePart(
        @Path("id") id: String
    ): SparePartResponse

    @POST("api/spare-parts")
    suspend fun createSparePart(
        @Body request: CreateSparePartRequest
    ): SparePartResponse

    @PUT("api/spare-parts/{id}")
    suspend fun updateSparePart(
        @Path("id") id: String,
        @Body request: UpdateSparePartRequest
    ): SparePartResponse
}
```

Again, these are recommended contracts and must be synchronized with the actual backend.

---

# 38. PAYMENT API SERVICE

Recommended structure:

```kotlin
interface PaymentApiService {

    @POST("api/payments")
    suspend fun createPayment(
        @Body request: CreatePaymentRequest
    ): PaymentResponse

    @GET("api/payments/{id}")
    suspend fun getPayment(
        @Path("id") id: String
    ): PaymentResponse

    @GET("api/payments")
    suspend fun getPayments(): PaymentsResponse
}
```

Potential request:

```kotlin
data class CreatePaymentRequest(
    val appointmentId: String,
    val amount: Double,
    val paymentMethod: String
)
```

Do not send sensitive card credentials to your own API unless the architecture explicitly requires it and has been designed securely.

---

# 39. AUTHORIZATION

Member 4 must respect the role system.

Typical separation:

```text
CUSTOMER
    ↓
View own payment history
Pay own repair
View own receipt

TECHNICIAN
    ↓
View repair-related parts
Record/confirm parts usage if permitted

MANAGER
    ↓
View reports
Manage stock
View branch payment information

ADMIN
    ↓
Full management/report access
```

The exact permission matrix must match the team's agreed backend authorization rules.

Android UI hiding is not security.

The backend must enforce authorization.

---

# 40. AUTH TOKEN

Protected API requests should include:

```http
Authorization: Bearer <TOKEN>
```

Member 4 should use the team's existing authentication/token mechanism rather than creating a separate login system.

Recommended architecture:

```text
Login
  ↓
Token
  ↓
Secure local storage
  ↓
Retrofit interceptor
  ↓
Authorization header
  ↓
Member 4 API requests
```

---

# 41. ROOM / SQLITE OFFLINE SUPPORT

The coursework specifically lists SQLite/offline application as a possible advanced Android technology.

Member 4 can use Room to cache:

```text
Spare parts
Payment history
Recent reports
Stock status
```

Example entity:

```kotlin
@Entity(tableName = "local_spare_parts")
data class SparePartEntity(
    @PrimaryKey val id: String,
    val partCode: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val branchId: String,
    val status: String
)
```

DAO:

```kotlin
@Dao
interface SparePartDao {

    @Query("SELECT * FROM local_spare_parts")
    fun observeAll(): Flow<List<SparePartEntity>>

    @Query(
        "SELECT * FROM local_spare_parts " +
        "WHERE name LIKE '%' || :query || '%'"
    )
    fun search(query: String): Flow<List<SparePartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parts: List<SparePartEntity>)

    @Query("DELETE FROM local_spare_parts")
    suspend fun clear()
}
```

---

# 42. OFFLINE STRATEGY

Do not pretend that a payment succeeded while offline.

Safe distinction:

### Read-only information

Can potentially be cached:

```text
Spare parts
Payment history
Previous reports
```

### Operations requiring server confirmation

Must normally require connectivity:

```text
Payment
Stock deduction
Stock transfer
Creating a financial transaction
```

Correct UX:

```text
No Internet
     ↓
User taps PAY
     ↓
"Internet connection is required to process payment."
```

---

# 43. REPOSITORY ARCHITECTURE

Recommended:

```text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
Remote API
 ↓
Cloudflare Worker
 ↓
D1
```

For cached data:

```text
UI
 ↓
ViewModel
 ↓
Repository
 ├── Remote API
 └── Room Database
```

Example:

```kotlin
class SparePartRepository(
    private val api: SparePartsApiService,
    private val dao: SparePartDao
) {

    suspend fun refresh() {
        val response = api.getSpareParts()

        if (response.success) {
            dao.insertAll(response.data.map { it.toEntity() })
        }
    }

    fun observeParts(): Flow<List<SparePartEntity>> {
        return dao.observeAll()
    }
}
```

---

# 44. VIEWMODEL

Example:

```kotlin
class SparePartsViewModel(
    private val repository: SparePartRepository
) : ViewModel() {

    val parts = repository.observeParts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun refresh() {
        viewModelScope.launch {
            try {
                repository.refresh()
            } catch (e: Exception) {
                // expose UI error state
            }
        }
    }
}
```

The real project should use proper `UiState` rather than silently swallowing errors.

---

# 45. UI STATE

Member 4 screens should clearly handle:

```text
Loading
Success
Empty
Error
Refreshing
```

Example:

```text
Loading:
[Progress Indicator]

Success:
[Parts List]

Empty:
"No spare parts found."

Error:
"Unable to load spare parts."
[Retry]

Offline:
"Showing saved data. Last updated 10:30 AM."
```

---

# 46. ERROR HANDLING

Handle:

```text
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
422 Validation Error
429 Rate Limit
500 Server Error
Network Timeout
No Internet
```

Example:

```kotlin
when (response.code()) {
    401 -> showError("Please log in again.")
    403 -> showError("You do not have permission.")
    404 -> showError("Record not found.")
    409 -> showError("Stock changed. Refresh and try again.")
}
```

Do not display raw server exceptions to users.

Bad:

```text
java.net.SocketTimeoutException...
```

Good:

```text
"Unable to connect to TECHFIX server. Please try again."
```

---

# 47. PACKAGE STRUCTURE

A clean structure could be:

```text
com.techfix
│
├── data
│   ├── remote
│   │   ├── api
│   │   │   ├── SparePartsApiService.kt
│   │   │   ├── PaymentApiService.kt
│   │   │   └── ReportsApiService.kt
│   │   │
│   │   ├── model
│   │   │   ├── SparePartDto.kt
│   │   │   ├── PaymentDto.kt
│   │   │   └── ReportDto.kt
│   │   │
│   │   └── RetrofitClient.kt
│   │
│   └── local
│       └── database
│           ├── SparePartEntity.kt
│           ├── PaymentEntity.kt
│           ├── SparePartDao.kt
│           └── AppDatabase.kt
│
├── domain
│   ├── model
│   └── repository
│       ├── SparePartRepository.kt
│       ├── PaymentRepository.kt
│       └── ReportRepository.kt
│
└── presentation
    ├── spareparts
    ├── payments
    └── reports
```

Keep the exact package naming consistent with the existing team project.

---

# 48. SPARE PARTS SCREEN NAVIGATION

Recommended flow:

```text
Management Dashboard
       ↓
Spare Parts
       ↓
Spare Parts List
       ↓
Select Part
       ↓
Part Details
       ↓
Edit / Stock Adjustment
```

Optional:

```text
Spare Parts
   ↓
Low Stock
```

---

# 49. PAYMENT SCREEN NAVIGATION

Customer flow:

```text
My Appointments
      ↓
Completed Repair
      ↓
Repair Details
      ↓
Payment
      ↓
Payment Result
      ↓
Receipt
```

Management flow:

```text
Management
   ↓
Payments
   ↓
Payment List
   ↓
Payment Details
```

---

# 50. REPORT NAVIGATION

```text
Management Dashboard
       ↓
Reports
       ↓
Reports Dashboard
       ├── Revenue
       ├── Payments
       ├── Spare Parts
       └── Branch Summary
```

---

# 51. DATA CALCULATION RULES

Where possible, calculations involving business truth should be performed on the backend.

Examples:

```text
Total Revenue
Paid payment sum

Active repairs
Appointment status-based count

Stock
Database inventory quantity

Parts used
Confirmed repair-part usage records
```

Android can calculate presentation values, but it should not become a second independent business-logic engine.

---

# 52. PAYMENT TOTAL CALCULATION

A repair may have:

```text
Service charge
+
Parts charge
+
Additional charge
-
Discount
=
Final total
```

Example:

```text
Service = 20,000
Parts = 28,500
Additional = 0
Discount = 1,500

Total = 47,000
```

The authoritative final amount should come from the backend.

Never trust a client-provided amount blindly for a real payment.

---

# 53. SPARE PART COST CALCULATION

For a repair:

```text
Part A:
Quantity = 2
Unit Price = 2,000

Part B:
Quantity = 1
Unit Price = 5,000

Parts Total:
(2 × 2,000) + (1 × 5,000)
= 9,000
```

Use backend-confirmed values for final billing.

---

# 54. REPORT PERFORMANCE

Reports can become expensive if the Android application downloads every payment and calculates everything locally.

Prefer:

```text
Android
 ↓
GET /reports/revenue
 ↓
Backend SQL aggregation
 ↓
Small JSON response
 ↓
Android chart
```

For example, backend SQL can aggregate:

```sql
SELECT
    SUM(amount) AS total_revenue,
    COUNT(*) AS payment_count
FROM payments
WHERE status = 'PAID';
```

The exact SQL depends on the team's actual schema.

---

# 55. SECURITY RULES

Member 4 must follow these rules:

### Never hard-code:

```text
JWT tokens
API secrets
Cloudflare API tokens
Payment gateway secret keys
```

### Never commit:

```text
.env
secret keys
private credentials
real payment information
```

### Never trust:

```text
client-side price
client-side stock quantity
client-side role
client-side payment status
```

The backend must validate all important business operations.

---

# 56. REPORT ACCESS CONTROL

A customer should not be able to request:

```text
GET /api/reports/all
```

and receive the entire company's revenue.

The backend must check:

```text
Who is requesting?
What is their role?
Which branch can they access?
Which records belong to them?
```

For customers:

```text
Own payments only
Own receipts only
Own repair information only
```

For management:

```text
Authorized business reports
```

---

# 57. TEST DATA

Member 4 should create realistic test data.

Example spare parts:

```text
SP-001 | Laptop LCD | 4
SP-002 | Laptop Battery | 8
SP-003 | 8GB DDR5 RAM | 15
SP-004 | Mobile Display | 2
SP-005 | USB-C Charging Port | 0
```

Example payments:

```text
PAY-001 | PAID | 25,000
PAY-002 | PAID | 48,500
PAY-003 | PENDING | 12,000
PAY-004 | FAILED | 8,000
```

Use test/demo information only.

---

# 58. TEST CASES — SPARE PARTS

## Test 1 — Load parts

Expected:

```text
Parts are displayed correctly.
```

## Test 2 — Search

Input:

```text
battery
```

Expected:

```text
Battery-related parts only.
```

## Test 3 — Low stock

Set:

```text
quantity = 2
minimum = 3
```

Expected:

```text
LOW STOCK
```

## Test 4 — Out of stock

Set:

```text
quantity = 0
```

Expected:

```text
OUT OF STOCK
```

## Test 5 — Negative quantity

Expected:

```text
Validation error.
```

## Test 6 — Unauthorized edit

Expected:

```text
403 / permission message.
```

---

# 59. TEST CASES — PAYMENT

## Test 1 — Valid payment

Expected:

```text
Payment succeeds.
Payment record created.
Status becomes PAID.
Receipt displayed.
```

## Test 2 — Failed payment

Expected:

```text
Status remains FAILED.
No false success message.
```

## Test 3 — Duplicate payment

Expected:

```text
Backend prevents unintended duplicate payment.
```

## Test 4 — Invalid amount

Expected:

```text
Payment rejected.
```

## Test 5 — No internet

Expected:

```text
User receives a clear connectivity message.
No false payment success.
```

---

# 60. TEST CASES — REPORTS

## Test 1

Date:

```text
01 Aug — 31 Aug
```

Expected:

```text
Only records inside range are included.
```

## Test 2

Filter:

```text
Colombo
```

Expected:

```text
Colombo data only.
```

## Test 3

Payment status:

```text
PAID
```

Expected:

```text
Only successful payments included.
```

## Test 4

No records

Expected:

```text
"No data available for the selected filters."
```

---

# 61. REFRESH BEHAVIOR

All Member 4 list/report screens should support refresh.

Example:

```text
User pulls screen down
        ↓
Show refresh indicator
        ↓
Call API
        ↓
Update local cache
        ↓
Update UI
        ↓
Hide indicator
```

If the refresh fails:

```text
Keep cached data
+
Show:
"Could not refresh. Showing saved data."
```

---

# 62. LOADING UX

Avoid freezing the UI.

Bad:

```text
Button clicked
↓
UI freezes for 5 seconds
```

Good:

```text
Button clicked
↓
Disable repeated submission
↓
Show progress
↓
Network request
↓
Success/Error
↓
Restore button
```

For payment, disable the PAY button while the transaction is being processed to reduce accidental double submissions.

---

# 63. DUPLICATE PAYMENT PROTECTION

A common problem:

```text
User taps PAY
User taps PAY again
```

Possible result:

```text
Two transactions
```

Prevent this at multiple levels:

### Android

```text
Disable button while request is running
```

### Backend

```text
Validate appointment/payment state
Use transaction/reference/idempotency protection where appropriate
```

The backend is the final protection.

---

# 64. GIT RESPONSIBILITY

Member 4 should work on their own branch.

Example:

```bash
git switch -c member04/spare-parts-payment-reports
```

Regular workflow:

```bash
git status
git add .
git commit -m "add spare parts management"
git push -u origin member04/spare-parts-payment-reports
```

Use focused commits.

Good:

```text
add spare parts list UI
add stock filtering
add payment screen
add payment history
add reports dashboard
fix payment error state
```

Avoid one giant commit:

```text
final everything
```

---

# 65. DO NOT COMMIT SECRETS

Before committing:

```bash
git status
```

Check for:

```text
.env
local.properties
API secrets
payment secrets
private certificates
```

If a secret is accidentally committed, remove it and rotate the secret immediately.

---

# 66. INTEGRATION WITH MEMBER 1

Member 1 provides:

```text
Authentication
API
Database
Authorization
Business rules
```

Member 4 provides:

```text
Spare Parts UI
Payment UI
Reports UI
Android data models
Retrofit integration
Room/cache integration
Charts/visualization
Testing
```

Integration flow:

```text
Member 4 Android
      ↓
Retrofit
      ↓
Member 1 Worker API
      ↓
D1
      ↓
JSON response
      ↓
Member 4 Repository
      ↓
ViewModel
      ↓
UI
```

---

# 67. INTEGRATION WITH MEMBER 2

Member 2 owns:

```text
Repair booking
Appointment viewing
Repair tracking
Repair history
```

Member 4 needs to integrate with appointment information.

Example:

```text
Member 2:
Appointment completed
        ↓
Member 4:
Payment becomes available
        ↓
Customer pays
        ↓
Receipt
```

Member 4 should not duplicate the appointment-tracking screen.

---

# 68. INTEGRATION WITH MEMBER 3

Member 3 owns:

```text
Branch management
Technician management
Technician assignment
Management dashboard
```

Member 4 provides:

```text
Spare-parts information
Payment information
Reports
```

Example:

```text
Member 3:
Branch → Colombo
        ↓
Member 4:
Colombo stock
Colombo revenue
Colombo payment report
```

---

# 69. COMPLETE TECHFIX REPAIR + PAYMENT FLOW

This is the flow Member 4 should understand:

```text
CUSTOMER
   ↓
Books repair
   ↓
MEMBER 2
   ↓
Appointment created
   ↓
MEMBER 3
   ↓
Technician assigned
   ↓
Technician diagnoses repair
   ↓
Required parts identified
   ↓
MEMBER 4
   ↓
Check spare-part availability
   ↓
Parts used
   ↓
Stock updated
   ↓
Repair completed
   ↓
Final amount calculated
   ↓
Customer payment
   ↓
Payment confirmed
   ↓
Receipt
   ↓
REPORTS
   ↓
Revenue + Parts + Payment statistics
```

This is one of the most important end-to-end workflows for Member 4.

---

# 70. EXAMPLE END-TO-END SCENARIO

## Scenario

Customer needs a laptop screen replacement.

### Step 1 — Booking

```text
Service:
Laptop Screen Replacement
```

### Step 2 — Assignment

Management assigns a technician.

### Step 3 — Diagnosis

Technician confirms:

```text
LCD damaged
```

### Step 4 — Spare part

System checks:

```text
Laptop LCD
Colombo stock = 4
```

### Step 5 — Part usage

Technician/authorized user records:

```text
1 × Laptop LCD
```

Stock becomes:

```text
4 → 3
```

### Step 6 — Final price

```text
Service: LKR 20,000
LCD:     LKR 25,000
--------------------
Total:   LKR 45,000
```

### Step 7 — Payment

Customer pays:

```text
LKR 45,000
```

### Step 8 — Receipt

System shows:

```text
PAID
Transaction reference
Appointment number
Amount
Date
```

### Step 9 — Report

Revenue report includes:

```text
+ LKR 45,000
```

Parts report includes:

```text
Laptop LCD usage +1
```

---

# 71. RECOMMENDED UI QUALITY

Member 4 should aim for:

- Consistent colors
- Consistent spacing
- Clear typography
- Good card layouts
- Proper icons
- Accessible buttons
- Clear status badges
- Empty states
- Loading states
- Error states
- Confirmation dialogs for destructive actions
- Pull-to-refresh
- Search/filter controls

Avoid:

```text
Huge blocks of text
Tiny buttons
Random colors
Unaligned cards
Hard-coded values
Fake statistics
```

---

# 72. IMPORTANT STATUS COLORS

The exact theme should match the team's design system.

Conceptually:

```text
PAID         → success
PENDING      → warning
FAILED       → error
LOW STOCK    → warning
OUT OF STOCK → error
IN STOCK     → success
```

Do not rely only on color. Also show text/icons so status remains understandable.

---

# 73. CONFIRMATION DIALOGS

For operations such as deleting/deactivating a spare part:

```text
Are you sure?

Deactivate "Laptop LCD"?

[Cancel] [Deactivate]
```

For stock adjustment:

```text
Confirm stock adjustment?

Laptop LCD
Current: 4
Adjustment: -1
New: 3

Reason:
Repair usage

[Cancel] [Confirm]
```

---

# 74. REPORT EXPORT — OPTIONAL ADVANCED FEATURE

If time permits, add:

```text
[Export Report]
```

Possible output:

```text
PDF
CSV
Share
```

This is an enhancement, not a requirement explicitly guaranteed by the coursework.

If implemented, make sure exported reports contain:

```text
Report title
Date range
Branch
Summary
Data
Generated date
```

---

# 75. CAMERA / IMAGE FOR SPARE PARTS — OPTIONAL

The coursework lists camera/image integration as an advanced Android technology.

If the team wants to use this for Member 4, a spare-part image feature could allow management to attach a photo.

Example:

```text
Add Spare Part
     ↓
[Take Photo]
     ↓
Camera
     ↓
Preview
     ↓
Upload
```

Do not add this just for decoration. It should have a useful purpose and be integrated correctly.

---

# 76. REPORT CHARTS — OPTIONAL ADVANCED FEATURE

A polished reports module could contain:

```text
Revenue Trend
Payment Status
Parts Usage
Branch Revenue
```

Example dashboard:

```text
--------------------------------
REVENUE
LKR 850,000
↑ 12%
--------------------------------

PAYMENTS
42 PAID
5 PENDING
3 FAILED
--------------------------------

LOW STOCK
6 ITEMS
--------------------------------
```

---

# 77. PERFORMANCE RULES

Avoid:

```text
Downloading thousands of records
then calculating everything on Android
```

Prefer:

```text
Backend SQL aggregation
       ↓
Small JSON response
       ↓
Android visualization
```

Use pagination for long lists if the API supports it.

---

# 78. ACCESSIBILITY

Member 4 should ensure:

- Buttons have readable labels
- Content descriptions exist for important icons
- Text is readable
- Touch targets are sufficiently large
- Status is not communicated by color alone
- Error messages are understandable

---

# 79. TESTING ON REAL DEVICES

Do not only test in Android Studio emulator.

Test:

```text
Wi-Fi
Mobile data
Slow network
No network
Screen rotation if supported
Different screen sizes
Real device
```

Payment and inventory behavior should especially be tested against the actual backend.

---

# 80. MEMBER 4 DEVELOPMENT PHASES

## Phase 1 — Understand project

Study:

```text
Coursework
Database schema
API documentation
Authentication
Existing Android architecture
Team navigation
```

Deliverable:

```text
Member 4 implementation plan
```

---

## Phase 2 — Spare Parts

Build:

```text
Spare parts list
Search
Filter
Details
Stock status
```

Deliverable:

```text
Working spare-parts UI
```

---

## Phase 3 — Inventory

Build:

```text
Add/edit
Stock adjustment
Low-stock
Out-of-stock
Branch stock
```

Deliverable:

```text
Working inventory workflow
```

---

## Phase 4 — Payment

Build:

```text
Payment screen
Payment result
Payment history
Payment details
Receipt
```

Deliverable:

```text
End-to-end payment workflow
```

---

## Phase 5 — Reports

Build:

```text
Reports dashboard
Revenue
Payments
Parts
Branch summaries
Filters
Charts if implemented
```

Deliverable:

```text
Working management reports
```

---

## Phase 6 — Offline

Add:

```text
Room cache
Offline read
Last updated indicator
```

Do not fake offline payment success.

---

## Phase 7 — Testing

Test:

```text
Success
Failure
Unauthorized
No internet
Empty data
Invalid input
Duplicate actions
```

---

## Phase 8 — Integration

Test with:

```text
Member 1 backend
Member 2 appointment flow
Member 3 management/branch flow
```

---

## Phase 9 — Polish

Fix:

```text
UI alignment
Loading
Errors
Empty states
Navigation
Performance
Crashes
```

---

# 81. DEFINITION OF DONE — SPARE PARTS

Member 4 can mark Spare Parts complete when:

- [ ] Spare parts list works
- [ ] Search works
- [ ] Filters work
- [ ] Details screen works
- [ ] Stock status is displayed
- [ ] Low stock is identified
- [ ] Out-of-stock is identified
- [ ] Authorized users can modify stock if required
- [ ] API integration works
- [ ] Errors are handled
- [ ] Loading state works
- [ ] Empty state works
- [ ] Branch stock is displayed where required
- [ ] No hard-coded production data remains

---

# 82. DEFINITION OF DONE — PAYMENT

- [ ] Payment screen works
- [ ] Correct final amount is displayed
- [ ] Payment method selection works
- [ ] Backend payment request works
- [ ] Success state works
- [ ] Failure state works
- [ ] Duplicate submission is prevented
- [ ] Payment history works
- [ ] Payment details work
- [ ] Receipt works
- [ ] Payment status is server-confirmed
- [ ] Sensitive payment credentials are not stored
- [ ] No fake success messages remain

---

# 83. DEFINITION OF DONE — REPORTS

- [ ] Reports dashboard works
- [ ] Revenue report works
- [ ] Payment report works
- [ ] Spare-parts report works
- [ ] Branch filtering works where required
- [ ] Date filtering works
- [ ] Empty results handled
- [ ] Loading state works
- [ ] Error state works
- [ ] Data comes from backend
- [ ] No fake statistics remain
- [ ] Charts work if included

---

# 84. DEFINITION OF DONE — OVERALL MEMBER 4

Member 4 is complete when:

```text
Spare Parts
     ✓
Payments
     ✓
Reports
     ✓
     |
     +---- Backend integration ✓
     |
     +---- Authentication ✓
     |
     +---- Error handling ✓
     |
     +---- Testing ✓
     |
     +---- Documentation ✓
```

---

# 85. DEMONSTRATION PLAN

The coursework requires a demonstration video of less than five minutes showing the major functions.

Member 4 should prepare a short, clean demonstration.

Recommended sequence:

### 1. Spare Parts

```text
Open Spare Parts
→ Search part
→ Open details
→ Show quantity
→ Show low-stock status
```

### 2. Stock Usage

```text
Open repair
→ Select/use required part
→ Confirm
→ Show updated stock
```

### 3. Payment

```text
Open completed repair
→ Open payment
→ Show total
→ Select test payment method
→ Complete payment
→ Show PAID
```

### 4. Receipt

```text
Open receipt
→ Show appointment
→ Show amount
→ Show transaction reference
```

### 5. Reports

```text
Open Reports
→ Show revenue
→ Show payment summary
→ Show stock summary
→ Filter by date/branch
→ Show chart
```

Keep the demonstration focused. Do not spend most of the video opening menus.

---

# 86. WHAT MEMBER 4 SHOULD SAY DURING THE VIVA

A simple explanation:

> "My main responsibility in the TECHFIX application is the spare-parts, payment, and reporting module. I developed the Android interfaces for managing spare-part availability, displaying stock information, handling repair-related parts, presenting payment workflows and receipts, and generating management reports. My module communicates with the backend through REST APIs. The backend remains responsible for authorization and business validation, while the Android application handles presentation, state management, caching, and user interaction."

---

# 87. POSSIBLE LECTURER QUESTION — WHY DID YOU USE RETROFIT?

Answer:

> "Retrofit provides a structured way to communicate with the REST API. It allows us to define API interfaces, request models, response models, authentication headers, and asynchronous network operations cleanly."

---

# 88. WHY USE ROOM?

Answer:

> "Room provides a structured SQLite abstraction for local persistence. We can cache spare-parts and payment-history data so the application can still display previously synchronized information when the network is unavailable. Financial operations such as payment are still server-dependent."

---

# 89. WHY SHOULD PAYMENT STATUS COME FROM THE BACKEND?

Answer:

> "Because the Android client cannot be trusted as the authority for a financial transaction. The backend validates the appointment, amount, authorization, and transaction result and then returns the authoritative payment status."

---

# 90. WHY IS STOCK VALIDATED ON THE SERVER?

Answer:

> "Because multiple users or technicians may change stock at the same time. If Android handled stock independently, two users could consume the same item. The backend must perform the final validation and update atomically."

---

# 91. WHY HAVE REPORTS ON THE BACKEND?

Answer:

> "Reports can involve aggregation across many records. Performing aggregation on the backend reduces the amount of data transferred to the Android device and keeps the business calculations consistent."

---

# 92. HOW DO YOU PREVENT A CUSTOMER FROM SEEING OTHER CUSTOMERS' PAYMENTS?

Answer:

> "The Android application does not provide security by itself. The backend verifies the authenticated user's identity and role and restricts payment queries to records that the user is authorized to access."

---

# 93. WHAT HAPPENS IF INTERNET IS LOST?

Answer:

> "Cached read-only information can still be displayed where appropriate. For operations that require server confirmation, especially payment and stock-changing operations, the application clearly informs the user that an internet connection is required instead of pretending that the operation succeeded."

---

# 94. HOW DOES MEMBER 4 WORK WITH MEMBER 1?

Answer:

> "Member 1 provides the shared REST API, authentication, database and backend business logic. I consume those APIs from Android using Retrofit and build the spare-parts, payment and reports features on top of that shared backend."

---

# 95. HOW DOES MEMBER 4 WORK WITH MEMBER 2?

Answer:

> "Member 2 handles the customer appointment and repair-tracking journey. Once an appointment reaches the appropriate repair/payment stage, my module provides the spare-parts and payment functionality without duplicating the appointment workflow."

---

# 96. HOW DOES MEMBER 4 WORK WITH MEMBER 3?

Answer:

> "Member 3 manages branches and technicians. My reports and inventory functions use branch information and provide management with spare-part availability, payment and financial summaries."

---

# 97. IMPORTANT TEAM BOUNDARY

Keep this separation clear:

```text
MEMBER 1
Backend + Authentication + Shared APIs

MEMBER 2
Customer Booking + Tracking + Repair History

MEMBER 3
Branch + Technician Management

MEMBER 4
Spare Parts + Payment + Reports
```

Avoid implementing the same screen twice.

If a screen crosses two modules, decide who owns the primary UI and define the integration point.

---

# 98. FINAL MEMBER 4 CHECKLIST

## Planning

- [ ] Understand coursework
- [ ] Understand team architecture
- [ ] Confirm API contracts with Member 1
- [ ] Confirm appointment flow with Member 2
- [ ] Confirm branch/technician flow with Member 3

## Spare Parts

- [ ] List
- [ ] Search
- [ ] Filter
- [ ] Details
- [ ] Add/edit if authorized
- [ ] Stock adjustment
- [ ] Low stock
- [ ] Out of stock
- [ ] Branch stock
- [ ] Repair usage

## Payment

- [ ] Payment screen
- [ ] Amount
- [ ] Payment method
- [ ] Payment request
- [ ] Success
- [ ] Failure
- [ ] History
- [ ] Details
- [ ] Receipt
- [ ] Duplicate protection

## Reports

- [ ] Dashboard
- [ ] Revenue
- [ ] Payments
- [ ] Parts
- [ ] Branch
- [ ] Date filters
- [ ] Charts if implemented
- [ ] Empty state

## Engineering

- [ ] Retrofit
- [ ] Repository
- [ ] ViewModel
- [ ] Room/cache if used
- [ ] Error handling
- [ ] Loading states
- [ ] Offline behavior
- [ ] Authentication
- [ ] Authorization
- [ ] No secrets in Git

## Testing

- [ ] Success cases
- [ ] Failure cases
- [ ] No internet
- [ ] Unauthorized
- [ ] Invalid data
- [ ] Duplicate payment
- [ ] Insufficient stock
- [ ] Empty reports
- [ ] Different branches
- [ ] Real device

## Submission

- [ ] Code committed
- [ ] Branch pushed
- [ ] README updated
- [ ] Screenshots captured
- [ ] Report contribution prepared
- [ ] Demo flow prepared
- [ ] Viva questions prepared

---

# 99. FINAL MEMBER 4 RESPONSIBILITY SUMMARY

Member 4 is responsible for making TECHFIX's **inventory, financial transaction, and management reporting side** useful and reliable.

The core flow is:

```text
SPARE PARTS
    ↓
Availability
    ↓
Repair Usage
    ↓
Stock Update
    ↓
FINAL REPAIR COST
    ↓
PAYMENT
    ↓
RECEIPT
    ↓
REPORTS
    ↓
Management Information
```

The strongest implementation is not simply three screens labelled "Spare Parts", "Payment", and "Reports."

The module should demonstrate a connected business workflow:

```text
A real spare part exists
        ↓
Its branch stock is known
        ↓
The part is required for a repair
        ↓
Stock is validated
        ↓
The part is consumed
        ↓
The final repair price is calculated
        ↓
The customer pays
        ↓
The payment is confirmed
        ↓
A receipt is available
        ↓
Revenue and inventory reports update
```

That end-to-end connection is what makes Member 4's work feel like part of a real repair-management system rather than a collection of unrelated UI screens.

---

# 100. FINAL TARGET

### Member 4 should be able to demonstrate:

```text
                    TECHFIX
                       |
          +------------+------------+
          |                         |
      INVENTORY                  FINANCE
          |                         |
    Spare Parts                  Payment
          |                         |
    Stock Levels                Receipt
    Low Stock                      |
    Branch Stock              Payment History
          |                         |
          +------------+------------+
                       |
                    REPORTS
                       |
              +--------+--------+
              |        |        |
           Revenue  Payments   Parts
              |
           Branch
           Summary
```

### Final goal:

> Build a reliable, integrated Android module that allows TECHFIX to manage spare-part availability, support repair-related inventory usage, process and display payment information, provide receipts and payment history, and present useful management reports while integrating cleanly with the other three members' modules.

**End of Member 4 Guide**
