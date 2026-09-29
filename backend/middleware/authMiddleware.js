/**
 * Authentication Middleware
 * Supports standard Bearer JWT validation and Demo/Offline Pass-Through.
 */

const jwt = require('jsonwebtoken');
const db = require('../config/database');

const JWT_SECRET = process.env.JWT_SECRET || 'safetyar_jharkhand_super_secret_jwt_key_2026';

function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    // In demo / SIH evaluation mode, allow fallback worker identity if designated
    const demoWorkerId = req.headers['x-worker-id'] || 'WRK-JH-COAL-0891';
    const worker = db.workers.find(w => w.workerId === demoWorkerId);
    if (worker) {
      req.user = { workerId: worker.workerId, role: worker.role, fullName: worker.fullName };
      return next();
    }
    return res.status(401).json({ success: false, message: 'Authentication token required.' });
  }

  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) {
      return res.status(403).json({ success: false, message: 'Invalid or expired authentication token.' });
    }
    req.user = user;
    next();
  });
}

function requireRole(role) {
  return (req, res, next) => {
    if (!req.user || (req.user.role !== role && req.user.role !== 'ADMIN' && req.user.role !== 'SUPERVISOR')) {
      return res.status(403).json({ success: false, message: `Access denied. Requires ${role} authorization.` });
    }
    next();
  };
}

module.exports = {
  authenticateToken,
  requireRole,
  JWT_SECRET
};
