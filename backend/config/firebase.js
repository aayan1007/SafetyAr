/**
 * Firebase Admin SDK Driver with Automatic Zero-Setup Local Fallback
 */

const db = require('./database');

let admin = null;
let firestore = null;
let isFirebaseActive = false;

try {
  if (process.env.FIREBASE_PROJECT_ID && process.env.FIREBASE_PRIVATE_KEY && process.env.FIREBASE_CLIENT_EMAIL) {
    admin = require('firebase-admin');
    admin.initializeApp({
      credential: admin.credential.cert({
        projectId: process.env.FIREBASE_PROJECT_ID,
        clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
        privateKey: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, '\n')
      })
    });
    firestore = admin.firestore();
    isFirebaseActive = true;
    console.log('[Firebase] Successfully connected to Cloud Firestore project:', process.env.FIREBASE_PROJECT_ID);
  } else {
    console.log('[Firebase] No Cloud credentials detected. Running in Zero-Setup In-Memory Mode.');
  }
} catch (error) {
  console.warn('[Firebase] Initialization skipped. Using local in-memory store:', error.message);
  isFirebaseActive = false;
}

module.exports = {
  admin,
  firestore,
  isFirebaseActive,
  db
};
