package com.safetyar.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.safetyar.app.data.local.AppDatabase;
import com.safetyar.app.data.local.dao.WorkerDao;
import com.safetyar.app.data.local.entity.WorkerEntity;

import java.util.List;

public class WorkerRepository {
    private final WorkerDao workerDao;
    private final LiveData<WorkerEntity> activeWorker;
    private final LiveData<List<WorkerEntity>> allWorkers;

    public WorkerRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        workerDao = db.workerDao();
        activeWorker = workerDao.getActiveWorker();
        allWorkers = workerDao.getAllWorkers();
    }

    public LiveData<WorkerEntity> getActiveWorker() {
        return activeWorker;
    }

    public LiveData<List<WorkerEntity>> getAllWorkers() {
        return allWorkers;
    }

    public void updateWorker(WorkerEntity worker) {
        AppDatabase.databaseWriteExecutor.execute(() -> workerDao.updateWorker(worker));
    }

    public void markWorkerCertified(String workerId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            WorkerEntity worker = workerDao.getWorkerByIdSync(workerId);
            if (worker != null) {
                worker.setCertified(true);
                worker.setLastAssessmentTimestamp(System.currentTimeMillis());
                workerDao.updateWorker(worker);
            }
        });
    }

    public void switchActiveWorker(String workerId, Runnable onComplete) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            workerDao.switchActiveWorker(workerId);
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public void loginWithId(String idOrNationalId, LoginResultCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            WorkerEntity worker = workerDao.findByNationalOrWorkerIdSync(idOrNationalId);
            if (worker != null) {
                workerDao.switchActiveWorker(worker.getWorkerId());
                if (callback != null) callback.onSuccess(worker);
            } else {
                if (callback != null) callback.onError("Worker ID not found in local or national database.");
            }
        });
    }

    public interface LoginResultCallback {
        void onSuccess(WorkerEntity worker);
        void onError(String error);
    }
}
