const express = require('express');
const router = express.Router();
const trainingController = require('../controllers/trainingController');
const { authenticateToken, requireRole } = require('../middleware/authMiddleware');

router.get('/modules', authenticateToken, trainingController.getModules);
router.get('/modules/:id', authenticateToken, trainingController.getModuleById);
router.get('/assignments/:workerId', authenticateToken, trainingController.getAssignments);
router.post('/assignments', authenticateToken, requireRole('SUPERVISOR'), trainingController.createAssignment);

module.exports = router;
