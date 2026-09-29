/**
 * SafetyAR Central Safety & Certification REST API
 * Smart India Hackathon 2026 - Government of Jharkhand & DGMS Compliance Bureau
 */

require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const path = require('path');

const { isFirebaseActive } = require('./config/firebase');
const errorHandler = require('./middleware/errorHandler');

// Route Imports
const authRoutes = require('./routes/authRoutes');
const workerRoutes = require('./routes/workerRoutes');
const trainingRoutes = require('./routes/trainingRoutes');
const progressRoutes = require('./routes/progressRoutes');
const assessmentRoutes = require('./routes/assessmentRoutes');
const certificateRoutes = require('./routes/certificateRoutes');
const syncRoutes = require('./routes/syncRoutes');
const adminRoutes = require('./routes/adminRoutes');
const analyticsRoutes = require('./routes/analyticsRoutes');

const app = express();
const PORT = process.env.PORT || 5000;

// Security & Middleware Configuration
app.use(helmet({
  contentSecurityPolicy: false // Allows dashboard inline script execution and Chart.js CDN
}));
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PATCH', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization', 'x-worker-id']
}));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

// Serve Public Web Assets & Admin Dashboard
app.use(express.static(path.join(__dirname, 'public')));

// Root Route -> Serves Admin Web Dashboard
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Health Check Endpoint
app.get('/api/health', (req, res) => {
  res.json({
    status: 'HEALTHY',
    service: 'SafetyAR Central REST API',
    jurisdiction: 'State Industrial Safety Council (JISC), Jharkhand',
    mode: isFirebaseActive ? 'FIREBASE_FIRESTORE' : 'IN_MEMORY_PRE_SEEDED',
    uptimeSeconds: Math.floor(process.uptime()),
    timestamp: Date.now()
  });
});

// Mount V1 REST API Routes
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/workers', workerRoutes);
app.use('/api/v1/training', trainingRoutes);
app.use('/api/v1/progress', progressRoutes);
app.use('/api/v1/assessments', assessmentRoutes);
app.use('/api/v1/certificates', certificateRoutes);
app.use('/api/v1/sync', syncRoutes);
app.use('/api/v1/admin', adminRoutes);
app.use('/api/v1/analytics', analyticsRoutes);

// Direct Aliases for Android Retrofit SafetyApiService compatibility
app.get('/api/v1/workers/:id/status', (req, res) => {
  const db = require('./config/database');
  const worker = db.workers.find(w => w.workerId.toUpperCase() === req.params.id.toUpperCase());
  if (!worker) {
    return res.status(404).json({ success: false, message: 'Worker not found' });
  }
  res.json({
    success: true,
    data: worker.complianceStatus
  });
});

app.get('/api/v1/certificates/verify/:hash', (req, res) => {
  const db = require('./config/database');
  const cert = db.certificates.find(c =>
    (c.verificationToken && c.verificationToken.toLowerCase() === req.params.hash.toLowerCase()) ||
    c.certificateId.toUpperCase() === req.params.hash.toUpperCase()
  );
  const isValid = cert && cert.status === 'VALID' && Date.now() <= cert.expiresTimestamp;
  res.json({
    success: true,
    data: Boolean(isValid)
  });
});

// 404 Route Catch-All
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: `Endpoint ${req.method} ${req.url} does not exist.`
  });
});

// Central Error Handler
app.use(errorHandler);

// Start Server
if (process.env.NODE_ENV !== 'test') {
  app.listen(PORT, () => {
    console.log('====================================================');
    console.log(`🚀 SafetyAR REST API Server running on port ${PORT}`);
    console.log(`🌐 Web Admin Dashboard: http://localhost:${PORT}/`);
    console.log(`🩺 Health Check: http://localhost:${PORT}/api/health`);
    console.log(`📋 API Docs: http://localhost:${PORT}/api-docs`);
    console.log('====================================================');
  });
}

module.exports = app;
