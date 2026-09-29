const db = require('../config/database');

exports.getModules = (req, res) => {
  const { sector } = req.query;
  let list = [...db.modules];
  if (sector) {
    list = list.filter(m => m.sector.toUpperCase() === sector.toUpperCase());
  }

  res.json({
    success: true,
    count: list.length,
    data: list
  });
};

exports.getModuleById = (req, res) => {
  const { id } = req.params;
  const mod = db.modules.find(m => m.moduleId.toUpperCase() === id.toUpperCase());

  if (!mod) {
    return res.status(404).json({ success: false, message: 'Training module not found.' });
  }

  res.json({
    success: true,
    data: mod
  });
};

exports.getAssignments = (req, res) => {
  const { workerId } = req.params;
  const assignments = db.assignments.filter(a => a.workerId.toUpperCase() === workerId.toUpperCase());

  res.json({
    success: true,
    count: assignments.length,
    data: assignments
  });
};

exports.createAssignment = (req, res) => {
  const { workerId, moduleId, priority, daysDue } = req.body;

  if (!workerId || !moduleId) {
    return res.status(400).json({ success: false, message: 'workerId and moduleId are required.' });
  }

  const now = Date.now();
  const due = now + ((daysDue || 7) * 24 * 60 * 60 * 1000);

  const newAssignment = {
    assignmentId: `ASGN-${Date.now().toString().slice(-6)}`,
    workerId,
    moduleId,
    assignedBy: req.user ? req.user.fullName : 'DGMS Safety Directorate',
    assignedTimestamp: now,
    dueTimestamp: due,
    status: 'ASSIGNED',
    priority: priority || 'HIGH'
  };

  db.assignments.push(newAssignment);

  res.status(201).json({
    success: true,
    message: 'Training module assigned successfully.',
    data: newAssignment
  });
};
