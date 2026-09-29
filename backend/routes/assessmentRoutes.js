const express = require('express');
const router = express.Router();
const assessmentController = require('../controllers/assessmentController');
const { authenticateToken } = require('../middleware/authMiddleware');

router.post('/submit', authenticateToken, assessmentController.submitAssessment);
router.get('/worker/:workerId', authenticateToken, assessmentController.getWorkerAssessments);
router.get('/stats', authenticateToken, assessmentController.getStats);

module.exports = router;
