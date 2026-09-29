package com.safetyar.app;

import android.app.Application;
import android.util.Log;

import com.safetyar.app.data.local.AppDatabase;
import com.safetyar.app.data.sync.DataSyncWorker;

public class SafetyApplication extends Application {
    private static final String TAG = "SafetyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Initializing SafetyAR - Industrial Safety Platform (SIH 2026)");

        // Initialize Room database singleton asynchronously
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getDatabase(this);
            Log.i(TAG, "Room Database ready.");
        });

        // Initialize WorkManager periodic background sync
        DataSyncWorker.schedulePeriodicSync(this);
        Log.i(TAG, "WorkManager periodic sync scheduled.");

        // Initialize Network Callback Monitor for immediate sync on reconnect
        com.safetyar.app.util.NetworkMonitor.getInstance(this);
        Log.i(TAG, "NetworkMonitor initialized for offline-first sync.");
    }
}
