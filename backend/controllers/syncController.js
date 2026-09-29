const db = require('../config/database');

exports.uploadSync = (req, res) => {
  const { workerId, deviceId, timestamp, assessments, certificates } = req.body;

  let processedAssessments = 0;
  let processedCertificates = 0;

  if (Array.isArray(assessments) && assessments.length > 0) {
    assessments.forEach(a => {
      const existing = db.assessments.find(e => e.assessmentId === a.assessmentId);
      if (!existing) {
        db.assessments.push({
          ...a,
          syncTimestamp: Date.now()
        });
        processedAssessments++;
      }
    });
  }

  if (Array.isArray(certificates) && certificates.length > 0) {
    certificates.forEach(c => {
      const existing = db.certificates.find(e => e.certificateId === c.certificateId);
      if (!existing) {
        db.certificates.push({
          ...c,
          syncTimestamp: Date.now()
        });
        processedCertificates++;
      }
    });
  }

  const logEntry = {
    logId: `SYNC-${Date.now().toString().slice(-6)}`,
    workerId: workerId || 'WRK-JH-OFFLINE',
    deviceId: deviceId || 'Android Client',
    timestamp: timestamp || Date.now(),
    status: 'SUCCESS',
    recordsUploaded: processedAssessments + processedCertificates,
    clientIp: req.ip || req.connection.remoteAddress
  };

  db.syncAuditLogs.unshift(logEntry);

  const worker = db.workers.find(w => w.workerId === workerId);
  if (worker) {
    worker.lastActiveTimestamp = Date.now();
  }

  res.json({
    success: true,
    message: 'Synchronization payload processed successfully.',
    data: {
      logId: logEntry.logId,
      processedAssessments,
      processedCertificates,
      serverTimestamp: Date.now()
    }
  });
};

exports.downloadUpdates = (req, res) => {
  const { workerId } = req.params;

  const worker = db.workers.find(w => w.workerId.toUpperCase() === workerId.toUpperCase());
  const assignments = db.assignments.filter(a => a.workerId.toUpperCase() === workerId.toUpperCase());
  const certs = db.certificates.filter(c => c.workerId.toUpperCase() === workerId.toUpperCase());

  res.json({
    success: true,
    data: {
      worker: worker || null,
      assignments,
      certificates: certs,
      modules: db.modules,
      serverTimestamp: Date.now()
    }
  });
};

exports.getSyncLogs = (req, res) => {
  res.json({
    success: true,
    count: db.syncAuditLogs.length,
    data: db.syncAuditLogs
  });
};
