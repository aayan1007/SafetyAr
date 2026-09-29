/**
 * SafetyAR Central Database Store (In-Memory / Local JSON Store)
 * Pre-seeded with realistic Jharkhand Industrial Safety Council (JISC) & DGMS records.
 */

const crypto = require('crypto');

const CRYPTO_SALT = process.env.CRYPTO_SALT || 'SAFETYAR_DGMS_JHARKHAND_2026';

function computeToken(certId, workerId, org, module, score, issuedAt, expiresAt) {
  const rawData = `${certId}|${workerId}|${org}|${module}|${score}|${issuedAt}|${expiresAt}|${CRYPTO_SALT}`;
  return crypto.createHash('sha256').update(rawData).digest('hex');
}

class DatabaseStore {
  constructor() {
    this.resetSeed();
  }

  resetSeed() {
    const now = Date.now();
    const oneDay = 24 * 60 * 60 * 1000;
    const oneYear = 365 * oneDay;

    // 1. Organizations
    this.organizations = [
      {
        orgId: 'ORG-BCCL',
        name: 'Bharat Coking Coal Limited (BCCL)',
        sector: 'COAL_MINING',
        headquarters: 'Koyla Bhawan, Dhanbad, Jharkhand',
        directorate: 'DGMS Eastern Zone, Dhanbad',
        phone: '0326-2230190'
      },
      {
        orgId: 'ORG-TATA',
        name: 'Tata Steel Limited',
        sector: 'STEEL_MANUFACTURING',
        headquarters: 'Jamshedpur Works, East Singhbhum, Jharkhand',
        directorate: 'Jharkhand Factory Directorate, Jamshedpur',
        phone: '0657-2431000'
      },
      {
        orgId: 'ORG-JSMDC',
        name: 'Jharkhand State Mineral Dev Corp (JSMDC)',
        sector: 'MICA_PROCESSING',
        headquarters: 'Khanij Nigam Bhawan, Ranchi / Koderma',
        directorate: 'DGMS North Zone, Koderma',
        phone: '0651-2490767'
      }
    ];

    // 2. Industrial Workers
    this.workers = [
      {
        workerId: 'WRK-JH-COAL-0891',
        orgId: 'ORG-BCCL',
        fullName: 'Ramesh Soren',
        passcode: '1234', // Simple demo PIN
        role: 'WORKER',
        designation: 'Underground Drill & Strata Technician',
        sector: 'COAL_MINING',
        enterprise: 'Bharat Coking Coal Limited (BCCL)',
        division: 'Moonidih Underground Project, Seam XVI',
        onboardingDate: '02 Sep 2026',
        assignedModuleId: 'MOD-FIRE-01',
        orientationDay: 12,
        isCertified: true,
        complianceStatus: 'COMPLIANT',
        lastActiveTimestamp: now
      },
      {
        workerId: 'WRK-JH-STEEL-1045',
        orgId: 'ORG-TATA',
        fullName: 'Arjun Mahto',
        passcode: '1234',
        role: 'WORKER',
        designation: 'Blast Furnace Taphole & Slag Operator',
        sector: 'STEEL_MANUFACTURING',
        enterprise: 'Tata Steel Limited',
        division: 'Iron Making Division, Blast Furnace I',
        onboardingDate: '09 Sep 2026',
        assignedModuleId: 'MOD-STEEL-01',
        orientationDay: 5,
        isCertified: false,
        complianceStatus: 'NEEDS_REFRESHER',
        lastActiveTimestamp: now - (2 * oneDay)
      },
      {
        workerId: 'WRK-JH-MICA-0312',
        orgId: 'ORG-JSMDC',
        fullName: 'Sunita Murmu',
        passcode: '1234',
        role: 'WORKER',
        designation: 'Mica Flake Quality & Particulate Specialist',
        sector: 'MICA_PROCESSING',
        enterprise: 'Jharkhand State Mineral Development (JSMDC)',
        division: 'Koderma Industrial Mica Flake Unit',
        onboardingDate: '21 Aug 2026',
        assignedModuleId: 'MOD-MICA-01',
        orientationDay: 24,
        isCertified: false,
        complianceStatus: 'SUSPENDED_REVOKED',
        lastActiveTimestamp: now - (5 * oneDay)
      },
      {
        workerId: 'SUP-JH-DGMS-0001',
        orgId: 'ORG-BCCL',
        fullName: 'B. N. Singh (DGMS Inspector)',
        passcode: '9999',
        role: 'SUPERVISOR',
        designation: 'Mines Safety Overman & Compliance Officer',
        sector: 'GOVERNMENT_REGULATOR',
        enterprise: 'Directorate General of Mines Safety (DGMS)',
        division: 'Eastern Zone Industrial Audit Bureau',
        onboardingDate: '01 Jan 2025',
        assignedModuleId: null,
        orientationDay: 30,
        isCertified: true,
        complianceStatus: 'COMPLIANT',
        lastActiveTimestamp: now
      }
    ];

    // 3. Training Modules
    this.modules = [
      {
        moduleId: 'MOD-FIRE-01',
        title: 'Fire & Explosion Emergency Response',
        sector: 'COAL_MINING',
        standardCode: 'DGMS-COAL-R118-FIRE',
        description: 'Methane and electrical fire isolation, PASS extinguisher protocol, and mine evacuation.',
        totalLessons: 4,
        passThreshold: 80,
        estimatedMinutes: 15
      },
      {
        moduleId: 'MOD-GAS-01',
        title: 'Gas Leak & Confined Space Safety',
        sector: 'COAL_MINING',
        standardCode: 'DGMS-CS-S54:2024',
        description: 'Atmospheric 4-gas testing, SCBA donning, standby buddy verification, and Form-IV permits.',
        totalLessons: 4,
        passThreshold: 80,
        estimatedMinutes: 18
      },
      {
        moduleId: 'MOD-COAL-01',
        title: 'Roof Strata Stability & Methane Detection',
        sector: 'COAL_MINING',
        standardCode: 'DGMS-COAL-R115',
        description: 'Testing bar sounding, hydraulic props setting, and methane cut-off procedures.',
        totalLessons: 3,
        passThreshold: 80,
        estimatedMinutes: 15
      },
      {
        moduleId: 'MOD-STEEL-01',
        title: 'Blast Furnace Tapping & Molten Metal Splash',
        sector: 'STEEL_MANUFACTURING',
        standardCode: 'IS-14489:2018',
        description: 'Aluminized heat suits, slag runner clearance perimeters, and splash barrier SOPs.',
        totalLessons: 3,
        passThreshold: 80,
        estimatedMinutes: 18
      },
      {
        moduleId: 'MOD-MICA-01',
        title: 'Respirable Mica Dust & Silicosis Prevention',
        sector: 'MICA_PROCESSING',
        standardCode: 'DGMS-OCC-H98',
        description: 'Wet dust misting suppression, N95/FFP3 respirators, and lung clearance audits.',
        totalLessons: 3,
        passThreshold: 80,
        estimatedMinutes: 14
      }
    ];

    // 4. Training Assignments
    this.assignments = [
      {
        assignmentId: 'ASGN-001',
        workerId: 'WRK-JH-COAL-0891',
        moduleId: 'MOD-FIRE-01',
        assignedBy: 'Safety Overman B. N. Singh',
        assignedTimestamp: now - (5 * oneDay),
        dueTimestamp: now + (2 * oneDay),
        status: 'COMPLETED',
        priority: 'HIGH'
      },
      {
        assignmentId: 'ASGN-002',
        workerId: 'WRK-JH-COAL-0891',
        moduleId: 'MOD-GAS-01',
        assignedBy: 'Safety Overman B. N. Singh',
        assignedTimestamp: now - (1 * oneDay),
        dueTimestamp: now + (6 * oneDay),
        status: 'IN_PROGRESS',
        priority: 'HIGH'
      },
      {
        assignmentId: 'ASGN-003',
        workerId: 'WRK-JH-STEEL-1045',
        moduleId: 'MOD-STEEL-01',
        assignedBy: 'Shift Supervisor R. K. Verma',
        assignedTimestamp: now - (4 * oneDay),
        dueTimestamp: now + (3 * oneDay),
        status: 'PENDING',
        priority: 'HIGH'
      },
      {
        assignmentId: 'ASGN-004',
        workerId: 'WRK-JH-MICA-0312',
        moduleId: 'MOD-MICA-01',
        assignedBy: 'Unit Officer P. Murmu',
        assignedTimestamp: now - (10 * oneDay),
        dueTimestamp: now - (1 * oneDay),
        status: 'OVERDUE',
        priority: 'MEDIUM'
      }
    ];

    // 5. Training Progress
    this.progress = [
      {
        workerId: 'WRK-JH-COAL-0891',
        moduleId: 'MOD-FIRE-01',
        currentStep: 11,
        completed: true,
        percentComplete: 100,
        lastUpdated: now - (10 * oneDay)
      },
      {
        workerId: 'WRK-JH-COAL-0891',
        moduleId: 'MOD-GAS-01',
        currentStep: 6,
        completed: false,
        percentComplete: 55,
        lastUpdated: now
      },
      {
        workerId: 'WRK-JH-STEEL-1045',
        moduleId: 'MOD-STEEL-01',
        currentStep: 2,
        completed: false,
        percentComplete: 20,
        lastUpdated: now - (2 * oneDay)
      }
    ];

    // 6. Assessment Records
    this.assessments = [
      {
        attemptId: 'ATT-2026-001',
        assessmentId: 'ASSESS-FIRE-01',
        workerId: 'WRK-JH-COAL-0891',
        moduleId: 'MOD-FIRE-01',
        scoreObtained: 94,
        totalPossible: 100,
        percentage: 94,
        isPassed: true,
        criticalMistakesCount: 0,
        correctCount: 9,
        incorrectCount: 1,
        timestamp: now - (10 * oneDay),
        durationSeconds: 42
      }
    ];

    // 7. Seed Sample Certificates (Covers the 3 core states: VALID, EXPIRED, REVOKED)
    const cert1Id = 'SAFETYAR-JH-2026-000001';
    const cert1Issued = now - (12 * oneDay);
    const cert1Expires = cert1Issued + oneYear;
    const cert1Token = computeToken(cert1Id, 'WRK-JH-COAL-0891', 'Bharat Coking Coal Limited (BCCL)', 'Fire & Explosion Emergency Response', 94, cert1Issued, cert1Expires);

    const cert2Id = 'SAFETYAR-JH-2026-000002';
    const cert2Issued = now - (400 * oneDay);
    const cert2Expires = now - (35 * oneDay); // Expired 35 days ago
    const cert2Token = computeToken(cert2Id, 'WRK-JH-STEEL-1045', 'Tata Steel Limited', 'Blast Furnace Tapping & Molten Metal Splash', 88, cert2Issued, cert2Expires);

    const cert3Id = 'SAFETYAR-JH-2026-000003';
    const cert3Issued = now - (60 * oneDay);
    const cert3Expires = cert3Issued + oneYear;
    const cert3Token = computeToken(cert3Id, 'WRK-JH-MICA-0312', 'Jharkhand State Mineral Dev Corp', 'Respirable Mica Dust & Silicosis Prevention', 82, cert3Issued, cert3Expires);

    this.certificates = [
      {
        certificateId: cert1Id,
        workerId: 'WRK-JH-COAL-0891',
        workerName: 'Ramesh Soren',
        organization: 'Bharat Coking Coal Limited (BCCL)',
        trainingModule: 'Fire & Explosion Emergency Response',
        score: 94,
        sector: 'COAL_MINING',
        trade: 'Underground Drill & Strata Technician',
        issuedTimestamp: cert1Issued,
        expiresTimestamp: cert1Expires,
        issuingAuthority: 'Jharkhand Industrial Safety Council (JISC)',
        status: 'VALID',
        verificationToken: cert1Token,
        revocationReason: null
      },
      {
        certificateId: cert2Id,
        workerId: 'WRK-JH-STEEL-1045',
        workerName: 'Arjun Mahto',
        organization: 'Tata Steel Limited',
        trainingModule: 'Blast Furnace Tapping & Molten Metal Splash',
        score: 88,
        sector: 'STEEL_MANUFACTURING',
        trade: 'Blast Furnace Taphole & Slag Operator',
        issuedTimestamp: cert2Issued,
        expiresTimestamp: cert2Expires,
        issuingAuthority: 'Jharkhand Industrial Safety Council (JISC)',
        status: 'EXPIRED',
        verificationToken: cert2Token,
        revocationReason: 'Validity period exceeded (365 days expired).'
      },
      {
        certificateId: cert3Id,
        workerId: 'WRK-JH-MICA-0312',
        workerName: 'Sunita Murmu',
        organization: 'Jharkhand State Mineral Dev Corp (JSMDC)',
        trainingModule: 'Respirable Mica Dust & Silicosis Prevention',
        score: 82,
        sector: 'MICA_PROCESSING',
        trade: 'Mica Flake Quality Specialist',
        issuedTimestamp: cert3Issued,
        expiresTimestamp: cert3Expires,
        issuingAuthority: 'Jharkhand Industrial Safety Council (JISC)',
        status: 'REVOKED',
        verificationToken: cert3Token,
        revocationReason: 'DGMS Notice #JH-2026-89: Life-safety respiratory compliance violation.'
      }
    ];

    // 8. Sync Audit Logs
    this.syncAuditLogs = [
      {
        logId: 'SYNC-LOG-01',
        workerId: 'WRK-JH-COAL-0891',
        deviceId: 'Android-Handheld-SM-G991B',
        timestamp: now - (2 * 60 * 60 * 1000),
        status: 'SUCCESS',
        recordsUploaded: 2,
        clientIp: '127.0.0.1'
      }
    ];
  }
}

const db = new DatabaseStore();

module.exports = db;
module.exports.computeToken = computeToken;
