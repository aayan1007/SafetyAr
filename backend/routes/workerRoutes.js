const express = require('express');
const router = express.Router();
const workerController = require('../controllers/workerController');
const { authenticateToken, requireRole } = require('../middleware/authMiddleware');

router.get('/', authenticateToken, workerController.getWorkers);
router.get('/:id', authenticateToken, workerController.getWorkerById);
router.post('/', authenticateToken, requireRole('SUPERVISOR'), workerController.createWorker);
router.patch('/:id/status', authenticateToken, requireRole('SUPERVISOR'), workerController.updateCompliance);

module.exports = router;
