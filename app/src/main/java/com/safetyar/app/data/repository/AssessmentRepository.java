package com.safetyar.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.safetyar.app.data.local.AppDatabase;
import com.safetyar.app.data.local.dao.AssessmentDao;
import com.safetyar.app.data.local.dao.AssessmentAttemptDao;
import com.safetyar.app.data.local.dao.CertificateDao;
import com.safetyar.app.data.local.dao.SyncQueueDao;
import com.safetyar.app.data.local.entity.AssessmentRecordEntity;
import com.safetyar.app.data.local.entity.CertificateEntity;
import com.safetyar.app.data.local.entity.SyncQueueEntity;
import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

public class AssessmentRepository {
    private final AssessmentDao assessmentDao;
    private final AssessmentAttemptDao assessmentAttemptDao;
    private final CertificateDao certificateDao;
    private final SyncQueueDao syncQueueDao;
    private final Gson gson = new Gson();

    public AssessmentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        assessmentDao = db.assessmentDao();
        assessmentAttemptDao = db.assessmentAttemptDao();
        certificateDao = db.certificateDao();
        syncQueueDao = db.syncQueueDao();
    }

    public LiveData<List<AssessmentRecordEntity>> getAllAssessments() {
        return assessmentDao.getAllAssessments();
    }

    public LiveData<CertificateEntity> getLatestCertificate(String workerId) {
        return certificateDao.getLatestCertificateByWorker(workerId);
    }

    public LiveData<CertificateEntity> getCertificateById(String certId) {
        return certificateDao.getCertificateById(certId);
    }

    public void saveAssessmentAndIssueCertificate(
            AssessmentRecordEntity record,
            CertificateEntity certificate,
            OnAssessmentSavedCallback callback) {

        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 1. Insert assessment record
            assessmentDao.insertAssessment(record);

            // 2. Queue for offline synchronization
            SyncQueueEntity syncAssessment = new SyncQueueEntity(
                    UUID.randomUUID().toString(),
                    "ASSESSMENT",
                    record.getAssessmentId(),
                    gson.toJson(record),
                    System.currentTimeMillis(),
                    0,
                    "PENDING"
            );
            syncQueueDao.enqueue(syncAssessment);

            // 3. If passed, insert certificate and queue
            if (certificate != null && record.isPassed()) {
                certificateDao.insertCertificate(certificate);

                SyncQueueEntity syncCert = new SyncQueueEntity(
                        UUID.randomUUID().toString(),
                        "CERTIFICATE",
                        certificate.getCertificateId(),
                        gson.toJson(certificate),
                        System.currentTimeMillis(),
                        0,
                        "PENDING"
                );
                syncQueueDao.enqueue(syncCert);
            }

            if (callback != null) {
                callback.onSaved();
            }
        });
    }

    public void saveAttemptWithAnswers(
            com.safetyar.app.data.local.entity.AssessmentAttemptEntity attempt,
            List<com.safetyar.app.data.local.entity.AnswerEntity> answers,
            OnAssessmentSavedCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            assessmentAttemptDao.insertAttempt(attempt);
            if (answers != null && !answers.isEmpty()) {
                assessmentAttemptDao.insertAnswers(answers);
            }

            SyncQueueEntity syncAttempt = new SyncQueueEntity(
                    UUID.randomUUID().toString(),
                    "ASSESSMENT_ATTEMPT",
                    attempt.getAttemptId(),
                    gson.toJson(attempt),
                    System.currentTimeMillis(),
                    0,
                    "PENDING"
            );
            syncQueueDao.enqueue(syncAttempt);

            if (callback != null) {
                callback.onSaved();
            }
        });
    }

    public LiveData<List<com.safetyar.app.data.local.entity.AssessmentAttemptEntity>> getAttemptsForWorker(String workerId) {
        return assessmentAttemptDao.getAttemptsForWorker(workerId);
    }

    public interface OnAssessmentSavedCallback {
        void onSaved();
    }
}
