const express = require('express');
const router = express.Router();
const adminController = require('../controllers/adminController');
const { authenticateToken, requireRole } = require('../middleware/authMiddleware');

router.get('/summary', adminController.getSummary);
router.get('/compliance', adminController.getComplianceTable);

module.exports = router;
