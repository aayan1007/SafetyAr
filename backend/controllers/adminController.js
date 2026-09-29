const db = require('../config/database');

exports.getSummary = (req, res) => {
  const totalWorkers = db.workers.filter(w => w.role === 'WORKER').length;
  const certifiedWorkers = db.workers.filter(w => w.role === 'WORKER' && w.isCertified).length;
  const revokedWorkers = db.workers.filter(w => w.complianceStatus === 'SUSPENDED_REVOKED').length;
  const needsRefresher = db.workers.filter(w => w.complianceStatus === 'NEEDS_REFRESHER').length;

  const totalCertificates = db.certificates.length;
  const validCertificates = db.certificates.filter(c => c.status === 'VALID').length;
  const expiredCertificates = db.certificates.filter(c => c.status === 'EXPIRED').length;
  const revokedCertificates = db.certificates.filter(c => c.status === 'REVOKED').length;

  const totalAssignments = db.assignments.length;
  const completedAssignments = db.assignments.filter(a => a.status === 'COMPLETED').length;

  const complianceRate = totalWorkers > 0 ? Math.round((certifiedWorkers / totalWorkers) * 100) : 0;

  res.json({
    success: true,
    data: {
      kpis: {
        totalWorkers,
        certifiedWorkers,
        complianceRatePercent: complianceRate,
        revokedWorkers,
        needsRefresher
      },
      certificates: {
        total: totalCertificates,
        valid: validCertificates,
        expired: expiredCertificates,
        revoked: revokedCertificates
      },
      training: {
        totalAssigned: totalAssignments,
        completed: completedAssignments,
        pending: totalAssignments - completedAssignments
      },
      organizationsCount: db.organizations.length,
      recentSyncCount: db.syncAuditLogs.length
    }
  });
};

exports.getComplianceTable = (req, res) => {
  const tableData = db.workers
    .filter(w => w.role === 'WORKER')
    .map(w => {
      const activeCert = db.certificates.find(c => c.workerId === w.workerId && c.status === 'VALID');
      const latestAttempt = [...db.assessments]
        .filter(a => a.workerId === w.workerId)
        .sort((a, b) => b.timestamp - a.timestamp)[0];

      return {
        workerId: w.workerId,
        fullName: w.fullName,
        designation: w.designation,
        enterprise: w.enterprise,
        sector: w.sector,
        orientationDay: w.orientationDay,
        isCertified: w.isCertified,
        complianceStatus: w.complianceStatus,
        activeCertificateId: activeCert ? activeCert.certificateId : null,
        latestScore: latestAttempt ? `${latestAttempt.percentage}%` : 'N/A',
        lastActiveDate: new Date(w.lastActiveTimestamp).toLocaleDateString('en-GB')
      };
    });

  res.json({
    success: true,
    count: tableData.length,
    data: tableData
  });
};
