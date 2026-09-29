const express = require('express');
const router = express.Router();
const certificateController = require('../controllers/certificateController');
const { authenticateToken, requireRole } = require('../middleware/authMiddleware');

// Public/Field Inspector Verification Endpoint (4 States: VALID, EXPIRED, REVOKED, NOT_FOUND)
router.post('/verify', certificateController.verifyCertificate);

// Certificate Management
router.get('/', authenticateToken, certificateController.getAllCertificates);
router.get('/:id', certificateController.getCertificateById);
router.post('/issue', authenticateToken, requireRole('SUPERVISOR'), certificateController.issueCertificate);
router.patch('/:id/revoke', authenticateToken, requireRole('SUPERVISOR'), certificateController.revokeCertificate);

module.exports = router;
