const express = require('express');
const router = express.Router();
const analyticsController = require('../controllers/analyticsController');
const { authenticateToken } = require('../middleware/authMiddleware');

router.get('/sector-matrix', analyticsController.getSectorMatrix);
router.get('/critical-errors', analyticsController.getCriticalErrors);

module.exports = router;
