package com.safetyar.app.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.safetyar.app.R;
import com.safetyar.app.data.local.entity.WorkerEntity;
import com.safetyar.app.data.repository.TrainingRepository;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityMainBinding;
import com.safetyar.app.domain.model.IndustrySector;
import com.safetyar.app.ui.ar.ArSafetyTrainingActivity;
import com.safetyar.app.ui.ar.FireExplosionArActivity;
import com.safetyar.app.ui.ar.GasConfinedSpaceArActivity;
import com.safetyar.app.ui.assessment.ArHazardAssessmentActivity;
import com.safetyar.app.ui.assessment.AssessmentEngineActivity;
import com.safetyar.app.ui.certificate.CertificateActivity;
import com.safetyar.app.ui.certificate.QrScannerActivity;
import com.safetyar.app.ui.help.HelpActivity;
import com.safetyar.app.ui.history.TrainingHistoryActivity;
import com.safetyar.app.ui.modules.ModuleDetailActivity;
import com.safetyar.app.ui.modules.ModuleLearningActivity;
import com.safetyar.app.ui.profile.WorkerProfileActivity;
import com.safetyar.app.ui.sync.SyncStatusActivity;
import com.safetyar.app.util.LocaleHelper;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_CAMERA = 101;
    private ActivityMainBinding binding;
    private WorkerRepository workerRepository;
    private TrainingRepository trainingRepository;
    private com.safetyar.app.data.repository.SyncRepository syncRepository;

    private WorkerEntity currentWorker;
    private IndustrySector currentSector = IndustrySector.COAL_MINING;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workerRepository = new WorkerRepository(getApplication());
        trainingRepository = new TrainingRepository(getApplication());
        syncRepository = new com.safetyar.app.data.repository.SyncRepository(getApplication());

        setupObservers();
        setupClickListeners();
        checkCameraPermission();
    }

    private void setupObservers() {
        workerRepository.getActiveWorker().observe(this, worker -> {
            if (worker != null) {
                currentWorker = worker;
                binding.tvDashboardWorkerName.setText(worker.getFullName());
                binding.tvDashboardWorkerOrg.setText(worker.getEnterprise());

                try {
                    currentSector = IndustrySector.valueOf(worker.getSector());
                } catch (Exception e) {
                    currentSector = IndustrySector.COAL_MINING;
                }

                // 30-Day Orientation Tracker (Calculated automatically from orientationStartDate)
                int day = worker.calculateOrientationDay();
                int percent = worker.calculateOrientationPercentage();
                int remainingDays = Math.max(0, 30 - day);

                binding.tvOrientationDay.setText(String.format(getString(R.string.orientation_day_format), day));
                binding.tvOrientationPercent.setText(String.format(getString(R.string.orientation_percent_format), percent));
                binding.progressOrientation.setProgress(percent);
                binding.tvOrientationDaysLeft.setText(String.format(getString(R.string.orientation_days_left), remainingDays));

                if (worker.isCertified()) {
                    binding.tvOrientationBadge.setText(getString(R.string.worker_status_certified));
                    binding.tvOrientationBadge.setTextColor(getColor(R.color.hazard_green));
                    binding.tvOrientationBadge.setBackgroundResource(R.drawable.badge_certified);
                } else {
                    binding.tvOrientationBadge.setText(getString(R.string.worker_status_pending));
                    binding.tvOrientationBadge.setTextColor(getColor(R.color.hazard_orange));
                    binding.tvOrientationBadge.setBackgroundResource(R.drawable.badge_warning);
                }

                // Populate Assigned Training Card based on active sector
                if (currentSector == IndustrySector.COAL_MINING) {
                    binding.tvAssignedTitle.setText("Roof Strata Stability & Methane Detection");
                    binding.tvAssignedCode.setText("DGMS Circular 3 of 2019 \u2022 15 Mins Required");
                } else if (currentSector == IndustrySector.STEEL_MANUFACTURING) {
                    binding.tvAssignedTitle.setText("Blast Furnace Tapping & Molten Metal Splash");
                    binding.tvAssignedCode.setText("IS-14489:2018 \u2022 18 Mins Required");
                } else {
                    binding.tvAssignedTitle.setText("Respirable Mica Dust & Silicosis Prevention");
                    binding.tvAssignedCode.setText("DGMS-OCC-H98 \u2022 14 Mins Required");
                }
            }
        });

        // Real-Time Sync Status Observer (Synced / Pending Sync / Sync Failed)
        syncRepository.getSyncStatus().observe(this, status -> {
            if (status != null) {
                binding.tvSyncStatusHeader.setText(status.getLabel());
                if (status == com.safetyar.app.data.repository.SyncRepository.SyncStatus.SYNCED) {
                    binding.tvSyncStatusHeader.setTextColor(getColor(R.color.hazard_green));
                    binding.ivSyncIcon.setColorFilter(getColor(R.color.hazard_green));
                    binding.containerSyncHeader.setBackgroundResource(R.drawable.badge_certified);
                } else if (status == com.safetyar.app.data.repository.SyncRepository.SyncStatus.PENDING_SYNC) {
                    binding.tvSyncStatusHeader.setTextColor(getColor(R.color.hazard_orange));
                    binding.ivSyncIcon.setColorFilter(getColor(R.color.hazard_orange));
                    binding.containerSyncHeader.setBackgroundResource(R.drawable.badge_warning);
                } else {
                    binding.tvSyncStatusHeader.setTextColor(getColor(R.color.hazard_red));
                    binding.ivSyncIcon.setColorFilter(getColor(R.color.hazard_red));
                    binding.containerSyncHeader.setBackgroundResource(R.drawable.badge_warning);
                }
            }
        });
    }

    private void setupClickListeners() {
        // SIH 2026 Primary Demo: AR Module 1 11-Step Microlearning
        binding.btnLaunchSihLearning.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ModuleLearningActivity.class);
            intent.putExtra("MODULE_ID", "MOD-FIRE-01");
            startActivity(intent);
        });

        // SIH 2026 Primary Demo: AR Module 1 Direct AR Simulation
        binding.btnLaunchSihDirectAr.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FireExplosionArActivity.class);
            intent.putExtra("MODULE_ID", "MOD-FIRE-01");
            startActivity(intent);
        });

        // SIH 2026 Primary Demo 2: AR Module 2 Gas Leak & Confined Space AR
        binding.btnLaunchGasConfinedAr.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GasConfinedSpaceArActivity.class);
            intent.putExtra("SECTOR_NAME", currentSector.name());
            startActivity(intent);
        });

        // Continue Assigned Training
        binding.btnContinueAssigned.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ModuleDetailActivity.class);
            String modId = currentWorker != null ? currentWorker.getAssignedModuleId() : "MOD-COAL-01";
            intent.putExtra("MODULE_ID", modId);
            intent.putExtra("SECTOR_NAME", currentSector.name());
            startActivity(intent);
        });

        // AR Practice
        binding.btnNavArTraining.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ArSafetyTrainingActivity.class);
            intent.putExtra("SECTOR_NAME", currentSector.name());
            startActivity(intent);
        });

        // Competency Assessment Exam (Reusable Assessment Engine)
        binding.btnNavAssessment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AssessmentEngineActivity.class);
            String modId = currentWorker != null ? currentWorker.getAssignedModuleId() : "MOD-FIRE-01";
            intent.putExtra("MODULE_ID", modId);
            intent.putExtra("SECTOR_NAME", currentSector.name());
            intent.putExtra("PASS_THRESHOLD", 70);
            startActivity(intent);
        });

        // Digital Safety Pass
        binding.btnNavCertificates.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CertificateActivity.class);
            intent.putExtra("SECTOR_NAME", currentSector.name());
            startActivity(intent);
        });

        // Supervisor QR Scanner
        binding.btnNavSupervisorScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QrScannerActivity.class);
            startActivity(intent);
        });

        // Training History
        binding.btnNavHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrainingHistoryActivity.class);
            startActivity(intent);
        });

        // Emergency & Help
        binding.btnNavHelp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        // Profile
        binding.btnProfileHeader.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WorkerProfileActivity.class);
            startActivity(intent);
        });

        // Sync Status Screen
        binding.containerSyncHeader.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SyncStatusActivity.class);
            startActivity(intent);
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQ_CAMERA
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted for AR & QR scanning.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
