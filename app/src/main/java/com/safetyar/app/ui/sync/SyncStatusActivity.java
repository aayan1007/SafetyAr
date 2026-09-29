package com.safetyar.app.ui.sync;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.safetyar.app.data.local.AppDatabase;
import com.safetyar.app.data.repository.SyncRepository;
import com.safetyar.app.databinding.ActivitySyncStatusBinding;

public class SyncStatusActivity extends AppCompatActivity {

    private ActivitySyncStatusBinding binding;
    private SyncRepository syncRepository;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.safetyar.app.util.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySyncStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        syncRepository = new SyncRepository(getApplication());

        binding.btnBackFromSync.setOnClickListener(v -> finish());

        syncRepository.getPendingCount().observe(this, count -> {
            int pending = count != null ? count : 0;
            binding.tvPendingCount.setText(pending + " Pending Audit Records");
            if (pending == 0) {
                binding.tvSyncStatusMessage.setText("All field evaluations and certificates are synchronized with Central DGMS portal.");
            } else {
                binding.tvSyncStatusMessage.setText(pending + " records cached locally in SQLite/Room. They will sync automatically when network is detected.");
            }
        });

        // Observe Synced, Pending Sync, Sync Failed states
        syncRepository.getSyncStatus().observe(this, status -> {
            if (status != null) {
                binding.tvSyncStateBadge.setText(status.getLabel());
                if (status == SyncRepository.SyncStatus.SYNCED) {
                    binding.tvSyncStateBadge.setTextColor(getColor(com.safetyar.app.R.color.hazard_green));
                    binding.tvSyncStateBadge.setBackgroundResource(com.safetyar.app.R.drawable.badge_certified);
                    binding.cardSyncStatus.setStrokeColor(getColor(com.safetyar.app.R.color.hazard_green));
                } else if (status == SyncRepository.SyncStatus.PENDING_SYNC) {
                    binding.tvSyncStateBadge.setTextColor(getColor(com.safetyar.app.R.color.hazard_orange));
                    binding.tvSyncStateBadge.setBackgroundResource(com.safetyar.app.R.drawable.badge_warning);
                    binding.cardSyncStatus.setStrokeColor(getColor(com.safetyar.app.R.color.hazard_orange));
                } else {
                    binding.tvSyncStateBadge.setTextColor(getColor(com.safetyar.app.R.color.hazard_red));
                    binding.tvSyncStateBadge.setBackgroundResource(com.safetyar.app.R.drawable.badge_warning);
                    binding.cardSyncStatus.setStrokeColor(getColor(com.safetyar.app.R.color.hazard_red));
                }
            }
        });

        binding.btnTriggerSync.setOnClickListener(v -> {
            binding.btnTriggerSync.setEnabled(false);
            binding.btnTriggerSync.setText("Synchronizing with DGMS Portal...");

            AppDatabase.databaseWriteExecutor.execute(() -> {
                boolean success = syncRepository.performSyncNow();
                runOnUiThread(() -> {
                    binding.btnTriggerSync.setEnabled(true);
                    binding.btnTriggerSync.setText("Force Immediate Sync Now");
                    if (success) {
                        Toast.makeText(this, "Local database successfully synchronized!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Sync queued. Ready to dispatch on network restore.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
}
