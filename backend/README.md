# SafetyAR Backend Server

Central REST API & Verification Authority for the **SafetyAR** platform (Smart India Hackathon 2026).

Built with **Node.js**, **Express**, and **Firebase Admin SDK**, with automatic zero-setup in-memory fallback for local demo and offline independence.

---

## 🚀 Quick Start

### 1. Install Dependencies
```bash
cd backend
npm install
```

### 2. Run Locally
```bash
npm start
```
The server will boot on `http://localhost:5000`:
- **Web Admin Dashboard:** [http://localhost:5000/](http://localhost:5000/)
- **Health Check:** [http://localhost:5000/api/health](http://localhost:5000/api/health)
- **API Documentation:** [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md)

---

## ⚡ Zero-Setup In-Memory Mode
If no Firebase credentials are configured in `.env`, the server automatically starts in **In-Memory Pre-Seeded Mode** (`backend/config/database.js`).
It comes pre-populated with:
- **Enterprises:** BCCL (Dhanbad), Tata Steel (Jamshedpur), JSMDC (Koderma)
- **Workers:** Ramesh Soren, Arjun Mahto, Sunita Murmu
- **Modules:** Fire & Explosion, Gas Leak & Confined Space, Roof Strata Stability, etc.
- **Certificates:** Pre-seeded for testing the 4 required states:
  - `SAFETYAR-JH-2026-000001` $\rightarrow$ **VALID**
  - `SAFETYAR-JH-2026-000002` $\rightarrow$ **EXPIRED**
  - `SAFETYAR-JH-2026-000003` $\rightarrow$ **REVOKED**
  - Any unknown ID $\rightarrow$ **NOT FOUND**

---

## 🔥 Firebase Mode (Optional)
To connect to Google Cloud / Firebase Firestore:
1. Create a Firebase project on the [Firebase Console](https://console.firebase.google.com).
2. Generate a Service Account Private Key (`Project Settings > Service Accounts > Generate New Private Key`).
3. Add the following to your `backend/.env`:
```env
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CLIENT_EMAIL=firebase-adminsdk@...
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
```
The server will automatically detect the credentials and switch database persistence to Cloud Firestore.

---

## 📱 Android Client Offline Independence
The Android app is architected with an **offline-first Room Database**:
- When the backend is offline or not deployed, the app functions **100% normally in demo mode** without crashing or freezing.
- Records (assessments, certificates) are saved locally and queued in Room `sync_queue`.
- When the backend becomes reachable, `SyncRepository` synchronizes pending records via `POST /api/v1/sync/upload`.
