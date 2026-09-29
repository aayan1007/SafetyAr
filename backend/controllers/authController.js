const jwt = require('jsonwebtoken');
const db = require('../config/database');
const { JWT_SECRET } = require('../middleware/authMiddleware');

exports.login = (req, res) => {
  const { workerId, passcode } = req.body;

  if (!workerId) {
    return res.status(400).json({ success: false, message: 'workerId is required.' });
  }

  const worker = db.workers.find(w => w.workerId.toUpperCase() === workerId.toUpperCase());
  if (!worker) {
    return res.status(404).json({ success: false, message: 'Worker ID not found in Jharkhand safety registry.' });
  }

  // Simple passcheck (or demo 1234 / 9999)
  if (passcode && worker.passcode !== passcode && passcode !== '1234') {
    return res.status(401).json({ success: false, message: 'Invalid safety passcode.' });
  }

  worker.lastActiveTimestamp = Date.now();

  const token = jwt.sign(
    {
      workerId: worker.workerId,
      role: worker.role,
      fullName: worker.fullName,
      sector: worker.sector,
      enterprise: worker.enterprise
    },
    JWT_SECRET,
    { expiresIn: '30d' }
  );

  res.json({
    success: true,
    message: 'Worker authenticated successfully.',
    data: {
      token,
      worker: {
        workerId: worker.workerId,
        fullName: worker.fullName,
        role: worker.role,
        designation: worker.designation,
        sector: worker.sector,
        enterprise: worker.enterprise,
        division: worker.division,
        isCertified: worker.isCertified,
        complianceStatus: worker.complianceStatus,
        orientationDay: worker.orientationDay,
        assignedModuleId: worker.assignedModuleId
      }
    }
  });
};

exports.verifyToken = (req, res) => {
  res.json({
    success: true,
    message: 'Token is valid.',
    data: req.user
  });
};
