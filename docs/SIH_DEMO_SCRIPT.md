# SafetyAR: 30-Step SIH 2026 Jury Presentation & Evaluation Script
**Official Demonstration Guide for Smart India Hackathon Evaluators**

This script outlines the exact sequence to demonstrate the complete end-to-end functionality of SafetyAR in under **8 to 10 minutes**.

---

### Step 1: Splash Screen
- Launch the application on the Android device or emulator.
- **Visuals**: Observe the high-contrast industrial SafetyAR crest, DGMS compliance subtext, and instant local database bootstrap checkmark.

### Step 2: Language Selection
- Tap the **Language** icon in the top header.
- Switch between:
  1. **English** (Standard statutory terminology)
  2. **Hindi (हिंदी)** (Devenagari translation of all safety warnings and questions)
  3. **Santali (Ol Chiki - ᱥᱟᱱᱛᱟᱲᱤ)** (Tribal miner language support tailored for Jharkhand)
- Note the instantaneous UI re-render without restarting the app.

### Step 3: 1-Tap Demo Worker Login
- On the Login screen, click the primary demo card:
  **"Ramesh Soren (WRK-JH-COAL-0891) • BCCL Dhanbad • Coal Mining"**
- Note the instantaneous sign-in authenticated locally from the Room database.

### Step 4: Worker Profile
- Inspect the worker credential card:
  - **Name**: Ramesh Soren
  - **Worker ID**: `WRK-JH-COAL-0891`
  - **Enterprise**: Bharat Coking Coal Limited (BCCL), Dhanbad
  - **Division**: Moonidih Underground Project, Seam XVI
  - **Designation**: Underground Drill & Strata Technician

### Step 5: Safety Dashboard
- Review the executive worker dashboard:
  - **30-Day Orientation Progress**: Day 12 / 30 (40% Complete)
  - **Assigned Modules**: Fire & Explosion Emergency Response, Gas Leak & Confined Space Safety
  - **Compliance Badge**: `COMPLIANT`
  - **Sync Queue Indicator**: `Synced (0 Pending)`

### Step 6: Assigned Training Overview
- Under "Assigned Mandatory Training", select **"Fire & Explosion Emergency Response"** (`MOD-FIRE-01`).

### Step 7: 11-Step Microlearning Framework
- The training view opens with the interactive 11-step progress stepper:
  - Step 1: Introduction
  - Step 2: Learning Objectives
  - Step 3: Safety Theory (DGMS Coal Mines Rule 118)
  - Step 4: Visual Explanation
  - Step 5: AR Demonstration

### Step 8: AR Demonstration Launch
- Tap **"Launch AR Demonstration"**.
- If on ARCore hardware: Camera stream opens with detected surface planes.
- If on unsupported device: Clear fallback banner displays:
  *"AR is not supported on this device. You can continue using Interactive Safety Simulation."*
  and proceeds seamlessly without crashing.

### Step 9: AR Practice Mode
- Observe virtual safety objects overlaid in real-time:
  - Active methane flame and hazard zone perimeter
  - Emergency pull-alarm switch
  - DCP Fire Extinguisher station
  - Green illuminated evacuation floor arrows
  - Surface muster point sign

### Step 10: Sequential AR Scenario Execution
- Follow the 10-step sequential evaluation:
  1. Recognize fire and sound alarm.
  2. Maintain 3-meter stand-off perimeter.
  3. Pick up the Dry Chemical Powder (DCP) extinguisher.

### Step 11: Unsafe Action Penalty Demonstration
- Demonstrate deliberate unsafe behavior:
  - Tap *"Deploy Water Hose on Electrical Coal Seam"* or in Gas Module, attempt *"Enter Confined Space Without Form-IV Permit"*.
- **System Feedback**:
  - Haptic device vibration triggers.
  - Large red banner appears: **"UNSAFE ACTION! Do not enter without required authorization and atmospheric testing."**
  - Instant score penalty deducted.

