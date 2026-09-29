package com.safetyar.app.data.sync;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.safetyar.app.data.repository.SyncRepository;

import java.util.concurrent.TimeUnit;

public class DataSyncWorker extends Worker {
    private static final String TAG = "DataSyncWorker";
    public static final String UNIQUE_PERIODIC_WORK = "SafetyArBackgroundSync";
    public static final String UNIQUE_IMMEDIATE_WORK = "SafetyArImmediateSync";

    public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Executing background safety compliance sync...");
        try {
            SyncRepository repo = new SyncRepository((Application) getApplicationContext());
            boolean success = repo.performSyncNow();
            if (success) {
                Log.d(TAG, "Background sync successfully completed.");
                return Result.success();
            } else {
                Log.w(TAG, "Network unavailable or sync queued. Retrying later.");
                return Result.retry();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in DataSyncWorker: " + e.getMessage(), e);
            return Result.failure();
        }
    }

    public static void schedulePeriodicSync(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                DataSyncWorker.class,
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
    }

    public static void scheduleImmediateSync(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest oneTimeRequest = new OneTimeWorkRequest.Builder(DataSyncWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_IMMEDIATE_WORK,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
        );
    }
}
