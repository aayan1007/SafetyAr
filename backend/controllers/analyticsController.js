const db = require('../config/database');

exports.getSectorMatrix = (req, res) => {
  const sectors = ['COAL_MINING', 'STEEL_MANUFACTURING', 'MICA_PROCESSING'];

  const matrix = sectors.map(sector => {
    const workers = db.workers.filter(w => w.sector === sector && w.role === 'WORKER');
    const certified = workers.filter(w => w.isCertified);
    const certs = db.certificates.filter(c => c.sector === sector);
    const validCerts = certs.filter(c => c.status === 'VALID');

    return {
      sector,
      displayName: sector.replace('_', ' '),
      totalWorkers: workers.length,
      certifiedWorkers: certified.length,
      complianceRate: workers.length > 0 ? Math.round((certified.length / workers.length) * 100) : 0,
      activeCertificates: validCerts.length,
      expiredCertificates: certs.filter(c => c.status === 'EXPIRED').length,
      revokedCertificates: certs.filter(c => c.status === 'REVOKED').length
    };
  });

  res.json({
    success: true,
    data: matrix
  });
};

exports.getCriticalErrors = (req, res) => {
  // Common industrial critical safety infractions
  const errorDistribution = [
    {
      errorType: 'Premature Entry Before Atmospheric Gas Testing',
      sector: 'COAL_MINING',
      dgmsRule: 'DGMS Circular 3 of 2019 / Standard 54',
      occurrenceCount: 14,
      severity: 'CRITICAL_FATALITY_RISK'
    },
    {
      errorType: 'Inappropriate PPE Selection (Dust mask in toxic gas zone)',
      sector: 'COAL_MINING',
      dgmsRule: 'DGMS Respiratory Code 22',
      occurrenceCount: 9,
      severity: 'HIGH_RISK'
    },
    {
      errorType: 'Failure to Station Standby Buddy with Lifeline',
      sector: 'COAL_MINING',
      dgmsRule: 'DGMS Confined Space Directive 2024',
      occurrenceCount: 7,
      severity: 'CRITICAL_FATALITY_RISK'
    },
    {
      errorType: 'Failure to Check Slag Moisture Before Molten Pouring',
      sector: 'STEEL_MANUFACTURING',
      dgmsRule: 'IS-14489:2018 Metal Splash Protocol',
      occurrenceCount: 5,
      severity: 'CRITICAL_EXPLOSION_RISK'
    },
    {
      errorType: 'Dry Sweeping Respirable Mica Flake Dust Without Water Misting',
      sector: 'MICA_PROCESSING',
      dgmsRule: 'DGMS Silicosis Health Code H98',
      occurrenceCount: 12,
      severity: 'CHRONIC_OCCUPATIONAL_HAZARD'
    }
  ];

  res.json({
    success: true,
    totalErrorsAnalyzed: errorDistribution.reduce((acc, curr) => acc + curr.occurrenceCount, 0),
    data: errorDistribution
  });
};
