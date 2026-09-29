const express = require('express');
const router = express.Router();
const progressController = require('../controllers/progressController');
const { authenticateToken } = require('../middleware/authMiddleware');

router.get('/:workerId/:moduleId', authenticateToken, progressController.getProgress);
router.post('/update', authenticateToken, progressController.updateProgress);

module.exports = router;
