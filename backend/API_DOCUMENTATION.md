# SafetyAR Central Safety REST API Documentation

**Version:** 1.0.0  
**Jurisdiction:** State Industrial Safety Council (JISC) & Directorate General of Mines Safety (DGMS), Jharkhand  
**Architecture:** Node.js + Express + Firebase Admin (with In-Memory Fallback)

---

## 🧭 Base URL & Health Check

- **Local Development:** `http://localhost:5000`
- **Android Emulator Loopback:** `http://10.0.2.2:5000`
- **Cloud Central:** `https://safetyar-dgms-jharkhand.gov.in`

### Health Check
```http
GET /api/health
```
**Response (200 OK):**
```json
{
  "status": "HEALTHY",
  "service": "SafetyAR Central REST API",
  "jurisdiction": "State Industrial Safety Council (JISC), Jharkhand",
  "mode": "IN_MEMORY_PRE_SEEDED",
  "uptimeSeconds": 142,
  "timestamp": 1757800000000
}
```

---

## 🔐 1. Authentication (`/api/v1/auth`)

### Worker / Supervisor Login
```http
POST /api/v1/auth/login
Content-Type: application/json
```
**Request Body:**
```json
{
  "workerId": "WRK-JH-COAL-0891",
  "passcode": "1234"
}
```
**Response (200 OK):**
```json
{
  "success": true,
  "message": "Worker authenticated successfully.",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "worker": {
      "workerId": "WRK-JH-COAL-0891",
      "fullName": "Ramesh Soren",
      "role": "WORKER",
      "designation": "Underground Drill & Strata Technician",
      "sector": "COAL_MINING",
      "enterprise": "Bharat Coking Coal Limited (BCCL)",
      "isCertified": true,
      "complianceStatus": "COMPLIANT"
    }
  }
}
```

---

## 👷 2. Worker Management (`/api/v1/workers`)

### List Workers
```http
GET /api/v1/workers?sector=COAL_MINING
Authorization: Bearer <token>
```
**Response (200 OK):**
```json
{
  "success": true,
  "count": 1,
  "data": [
    {
      "workerId": "WRK-JH-COAL-0891",
      "fullName": "Ramesh Soren",
      "sector": "COAL_MINING",
      "enterprise": "Bharat Coking Coal Limited (BCCL)",
      "isCertified": true,
      "complianceStatus": "COMPLIANT"
    }
  ]
}
```

### Get Worker Profile
```http
GET /api/v1/workers/:id
Authorization: Bearer <token>
```

### Register New Worker
```http
POST /api/v1/workers
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
  "workerId": "WRK-JH-COAL-0912",
  "fullName": "Babulal Hembrom",
  "designation": "Continuous Miner Operator",
  "sector": "COAL_MINING",
  "enterprise": "Bharat Coking Coal Limited (BCCL)",
  "division": "Putki Balihari Colliery"
}
```

---

## 📚 3. Training Assignments & Progress (`/api/v1/training`, `/api/v1/progress`)

### List Available Safety Modules
```http
GET /api/v1/training/modules
```

### Fetch Worker Assignments
```http
GET /api/v1/training/assignments/:workerId
```

### Assign Training to Worker
```http
POST /api/v1/training/assignments
Authorization: Bearer <token>
Content-Type: application/json
```
**Request Body:**
```json
{
  "workerId": "WRK-JH-COAL-0891",
  "moduleId": "MOD-GAS-01",
  "priority": "HIGH",
  "daysDue": 7
}
```

### Update Microlearning Progress
```http
POST /api/v1/progress/update
Content-Type: application/json
```
**Request Body:**
```json
{
  "workerId": "WRK-JH-COAL-0891",
  "moduleId": "MOD-FIRE-01",
  "currentStep": 8,
  "percentComplete": 72,
  "completed": false
}
```

---

## 🧠 4. Assessment Results (`/api/v1/assessments`)

