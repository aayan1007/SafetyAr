const db = require('../config/database');

exports.getWorkers = (req, res) => {
  const { sector, enterprise, complianceStatus } = req.query;

  let list = [...db.workers];
  if (sector) {
    list = list.filter(w => w.sector.toUpperCase() === sector.toUpperCase());
  }
  if (enterprise) {
    list = list.filter(w => w.enterprise.toLowerCase().includes(enterprise.toLowerCase()));
  }
  if (complianceStatus) {
    list = list.filter(w => w.complianceStatus === complianceStatus);
  }

  res.json({
    success: true,
    count: list.length,
    data: list
  });
};

exports.getWorkerById = (req, res) => {
  const { id } = req.params;
  const worker = db.workers.find(w => w.workerId.toUpperCase() === id.toUpperCase());

  if (!worker) {
    return res.status(404).json({ success: false, message: 'Worker not found.' });
  }

  const cert = db.certificates.find(c => c.workerId.toUpperCase() === id.toUpperCase() && c.status === 'VALID');
  const assignments = db.assignments.filter(a => a.workerId.toUpperCase() === id.toUpperCase());

  res.json({
    success: true,
    data: {
      ...worker,
      activeCertificate: cert || null,
      assignments: assignments
    }
  });
};

exports.createWorker = (req, res) => {
  const { workerId, fullName, designation, sector, enterprise, division } = req.body;

  if (!workerId || !fullName || !sector) {
    return res.status(400).json({ success: false, message: 'workerId, fullName, and sector are required.' });
  }

  const existing = db.workers.find(w => w.workerId.toUpperCase() === workerId.toUpperCase());
  if (existing) {
    return res.status(409).json({ success: false, message: 'Worker ID already exists.' });
  }

  const newWorker = {
    workerId,
    orgId: 'ORG-CUSTOM',
    fullName,
    passcode: '1234',
    role: 'WORKER',
    designation: designation || 'Field Operator',
    sector,
    enterprise: enterprise || 'Jharkhand Industrial Works',
    division: division || 'Surface & Underground Division',
    onboardingDate: new Date().toLocaleDateString('en-GB'),
    assignedModuleId: 'MOD-FIRE-01',
    orientationDay: 1,
    isCertified: false,
    complianceStatus: 'NEEDS_TRAINING',
    lastActiveTimestamp: Date.now()
  };

  db.workers.push(newWorker);

  res.status(201).json({
    success: true,
    message: 'Worker registered successfully.',
    data: newWorker
  });
};

exports.updateCompliance = (req, res) => {
  const { id } = req.params;
  const { complianceStatus, isCertified } = req.body;

  const worker = db.workers.find(w => w.workerId.toUpperCase() === id.toUpperCase());
  if (!worker) {
    return res.status(404).json({ success: false, message: 'Worker not found.' });
  }

  if (complianceStatus !== undefined) worker.complianceStatus = complianceStatus;
  if (isCertified !== undefined) worker.isCertified = Boolean(isCertified);
  worker.lastActiveTimestamp = Date.now();

  res.json({
    success: true,
    message: 'Worker compliance updated.',
    data: worker
  });
};
