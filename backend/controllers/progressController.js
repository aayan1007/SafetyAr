const db = require('../config/database');

exports.getProgress = (req, res) => {
  const { workerId, moduleId } = req.params;

  const item = db.progress.find(
    p => p.workerId.toUpperCase() === workerId.toUpperCase() &&
         p.moduleId.toUpperCase() === moduleId.toUpperCase()
  );

  if (!item) {
    return res.json({
      success: true,
      data: {
        workerId,
        moduleId,
        currentStep: 1,
        completed: false,
        percentComplete: 0,
        lastUpdated: Date.now()
      }
    });
  }

  res.json({
    success: true,
    data: item
  });
};

exports.updateProgress = (req, res) => {
  const { workerId, moduleId, currentStep, percentComplete, completed } = req.body;

  if (!workerId || !moduleId) {
    return res.status(400).json({ success: false, message: 'workerId and moduleId are required.' });
  }

  let item = db.progress.find(
    p => p.workerId.toUpperCase() === workerId.toUpperCase() &&
         p.moduleId.toUpperCase() === moduleId.toUpperCase()
  );

  const now = Date.now();
  if (item) {
    if (currentStep !== undefined) item.currentStep = currentStep;
    if (percentComplete !== undefined) item.percentComplete = percentComplete;
    if (completed !== undefined) item.completed = Boolean(completed);
    item.lastUpdated = now;
  } else {
    item = {
      workerId,
      moduleId,
      currentStep: currentStep || 1,
      completed: Boolean(completed),
      percentComplete: percentComplete || 0,
      lastUpdated: now
    };
    db.progress.push(item);
  }

  // Update assignment status if completed
  if (item.completed) {
    const assignment = db.assignments.find(
      a => a.workerId.toUpperCase() === workerId.toUpperCase() &&
           a.moduleId.toUpperCase() === moduleId.toUpperCase()
    );
    if (assignment) assignment.status = 'COMPLETED';
  }

  res.json({
    success: true,
    message: 'Training progress synchronized.',
    data: item
  });
};
