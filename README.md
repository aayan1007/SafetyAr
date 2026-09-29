# SafetyAR: Industrial AR Vocational Safety Training, Assessment & Certification Platform
**Smart India Hackathon (SIH) 2026 | Problem Statement: Vocational Safety Training for Industrial Workers in Jharkhand**

[![Platform](https://img.shields.io/badge/Platform-Android%2010%2B%20(API%2029%2B)-green.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-100%25%20Java%2017%20(Zero%20Kotlin)-orange.svg)](https://www.oracle.com/java/)
[![AR](https://img.shields.io/badge/AR-Google%20ARCore%20%2B%20Interactive%20Canvas-blue.svg)](https://developers.google.com/ar)
[![Database](https://img.shields.io/badge/Database-AndroidX%20Room%20(15%20Tables)-purple.svg)](https://developer.android.com/training/data-storage/room)
[![Offline Sync](https://img.shields.io/badge/Sync-WorkManager%20Queue-teal.svg)](https://developer.android.com/topic/libraries/architecture/workmanager)
[![Compliance](https://img.shields.io/badge/Compliance-DGMS%20%26%20IS%20Standards-red.svg)](https://dgms.gov.in)

---

## 📌 Executive Summary

**SafetyAR** is a production-grade, native Android application engineered strictly in **100% Native Java 17** (Zero Kotlin) to solve the critical vocational safety gap among high-risk industrial workers in Jharkhand. It specifically addresses environments regulated by the **Directorate General of Mines Safety (DGMS)** and the **State Industrial Safety Council (JISC)**:
- **Underground Coal Mining**: Bharat Coking Coal Limited (BCCL), Dhanbad-Jharia Seam XVI
- **Heavy Steel & Metallurgy**: Tata Steel Limited, Jamshedpur Blast Furnace Division
- **Mica Processing & Quarrying**: Jharkhand State Mineral Development Corporation (JSMDC), Koderma

The platform operates **100% offline** underground and in zero-connectivity environments, using an on-device **Room Database (15 relational tables)**, **Google ARCore** with an automatic **Interactive Canvas Fallback**, a **Reusable Assessment Engine** enforcing a **Zero-Tolerance Life-Safety Error Failure Rule**, and cryptographic **SHA-256 QR-verified Digital Safety Passes**.

---

## 🛠 Technology Stack Compliance

| Layer | Technology | Specification / Version |
| :--- | :--- | :--- |
| **Language** | **100% Pure Java 17** | Strictly Java across all application code. Zero Kotlin. |
| **Android SDK** | Modern AndroidX | Minimum SDK: Android 10 (API 29), Target SDK: Android 14 (API 34) |
| **UI Framework** | Material Design 3 | High-contrast industrial palette (Slate `#0A0F1D`, Safety Amber `#F59E0B`) |
| **Localization** | Trilingual Resources | English, Hindi (हिंदी), Santali in Ol Chiki script (ᱥᱟᱱᱛᱟᱲᱤ) |
| **Augmented Reality**| Google ARCore + Fallback | `com.google.ar:core:1.41.0` + CameraX & 2D/3D Interactive Canvas Simulation |
| **Local Storage** | AndroidX Room DB | 15 Relational SQLite tables pre-seeded with DGMS regulations |
| **Background Sync** | AndroidX WorkManager | Guaranteed network-constrained synchronization queue |
| **Credentials & QR** | ZXing Embedded | Offline cryptographic SHA-256 QR code generation & scanning |
| **Networking** | Retrofit 2 + OkHttp 3 | REST API client connecting to Central Safety Server |
| **Backend Service** | Node.js + Express + Firebase | Dual-store: Firebase Firestore Admin + zero-dependency in-memory DB |
| **Web Admin Portal** | HTML5 / Vanilla JS SPA | 8 Dedicated views with 4-role switcher & live QR verification workbench |
| **Automated Testing**| JUnit 4 | Unit test suite verifying scoring, zero-tolerance rules, orientation & QR states |

---

## 🔄 Complete Product Journey (14 Core Flow Steps)

```
Splash Screen
  └── Language Selection (English / Hindi / Santali)
        └── 1-Tap Demo Worker Login (Ramesh Soren - WRK-JH-COAL-0891)
              └── Worker Credential Profile (BCCL Moonidih, Seam XVI)
                    └── Safety Dashboard (Orientation Day 12/30, 40% Complete)
                          └── Assigned Mandatory Training
                                └── 11-Step Microlearning Structure
                                      └── AR Practical Simulation (Fire PASS / Gas Confined Space)
                                            └── Competency Assessment (MCQ, Image, Order, Scenario)
                                                  └── Score Calculation & Zero-Tolerance Safety Evaluation
                                                        └── Pass / Fail & Weak Area Remediation
                                                              └── Digital Certificate Issuance (SAFETYAR-JH-2026-XXXXXX)
                                                                    └── QR Verification Workbench (VALID, EXPIRED, REVOKED, NOT FOUND)
                                                                          └── Training History & Offline Sync Status
```

---

## 🏭 Primary Demonstration Modules

### 1. AR Module 1: Fire & Explosion Emergency Response (`MOD-FIRE-01`)
- **DGMS Standard**: Coal Mines Rule 118 (Mine Fire & Explosion Isolation).
- **Virtual AR Elements**: Active methane flame, 3-meter stand-off perimeter, Dry Chemical Powder (DCP) extinguisher, emergency pull-alarm switch, illuminated green floor evacuation arrows, surface muster assembly point.
- **PASS Extinguisher Protocol**: Step-by-step sequential verification:
  1. **P**ull the safety pin.
  2. **A**im nozzle at the base of the fire.
  3. **S**queeze the operating lever.
  4. **S**weep nozzle from side to side.
- **Immediate Feedback**: Unsafe actions (e.g. attempting water suppression on energized coal seam equipment) trigger immediate score penalties and safety warning callouts.

### 2. AR Module 2: Gas Leak & Confined Space Safety (`MOD-GAS-01`)
- **DGMS Standard**: Confined Space Safety Standard S54:2024.
- **Virtual AR Elements**: Toxic gas plume source, expanding danger perimeter, SCBA positive-pressure station, 4-Gas HUD monitor (CH4, CO, H2S, O2), standby safety buddy, Form-IV Entry Permit barrier, emergency egress route.
- **Unsafe Action Penalty**: Attempting to enter the confined space without Form-IV supervisor authorization or with combustible gas >1.25% triggers device vibration, a large red alert banner:
  > *"UNSAFE ACTION! Do not enter without required authorization and atmospheric testing."*
  and immediate score deduction.
- **Retry Feature**: Instant scenario restart via `btnRestartGasScenario`.

### 3. Google ARCore + Graceful Interactive Fallback
- Checks device capability via `ArCoreApk.getInstance().checkAvailability(...)`.
- On unsupported hardware, displays a prominent non-intrusive warning:
  > *"AR is not supported on this device. You can continue using Interactive Safety Simulation."*
  and proceeds seamlessly using touch gestures and CameraX without crashing.

### 4. Reusable Assessment Engine with Zero-Tolerance Fatal Mistake Rule
- Reusable across all modules supporting 5 question types: Multiple-Choice, Image-Based, Order/Sequence, Scenario-Based, and AR Action Audits.
- **Zero-Tolerance Fatal Mistake Rule**: Even if a worker achieves an 85% numerical score (above 70% threshold), committing **any critical safety mistake** (e.g. entering confined space without gas testing) causes **immediate failure** (`isPassed() == false`, `isFailedDueToCriticalMistake() == true`).
- Weak area diagnostics generate targeted DGMS circular remediation advice.

### 5. Digital Certification & QR Verification Workbench
- Unique ID Format: `SAFETYAR-JH-2026-XXXXXX` (e.g. `SAFETYAR-JH-2026-000001`).
- Signed with a salted SHA-256 cryptographic hash token.
- High-resolution in-app certificate viewer with Government of Jharkhand and JISC insignia.
- Save & Share: Export as PNG via Android `FileProvider` and share credentials via standard Android Sharesheet.
- **4 Distinct Verification States**:
  - `VALID` (Green theme `#10B981`, checkmark shield, authorized shift entry)
  - `EXPIRED` (Amber theme `#F59E0B`, warning clock, recertification mandatory)
  - `REVOKED` (Red theme `#EF4444`, banned alert, prohibited from underground entry)
  - `NOT FOUND` (Dark red/gray theme `#DC2626`, counterfeit or unregistered warning)
- Built-in SIH Demonstration Quick-Test Panel with 4 simulation buttons.

---

## 🌐 Central REST API Backend & 8-Page Admin Dashboard

The platform includes an independent central backend located in `backend/`:

### Key Features:
- **REST Endpoints**: Authentication, Workers, Training Modules, Assignments, Assessments, Certificates, Verification, WorkManager Sync, and Analytics.
- **Dual-Store Persistence Engine**: Connects to **Firebase Firestore** if configured, or automatically falls back to a **zero-dependency pre-seeded in-memory store**.
- **Comprehensive 8-Page Web Admin Dashboard** (`http://localhost:5000`):
  1. `Executive Dashboard` (Real-time KPIs, sector compliance rates, sync logs)
  2. `Workers & Roster` (Sector filtering, 30-day orientation tracker, worker registration)
  3. `Training & Modules` (Curriculum catalog, assignment dispatch tool)
  4. `Assessments & Audits` (Vocational score ledger, zero-tolerance violation tracker)
  5. `Certificates & QR Workbench` (Interactive 4-state QR validator & revocation workbench)
  6. `DGMS Safety Compliance` (Statutory checklist: Rule 115, Circular 3, IS-14489)
  7. `Reports & Export` (One-click export to CSV & JSON, print PDF dossier)
  8. `System Settings & Sync` (Live sync queue monitor, API health check, database reset)
- **4-Role Security Matrix**:
  - `Super Admin` (Full access to all 8 views and DB reset)
  - `Safety Officer` (Compliance audits, certificate revocation)
  - `Trainer` (Module dispatch, pass/fail review)
  - `Supervisor` (Shift roster, 30-day orientation monitoring)

---

## 🧪 Automated Unit Test Suite

Located in `app/src/test/java/com/safetyar/app/domain/`:
1. **`AssessmentEngineTest.java`**:
   - Tests pass threshold (70% vs strict 80%).
   - Tests fail on score below threshold.
   - **Tests zero-tolerance critical safety violation failure** (80% score fails immediately due to critical mistake).
   - Tests weak area diagnostic generation with DGMS circular advice.
2. **`OrientationTrackerTest.java`**:
   - Tests statutory 30-day orientation calculation (Day 1, Day 12 for demo worker, Day 30 cap).
   - Tests completion percentage (12 days = 40%, 30 days = 100%).
   - Tests phase progressions (Surface &rarr; Underground &rarr; Machinery &rarr; Certification).
3. **`QrPassVerificationTest.java`**:
   - Tests certificate ID formatting (`SAFETYAR-JH-2026-XXXXXX`).
   - Tests SHA-256 hash determinism and tamper resistance.
   - Tests QR JSON structure parsing.
   - Tests all 4 verification states (`VALID`, `EXPIRED`, `REVOKED`, `NOT FOUND`).

---

## 📖 Complete Documentation Links

- [📐 Technical Architecture Dossier (Mermaid Diagrams, DB Schemas, Sync)](docs/ARCHITECTURE.md)
- [🎬 30-Step SIH Presentation Script & Evaluation Guide](docs/SIH_DEMO_SCRIPT.md)
- [📡 REST API Endpoints & Swagger-Style Documentation](backend/API_DOCUMENTATION.md)
- [🖥️ Web Admin Dashboard Guide](admin-dashboard/README.md)

---

## 🚀 How to Run the Project

### 1. Running the Android Application
1. Launch **Android Studio** (Koala, Ladybug, Iguana, Hedgehog or newer).
2. Open the project root: `c:\Users\ADMIN\Desktop\NexaMine`.
3. Wait for Gradle to sync dependencies.
4. Connect an Android device (Android 10+ / API 29+) or launch an Android Emulator with Camera support.
5. Click **Run 'app'** (`Shift + F10`).
6. Tap **"Ramesh Soren (WRK-JH-COAL-0891) - Coal Mining"** on the login screen to begin the SIH demonstration!

### 2. Running the Central REST API Backend & Admin Portal
```bash
cd backend
npm install
npm run dev
```
Open **[http://localhost:5000](http://localhost:5000)** in any web browser to access the 8-page Admin Web Portal and live QR Verification Workbench.

---

## 👥 Demo Pre-Seeded Personnel Credentials

| Role | Worker ID | Name | Sector / Enterprise | Division | Initial Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Primary Demo Miner** | `WRK-JH-COAL-0891` | **Ramesh Soren** | **Coal Mining (BCCL)** | **Moonidih Project, Seam XVI** | **Day 12 / 30 &bull; Valid Cert** |
| Blast Furnace Tech | `WRK-JH-STEEL-1045`| Arjun Mahto | Steel (Tata Steel) | Iron Making Division, BF I | Day 5 / 30 &bull; Expired Cert |
| Flake Specialist | `WRK-JH-MICA-0312` | Sunita Murmu | Mica (JSMDC) | Koderma Flake Unit | Day 24 / 30 &bull; Revoked Cert |
| Safety Inspector | `SUP-JH-DGMS-0001` | B. N. Singh | DGMS Regulator | Eastern Zone Audit Bureau | Super Admin / Compliance |

---
*SafetyAR &bull; Smart India Hackathon 2026 &bull; Government of Jharkhand &bull; Directorate General of Mines Safety (DGMS)*
