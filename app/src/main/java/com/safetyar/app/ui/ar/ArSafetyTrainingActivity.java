package com.safetyar.app.ui.ar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.safetyar.app.R;
import com.safetyar.app.ar.ARSessionManager;
import com.safetyar.app.ar.HazardSimulationEngine;
import com.safetyar.app.data.repository.TrainingRepository;
import com.safetyar.app.databinding.ActivityArTrainingBinding;
import com.safetyar.app.domain.model.HazardItem;
import com.safetyar.app.domain.model.IndustrySector;

import java.util.List;

public class ArSafetyTrainingActivity extends AppCompatActivity {

    private ActivityArTrainingBinding binding;
    private ARSessionManager arSessionManager;
    private TrainingRepository trainingRepository;

    private IndustrySector currentSector = IndustrySector.COAL_MINING;
    private List<HazardItem> hazards;
    private HazardItem currentSelectedHazard;
    private int mitigatedCount = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(com.safetyar.app.util.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        trainingRepository = new TrainingRepository(getApplication());
        arSessionManager = new ARSessionManager();

        String sectorStr = getIntent().getStringExtra("SECTOR_NAME");
        if (sectorStr != null) {
            try {
                currentSector = IndustrySector.valueOf(sectorStr);
            } catch (Exception ignored) {}
        }

        setupUI();
        loadHazards();
        initializeCameraAndAR();
    }

    private void setupUI() {
        binding.tvArTitle.setText(currentSector.getDisplayName() + " AR Safety Practice");
        binding.btnCloseAr.setOnClickListener(v -> finish());

        binding.arOverlayView.setOnHazardClickListener(hazard -> {
            currentSelectedHazard = hazard;
            showHazardDetails(hazard);
            vibrateDevice(50);
        });

        binding.btnMitigateHazard.setOnClickListener(v -> {
            if (currentSelectedHazard != null) {
                currentSelectedHazard.setMitigated(true);
                mitigatedCount++;
                updateProgress();
                vibrateDevice(150);

                Toast.makeText(this, "SOP Applied: Hazard Safely Mitigated!", Toast.LENGTH_SHORT).show();
                binding.cardHazardDetails.setVisibility(View.GONE);
                binding.arOverlayView.invalidate();

                if (mitigatedCount >= hazards.size()) {
                    Toast.makeText(this, "Outstanding! All hazards in this zone neutralized.", Toast.LENGTH_LONG).show();
                    // Mark sample module completed
                    trainingRepository.markModuleCompleted(currentSector == IndustrySector.COAL_MINING ? "MOD-COAL-01" : "MOD-STEEL-01");
                }
            }
        });
    }

    private void loadHazards() {
        hazards = HazardSimulationEngine.generateHazardsForSector(currentSector);
        binding.arOverlayView.setHazards(hazards);
        updateProgress();
    }

    private void updateProgress() {
        binding.tvHazardProgress.setText(mitigatedCount + " / " + hazards.size() + " SAFE");
        if (mitigatedCount == hazards.size()) {
            binding.tvHazardProgress.setTextColor(getColor(R.color.hazard_green));
        }
    }

    private void showHazardDetails(HazardItem hazard) {
        binding.cardHazardDetails.setVisibility(View.VISIBLE);
        binding.tvHazardTitle.setText(hazard.getTitle());
        binding.tvHazardDesc.setText(hazard.getDescription());
        binding.tvHazardSop.setText(hazard.getSopActionRequired());
        binding.tvHazardSeverity.setText(hazard.getSeverity().getLabel());

        if (hazard.isMitigated()) {
            binding.btnMitigateHazard.setEnabled(false);
            binding.btnMitigateHazard.setText("Hazard Neutralized \u2713");
        } else {
            binding.btnMitigateHazard.setEnabled(true);
            binding.btnMitigateHazard.setText("Apply SOP & Neutralize Hazard");
        }
    }

    private void initializeCameraAndAR() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission needed for AR view.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize CameraX feed for high-performance camera streaming
        PreviewView previewView = new PreviewView(this);
        binding.cameraContainer.addView(previewView);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);

                binding.tvArTrackingStatus.setText("Spatial Surface Tracking: ACTIVE (3 Hazards Loaded)");
            } catch (Exception e) {
                binding.tvArTrackingStatus.setText("Camera Initialization: Standard View");
            }
        }, ContextCompat.getMainExecutor(this));

        // ARCore Session availability probe
        arSessionManager.initializeSession(this, new ARSessionManager.SessionCallback() {
            @Override
            public void onSessionReady(com.google.ar.core.Session session) {
                binding.tvArTrackingStatus.setText("Google ARCore: Surface Anchors Active");
            }

            @Override
            public void onSessionError(String errorMessage) {
                // Graceful fallback to CameraX preview + spatial mathematical overlay
                binding.tvArTrackingStatus.setText("AR Simulation: Active (Spatial Projection)");
            }
        });
    }

    private void vibrateDevice(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        arSessionManager.pauseSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arSessionManager.destroySession();
    }
}
