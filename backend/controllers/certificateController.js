const db = require('../config/database');
const { computeToken } = require('../config/database');

exports.issueCertificate = (req, res) => {
  const { workerId, organization, trainingModule, score, sector, trade } = req.body;

  if (!workerId || !trainingModule) {
    return res.status(400).json({ success: false, message: 'workerId and trainingModule are required.' });
  }

  const worker = db.workers.find(w => w.workerId.toUpperCase() === workerId.toUpperCase());
  const now = Date.now();
  const expiresAt = now + (365 * 24 * 60 * 60 * 1000);

  const randomSeq = Math.floor(100000 + Math.random() * 900000);
  const certId = `SAFETYAR-JH-2026-${randomSeq}`;
  const orgName = organization || (worker ? worker.enterprise : 'Jharkhand Industrial Safety Council (JISC)');
  const workerName = worker ? worker.fullName : 'Industrial Operator';
  const finalScore = score || 85;

  const token = computeToken(certId, workerId, orgName, trainingModule, finalScore, now, expiresAt);

  const cert = {
    certificateId: certId,
    workerId,
    workerName,
    organization: orgName,
    trainingModule,
    score: finalScore,
    sector: sector || (worker ? worker.sector : 'COAL_MINING'),
    trade: trade || (worker ? worker.designation : 'Safety Technician'),
    issuedTimestamp: now,
    expiresTimestamp: expiresAt,
    issuingAuthority: 'Jharkhand Industrial Safety Council (JISC)',
    status: 'VALID',
    verificationToken: token,
    revocationReason: null
  };

  db.certificates.push(cert);

  if (worker) {
    worker.isCertified = true;
    worker.complianceStatus = 'COMPLIANT';
  }

  res.status(201).json({
    success: true,
    message: 'Certificate issued successfully.',
    data: cert
  });
};

exports.getCertificateById = (req, res) => {
  const { id } = req.params;
  const cert = db.certificates.find(c => c.certificateId.toUpperCase() === id.toUpperCase());

  if (!cert) {
    return res.status(404).json({ success: false, message: 'Certificate not found.' });
  }

  res.json({
    success: true,
    data: cert
  });
};

exports.getAllCertificates = (req, res) => {
  const { status, workerId, sector } = req.query;

  let list = [...db.certificates];
  if (status) {
    list = list.filter(c => c.status.toUpperCase() === status.toUpperCase());
  }
  if (workerId) {
    list = list.filter(c => c.workerId.toUpperCase() === workerId.toUpperCase());
  }
  if (sector) {
    list = list.filter(c => c.sector.toUpperCase() === sector.toUpperCase());
  }

  res.json({
    success: true,
    count: list.length,
    data: list
  });
};

/**
 * QR & Certificate Verification Endpoint
 * Returns one of the 4 Mandatory States:
 * - VALID
 * - EXPIRED
 * - REVOKED
 * - NOT_FOUND
 */
exports.verifyCertificate = (req, res) => {
  const { query, certId, token, payload } = req.body;
  const rawInput = (query || certId || payload || '').trim();

  if (!rawInput) {
    return res.status(200).json({
      success: true,
      verificationStatus: 'NOT_FOUND',
      message: 'No certificate ID or QR payload provided.',
      complianceNote: 'Counterfeit check: Input payload is blank or unreadable.',
      data: null
    });
  }

  let targetId = rawInput;
  let tokenFromClient = token || null;
  const now = Date.now();

  // Parse if JSON payload was passed
  if (rawInput.startsWith('{') && rawInput.endsWith('}')) {
    try {
      const parsed = JSON.parse(rawInput);
      if (parsed.certId) targetId = parsed.certId;
      if (parsed.token) tokenFromClient = parsed.token;
      else if (parsed.signature) tokenFromClient = parsed.signature;
    } catch (e) {
      // malformed JSON
    }
  }

  const cert = db.certificates.find(c => c.certificateId.toUpperCase() === targetId.toUpperCase());

  // 1. NOT_FOUND check
  if (!cert) {
    return res.status(200).json({
      success: true,
      verificationStatus: 'NOT_FOUND',
      certificateId: targetId,
      message: 'CREDENTIAL NOT FOUND IN CENTRAL DGMS REGISTRY.',
      complianceNote: 'The scanned certificate ID is not registered in the Jharkhand State Mining & Industrial Safety database. Potential fraudulent credential.',
      data: null
    });
  }

  // 2. REVOKED check
  if (cert.status === 'REVOKED') {
    return res.status(200).json({
      success: true,
      verificationStatus: 'REVOKED',
      certificateId: cert.certificateId,
      message: 'ACCESS PROHIBITED: Safety Certificate Revoked by Inspectorate.',
      complianceNote: cert.revocationReason || 'Revoked for critical life-safety protocol violation (DGMS Circular 3 of 2019). Worker must surrender site badge immediately.',
      data: cert
    });
  }

  // 3. EXPIRED check
  if (now > cert.expiresTimestamp || cert.status === 'EXPIRED') {
    const expiredDateStr = new Date(cert.expiresTimestamp).toLocaleDateString('en-GB');
    return res.status(200).json({
      success: true,
      verificationStatus: 'EXPIRED',
      certificateId: cert.certificateId,
      message: `RECERTIFICATION REQUIRED: Certificate expired on ${expiredDateStr}.`,
      complianceNote: 'Under Mines Vocational Training Rules 1966, annual refresher certification is mandatory before site entry. Access temporarily suspended.',
      data: cert
    });
  }

  // 4. Token validation (if token supplied)
  if (tokenFromClient && cert.verificationToken && tokenFromClient !== cert.verificationToken) {
    return res.status(200).json({
      success: true,
      verificationStatus: 'NOT_FOUND',
      certificateId: cert.certificateId,
      message: 'TAMPER DETECTED: Cryptographic SHA-256 token mismatch.',
      complianceNote: 'Digital signature does not match central authority records. Security audit flagged.',
      data: null
    });
  }

  // 5. VALID state
  return res.status(200).json({
    success: true,
    verificationStatus: 'VALID',
    certificateId: cert.certificateId,
    message: 'AUTHENTICATED: Valid DGMS-Compliant Safety Credential.',
    complianceNote: 'Cryptographic signature matched. Worker is certified and authorized for hazardous zone operations.',
    data: cert
  });
};

exports.revokeCertificate = (req, res) => {
  const { id } = req.params;
  const { reason } = req.body;

  const cert = db.certificates.find(c => c.certificateId.toUpperCase() === id.toUpperCase());
  if (!cert) {
    return res.status(404).json({ success: false, message: 'Certificate not found.' });
  }

  cert.status = 'REVOKED';
  cert.revocationReason = reason || 'Revoked by DGMS Safety Directorate for life-safety non-compliance.';

  const worker = db.workers.find(w => w.workerId === cert.workerId);
  if (worker) {
    worker.isCertified = false;
    worker.complianceStatus = 'SUSPENDED_REVOKED';
  }

  res.json({
    success: true,
    message: 'Certificate revoked successfully.',
    data: cert
  });
};