### Step 12: Corrective Action
- Select correct action: Apply the **PASS Protocol** (Pull pin, Aim nozzle at base, Squeeze lever, Sweep side-to-side).
- Virtual fire extinguishes; smoke clears.

### Step 13: Scenario Completion
- Follow illuminated green evacuation arrows to the Surface Assembly Muster Point.
- Completion score calculates (e.g. 94%).

### Step 14: Assessment Start
- Tap **"Proceed to Competency Assessment"**.
- Assessment Engine initializes with 5 question categories.

### Step 15: Multiple-Choice Questions (MCQ)
- Answer question regarding permissible methane concentrations under DGMS Rule 115 (Correct: 1.25%).

### Step 16: Image-Based Questions
- Identify the correct extinguisher color band (Blue = Dry Powder).

### Step 17: Sequence / Order Questions
- Arrange the PASS protocol in correct sequence: Pull &rarr; Aim &rarr; Squeeze &rarr; Sweep.

### Step 18: Scenario Question
- Evaluate emergency evacuation actions during a mine ventilation reversal.

### Step 19: Submit Assessment
- Tap **"Submit & Grade Assessment"**.

### Step 20: Score Calculation
- Engine evaluates answers locally in Room DB:
  - Score: **94%** (Above 80% passing standard)
  - Critical life-safety mistakes: **0**

### Step 21: Zero-Tolerance Policy Check
- Explain to the jury: If even a single critical life-safety question had been missed, the candidate would have **immediately failed** regardless of numerical score.

### Step 22: Weak Area Review & Remediation
- Review the DGMS circular advice provided for any missed sub-questions.

### Step 23: Digital Certificate Generation
- System automatically generates a tamper-evident digital certificate:
  - Unique ID: `SAFETYAR-JH-2026-000001`
  - Cryptographic SHA-256 token signed with state salt.

### Step 24: View Certificate In-App
- Inspect the high-resolution certificate with Jharkhand State Industrial Safety Council insignia, worker photo, score, and scannable 220dp QR code.

### Step 25: Save & Share Certificate
- Tap **"Export Certificate (PNG)"** &rarr; Generates printable PNG via Android FileProvider.
- Tap **"Share Credential"** &rarr; Launches Android Sharesheet to send certificate summary via WhatsApp/SMS/Email.

### Step 26: QR Verification - VALID State
- Open the built-in **Supervisor QR Scanner** or the **Web Admin Dashboard**.
- Scan or click test chip: `SAFETYAR-JH-2026-000001` (Ramesh Soren).
- **Result**: Brilliant Green HUD (`#10B981`) &bull; **VALID &bull; Authorized for Underground Shift Entry**.

### Step 27: QR Verification - EXPIRED State
- In the Quick Test Panel, tap `SAFETYAR-JH-2026-000002` (Arjun Mahto - Tata Steel).
- **Result**: Amber HUD (`#F59E0B`) &bull; **EXPIRED &bull; Recertification Mandatory Before Shift**.

### Step 28: QR Verification - REVOKED State
- In the Quick Test Panel, tap `SAFETYAR-JH-2026-000003` (Sunita Murmu - JSMDC).
- **Result**: Red Alert HUD (`#EF4444`) &bull; **REVOKED &bull; Suspended for Critical Safety Breach**.

### Step 29: QR Verification - NOT FOUND / Counterfeit State
- In the Quick Test Panel, tap `SAFETYAR-FAKE-999999`.
- **Result**: Dark Red HUD (`#DC2626`) &bull; **NOT FOUND &bull; Unregistered / Counterfeit Credential Warning**.

### Step 30: Training History & Sync Verification
- Open **Training History**:
  - Review all past assessment records, module names, attempt dates, and direct "View Certificate" actions.
- Check **Offline Sync Status**:
  - Show how all data is persisted in Room SQLite and automatically uploaded to the central backend when internet is restored.