### Submit Assessment Attempt
```http
POST /api/v1/assessments/submit
Content-Type: application/json
```
**Request Body:**
```json
{
  "workerId": "WRK-JH-COAL-0891",
  "moduleId": "MOD-FIRE-01",
  "scoreObtained": 94,
  "totalPossible": 100,
  "isPassed": true,
  "criticalMistakesCount": 0,
  "correctCount": 9,
  "incorrectCount": 1,
  "durationSeconds": 45
}
```
*Note: If `isPassed` is true and `criticalMistakesCount` is 0, the server automatically generates and issues an official digital certificate with a unique ID format `SAFETYAR-JH-2026-XXXXXX`.*

---

## 📜 5. Certificates & QR Verification (`/api/v1/certificates`)

### Unified QR & Certificate Verification (Section 13)
```http
POST /api/v1/certificates/verify
Content-Type: application/json
```
**Request Body:**
```json
{
  "query": "SAFETYAR-JH-2026-000001"
}
```
**Response: State 1 - VALID (200 OK):**
```json
{
  "success": true,
  "verificationStatus": "VALID",
  "certificateId": "SAFETYAR-JH-2026-000001",
  "message": "AUTHENTICATED: Valid DGMS-Compliant Safety Credential.",
  "complianceNote": "Cryptographic signature matched. Worker is certified and authorized for hazardous zone operations.",
  "data": {
    "certificateId": "SAFETYAR-JH-2026-000001",
    "workerId": "WRK-JH-COAL-0891",
    "workerName": "Ramesh Soren",
    "organization": "Bharat Coking Coal Limited (BCCL)",
    "trainingModule": "Fire & Explosion Emergency Response",
    "score": 94,
    "status": "VALID"
  }
}
```

**Response: State 2 - EXPIRED (200 OK):**
```json
{
  "success": true,
  "verificationStatus": "EXPIRED",
  "certificateId": "SAFETYAR-JH-2026-000002",
  "message": "RECERTIFICATION REQUIRED: Certificate expired on 09/08/2026.",
  "complianceNote": "Under Mines Vocational Training Rules 1966, annual refresher certification is mandatory before site entry. Access temporarily suspended."
}
```

**Response: State 3 - REVOKED (200 OK):**
```json
{
  "success": true,
  "verificationStatus": "REVOKED",
  "certificateId": "SAFETYAR-JH-2026-000003",
  "message": "ACCESS PROHIBITED: Safety Certificate Revoked by Inspectorate.",
  "complianceNote": "DGMS Notice #JH-2026-89: Life-safety respiratory compliance violation. Worker must surrender site badge immediately."
}
```

**Response: State 4 - NOT_FOUND (200 OK):**
```json
{
  "success": true,
  "verificationStatus": "NOT_FOUND",
  "certificateId": "SAFETYAR-JH-2026-999999",
  "message": "CREDENTIAL NOT FOUND IN CENTRAL DGMS REGISTRY.",
  "complianceNote": "The scanned certificate ID is not registered in the Jharkhand State Mining & Industrial Safety database. Potential fraudulent credential."
}
```

---

## 🔄 6. Offline Synchronization (`/api/v1/sync`)

### Upload Queued Offline Records
```http
POST /api/v1/sync/upload
Content-Type: application/json
```
**Request Body (`SyncPayloadDto`):**
```json
{
  "workerId": "WRK-JH-COAL-0891",
  "deviceId": "Android-Pixel-7",
  "timestamp": 1757800000000,
  "assessments": [ ... ],
  "certificates": [ ... ]
}
```

### Download Server Updates
```http
GET /api/v1/sync/download/:workerId
```

---

## 📊 7. Admin Dashboard & Analytics (`/api/v1/admin`, `/api/v1/analytics`)

### Get Summary KPI Metrics
```http
GET /api/v1/admin/summary
```

### Get Compliance Table
```http
GET /api/v1/admin/compliance
```

### Get Sector Safety Risk Matrix
```http
GET /api/v1/analytics/sector-matrix
```

### Get Critical Safety Violations Breakdown
```http
GET /api/v1/analytics/critical-errors
```
