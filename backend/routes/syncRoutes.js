const express = require('express');
const router = express.Router();
const syncController = require('../controllers/syncController');
const { authenticateToken } = require('../middleware/authMiddleware');

router.post('/upload', syncController.uploadSync);
router.get('/download/:workerId', syncController.downloadUpdates);
router.get('/logs', authenticateToken, syncController.getSyncLogs);

module.exports = router;
