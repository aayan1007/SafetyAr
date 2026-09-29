const db = require('../config/database');
const { computeToken } = require('../config/database');

exports.submitAssessment = (req, res) => {
  const {
    workerId,
    moduleId,
    scoreObtained,
    totalPossible,
    isPassed,
    criticalMistakesCount,
    correctCount,
    incorrectCount,
    durationSeconds
  } = req.body;

  if (!workerId || !moduleId) {
    return res.status(400).json({ success: false, message: 'workerId and moduleId are required.' });
  }

  const worker = db.workers.find(w => w.workerId.toUpperCase() === workerId.toUpperCase());
  const now = Date.now();
  const percentage = totalPossible > 0 ? Math.round((scoreObtained / totalPossible) * 100) : scoreObtained;

  const attemptId = `ATT-${Date.now().toString().slice(-8)}`;
  const record = {
    attemptId,
    assessmentId: `ASSESS-${moduleId}`,
    workerId,
    moduleId,
    scoreObtained: scoreObtained || 0,
    totalPossible: totalPossible || 100,
    percentage: percentage || 0,
    isPassed: Boolean(isPassed) && (criticalMistakesCount || 0) === 0,
    criticalMistakesCount: criticalMistakesCount || 0,
    correctCount: correctCount || 0,
    incorrectCount: incorrectCount || 0,
    timestamp: now,
    durationSeconds: durationSeconds || 0
  };

  db.assessments.push(record);

  let generatedCertificate = null;
  // If passed with zero critical errors, issue Certificate
  if (record.isPassed) {
    const randomSeq = Math.floor(100000 + Math.random() * 900000);
    const certId = `SAFETYAR-JH-2026-${randomSeq}`;
    const expiresAt = now + (365 * 24 * 60 * 60 * 1000);
    const orgName = worker ? worker.enterprise : 'Jharkhand Industrial Safety Council';
    const workerName = worker ? worker.fullName : 'Industrial Operator';
    const mod = db.modules.find(m => m.moduleId === moduleId);
    const modTitle = mod ? mod.title : moduleId;

    const token = computeToken(certId, workerId, orgName, modTitle, percentage, now, expiresAt);

    generatedCertificate = {
      certificateId: certId,
      workerId,
      workerName,
      organization: orgName,
      trainingModule: modTitle,
      score: percentage,
      sector: worker ? worker.sector : 'COAL_MINING',
      trade: worker ? worker.designation : 'Safety Operator',
      issuedTimestamp: now,
      expiresTimestamp: expiresAt,
      issuingAuthority: 'Jharkhand Industrial Safety Council (JISC)',
      status: 'VALID',
      verificationToken: token,
      revocationReason: null
    };

    db.certificates.push(generatedCertificate);

    if (worker) {
      worker.isCertified = true;
      worker.complianceStatus = 'COMPLIANT';
    }
  }

  res.status(201).json({
    success: true,
    message: record.isPassed ? 'Assessment passed and certificate generated.' : 'Assessment recorded. Review training recommended.',
    data: {
      attempt: record,
      certificate: generatedCertificate
    }
  });
};

exports.getWorkerAssessments = (req, res) => {
  const { workerId } = req.params;
  const list = db.assessments.filter(a => a.workerId.toUpperCase() === workerId.toUpperCase());

  res.json({
    success: true,
    count: list.length,
    data: list
  });
};

exports.getStats = (req, res) => {
  const total = db.assessments.length;
  const passed = db.assessments.filter(a => a.isPassed).length;
  const criticalMistakesTotal = db.assessments.reduce((sum, a) => sum + (a.criticalMistakesCount || 0), 0);

  res.json({
    success: true,
    data: {
      totalAttempts: total,
      passedCount: passed,
      failedCount: total - passed,
      passRatePercent: total > 0 ? Math.round((passed / total) * 100) : 0,
      totalCriticalMistakesRecorded: criticalMistakesTotal
    }
  });
};
