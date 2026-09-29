# SafetyAR Admin Web Dashboard

This directory contains the standalone web admin portal for the SafetyAR platform, adhering to Section 17 of the SIH 2026 specification.

## Features
- **8 Dedicated Pages / Views**:
  1. Executive Dashboard (KPIs, live compliance rate, sector breakdowns)
  2. Workers & Roster (Worker management, sector filters, 30-day orientation status)
  3. Training & Modules (Curriculum, DGMS standards, assignment dispatch tool)
  4. Assessments & Audits (Vocational score ledger, zero-tolerance life-safety mistake counts)
  5. Certificates & QR Workbench (Interactive 4-state QR validator: VALID, EXPIRED, REVOKED, NOT FOUND, instant revocation tool)
  6. DGMS Safety Compliance (Rule 115, Circular 3, Form-IV Confined Space, IS-14489)
  7. Reports & Export (Downloadable CSV & JSON dossiers, print PDF view)
  8. System Settings & Sync (WorkManager sync queue logs, demo DB reset)

- **4-Role Security Matrix**:
  - Super Admin (Full access to all 8 operational views)
  - Safety Officer (Compliance enforcement, certificate revocation)
  - Trainer (Curriculum assignments, pass/fail grading)
  - Supervisor (Roster inspection, 30-day orientation monitoring)

## Running the Dashboard
The dashboard is served directly by the central backend server:
```bash
cd backend
npm install
npm run dev
```
Then open [http://localhost:5000](http://localhost:5000) in any web browser.
