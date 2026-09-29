package com.safetyar.app.ui.ar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ar.core.ArCoreApk;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.safetyar.app.R;
import com.safetyar.app.ar.ARSessionManager;
import com.safetyar.app.ar.GasConfinedSpaceOverlayView;
import com.safetyar.app.ar.GasConfinedSpaceStateManager;
import com.safetyar.app.data.local.entity.AssessmentRecordEntity;
import com.safetyar.app.data.local.entity.CertificateEntity;
import com.safetyar.app.data.repository.AssessmentRepository;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityGasConfinedSpaceArBinding;
import com.safetyar.app.ui.certificate.CertificateActivity;
import com.safetyar.app.util.LocaleHelper;
import com.safetyar.app.util.QrPassGenerator;

import java.util.UUID;

public class GasConfinedSpaceArActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_CAMERA = 201;

    private ActivityGasConfinedSpaceArBinding binding;
    private GasConfinedSpaceStateManager stateManager;
    private ARSessionManager arSessionManager;
    private AssessmentRepository assessmentRepository;
    private WorkerRepository workerRepository;

    private String activeWorkerId = "WRK-JH-COAL-0891";
    private String activeWorkerName = "Ramesh Soren";
    private String currentSector = "COAL_MINING";
    private boolean isArCoreAvailable = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGasConfinedSpaceArBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        stateManager = new GasConfinedSpaceStateManager();
        arSessionManager = new ARSessionManager();
        assessmentRepository = new AssessmentRepository(getApplication());
        workerRepository = new WorkerRepository(getApplication());

        if (getIntent().hasExtra("SECTOR_NAME")) {
            currentSector = getIntent().getStringExtra("SECTOR_NAME");
        }

        workerRepository.getActiveWorker().observe(this, worker -> {
            if (worker != null) {
                activeWorkerId = worker.getWorkerId();
                activeWorkerName = worker.getFullName();
            }
        });

        binding.gasOverlayView.setStateManager(stateManager);

        setupClickListeners();
        checkArCoreAndInitialize();
        updateUI();
    }

    private void checkArCoreAndInitialize() {
        // 1. Check ARCore Availability (Section 10)
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isSupported()) {
            isArCoreAvailable = true;
            binding.cardArFallbackBanner.setVisibility(View.GONE);
        } else {
            // Graceful Fallback Mode: Do NOT crash!
            isArCoreAvailable = false;
            binding.cardArFallbackBanner.setVisibility(View.VISIBLE);
            // In fallback mode, auto-anchor scenario in center of display
            binding.gasOverlayView.post(() -> {
                binding.gasOverlayView.placeScenarioAt(
                        binding.gasOverlayView.getWidth() / 2f,
                        binding.gasOverlayView.getHeight() / 2f
                );
            });
        }

        // 2. Request Camera Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQ_CAMERA);
        } else {
            startCameraPreview();
        }
    }

    private void startCameraPreview() {
        PreviewView previewView = new PreviewView(this);
        binding.gasCameraContainer.removeAllViews();
        binding.gasCameraContainer.addView(previewView);

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
            } catch (Exception ignored) {}
        }, ContextCompat.getMainExecutor(this));

        if (isArCoreAvailable) {
            arSessionManager.initializeSession(this, new ARSessionManager.SessionCallback() {
                @Override
                public void onSessionReady(com.google.ar.core.Session session) {}

                @Override
                public void onSessionError(String errorMessage) {
                    // Fallback seamlessly on session failure
                    runOnUiThread(() -> binding.cardArFallbackBanner.setVisibility(View.VISIBLE));
                }
            });
        }
    }

    private void setupClickListeners() {
        binding.btnBackFromGasAr.setOnClickListener(v -> finish());

        // Restart / Retry Scenario
        binding.btnRestartGasScenario.setOnClickListener(v -> {
            stateManager.reset();
            binding.gasOverlayView.invalidate();
            updateUI();
            binding.tvGasImmediateFeedback.setText("Scenario reset. Pan camera to identify the gas leak flange.");
            binding.cardGasFeedbackBanner.setStrokeColor(getColor(R.color.safety_primary));
            binding.tvGasImmediateFeedback.setTextColor(getColor(R.color.text_on_dark));
            binding.tvGasScoreBadge.setText("SCORE: 100");
        });

        // Direct Touch on Spatial Objects
        binding.gasOverlayView.setOnGasScenarioObjectClickListener(new GasConfinedSpaceOverlayView.OnGasScenarioObjectClickListener() {
            @Override
            public void onPlaneTapped(float x, float y) {
                binding.gasOverlayView.placeScenarioAt(x, y);
                vibrateDevice(50);
                Toast.makeText(GasConfinedSpaceArActivity.this, "Safety scenario anchored to physical plane.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGasLeakTapped() {
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_TAP_GAS_LEAK);
            }

            @Override
            public void onDangerZoneTapped() {
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_TAP_DANGER_ZONE);
            }

            @Override
            public void onConfinedSpaceTapped() {
                if (stateManager.getCurrentStep() == GasConfinedSpaceStateManager.Step.STEP_4_IDENTIFY_CONFINED_SPACE) {
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_TAP_CONFINED_ENTRANCE);
                } else if (!stateManager.isGasTested() || !stateManager.isPermitAuthorized()) {
                    // Attempting entry without testing triggers unsafe penalty
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_UNSAFE_PREMATURE_ENTRY);
                } else {
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_SAFE_ENTRY);
                }
            }

            @Override
            public void onPpeStationTapped() {
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_SELECT_SCBA_PPE);
            }

            @Override
            public void onGasDetectorTapped() {
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_USE_GAS_DETECTOR);
            }

            @Override
            public void onBuddyTapped() {
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_VERIFY_BUDDY);
            }

            @Override
            public void onPermitTapped() {
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_VERIFY_PERMIT);
            }

            @Override
            public void onEvacuationRouteTapped() {
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_EMERGENCY_EVACUATE);
            }
        });

        // PPE Selector
        binding.btnSelectScba.setOnClickListener(v ->
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_SELECT_SCBA_PPE));

        binding.btnSelectDustMask.setOnClickListener(v ->
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_SELECT_DUST_MASK));

        // Buddy Selector
        binding.btnVerifyBuddy.setOnClickListener(v ->
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_VERIFY_BUDDY));

        binding.btnSoloEntry.setOnClickListener(v ->
                executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_SOLO_ENTRY_ATTEMPT));

        // Contextual Action Button
        binding.btnGasContextualAction.setOnClickListener(v -> {
            switch (stateManager.getCurrentStep()) {
                case STEP_1_IDENTIFY_GAS_LEAK:
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_TAP_GAS_LEAK);
                    break;
                case STEP_2_IDENTIFY_DANGER_ZONE:
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_TAP_DANGER_ZONE);
                    break;
                case STEP_3_AVOID_DANGER_ZONE:
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_ESTABLISH_SAFE_STANDOFF);
                    break;
                case STEP_4_IDENTIFY_CONFINED_SPACE:
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_TAP_CONFINED_ENTRANCE);
                    break;
                case STEP_6_USE_GAS_DETECTOR:
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_USE_GAS_DETECTOR);
                    break;
                case STEP_8_VERIFY_AUTHORIZATION:
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_VERIFY_PERMIT);
                    break;
                case STEP_9_SAFE_ENTRY_PROCEDURE:
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_SAFE_ENTRY);
                    break;
                case STEP_10_EMERGENCY_EVACUATION:
                    executeAction(GasConfinedSpaceStateManager.UserAction.ACTION_EMERGENCY_EVACUATE);
                    break;
                default:
                    break;
            }
        });
    }

    private void executeAction(GasConfinedSpaceStateManager.UserAction action) {
        GasConfinedSpaceStateManager.StepResult result = stateManager.processAction(action);

        if (result.isCorrect) {
            vibrateDevice(60);
            binding.cardGasFeedbackBanner.setStrokeColor(getColor(R.color.hazard_green));
            binding.tvGasImmediateFeedback.setTextColor(getColor(R.color.hazard_green));
        } else {
            vibrateDevice(250);
            binding.cardGasFeedbackBanner.setStrokeColor(getColor(R.color.hazard_red));
            binding.tvGasImmediateFeedback.setTextColor(getColor(R.color.hazard_red));
        }

        binding.tvGasImmediateFeedback.setText(result.feedbackMessage);
        binding.tvGasScoreBadge.setText("SCORE: " + result.score);
        binding.gasOverlayView.invalidate();

        updateUI();

        if (result.isFinished) {
            onScenarioCompleted(result.passed, result.score);
        }
    }

    private void updateUI() {
        GasConfinedSpaceStateManager.Step step = stateManager.getCurrentStep();
        binding.tvGasCurrentStep.setText("Step " + step.getStepNumber() + " of 10: " + step.getTitle());

        if (step == GasConfinedSpaceStateManager.Step.STEP_5_SELECT_PPE) {
            binding.layoutGasPpeSelection.setVisibility(View.VISIBLE);
            binding.layoutGasBuddySelection.setVisibility(View.GONE);
            binding.btnGasContextualAction.setVisibility(View.GONE);
        } else if (step == GasConfinedSpaceStateManager.Step.STEP_7_FOLLOW_BUDDY_SYSTEM) {
            binding.layoutGasPpeSelection.setVisibility(View.GONE);
            binding.layoutGasBuddySelection.setVisibility(View.VISIBLE);
            binding.btnGasContextualAction.setVisibility(View.GONE);
        } else {
            binding.layoutGasPpeSelection.setVisibility(View.GONE);
            binding.layoutGasBuddySelection.setVisibility(View.GONE);
            binding.btnGasContextualAction.setVisibility(View.VISIBLE);

            switch (step) {
                case STEP_1_IDENTIFY_GAS_LEAK:
                    binding.btnGasContextualAction.setText("1. Tap Gas Leak Flange to Identify");
                    break;
                case STEP_2_IDENTIFY_DANGER_ZONE:
                    binding.btnGasContextualAction.setText("2. Identify Red Gas Danger Zone");
                    break;
                case STEP_3_AVOID_DANGER_ZONE:
                    binding.btnGasContextualAction.setText("3. Establish 10m Safe Upwind Standoff");
                    break;
                case STEP_4_IDENTIFY_CONFINED_SPACE:
                    binding.btnGasContextualAction.setText("4. Locate Confined Space Entrance");
                    break;
                case STEP_6_USE_GAS_DETECTOR:
                    binding.btnGasContextualAction.setText("6. Test Atmospheric Levels (4-Gas Detector)");
                    break;
                case STEP_8_VERIFY_AUTHORIZATION:
                    binding.btnGasContextualAction.setText("8. Verify Signed Entry Permit Form-IV");
                    break;
                case STEP_9_SAFE_ENTRY_PROCEDURE:
                    binding.btnGasContextualAction.setText("9. Execute Tethered Lifeline Entry");
                    break;
                case STEP_10_EMERGENCY_EVACUATION:
                    binding.btnGasContextualAction.setText("10. Evacuate Along Escape Route to Muster Area \u2192");
                    break;
                default:
                    break;
            }
        }
    }

    private void onScenarioCompleted(boolean passed, int finalScore) {
        String attemptId = "ATT-GAS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        AssessmentRecordEntity record = new AssessmentRecordEntity(
                attemptId,
                activeWorkerId,
                "MOD-GAS-01",
                currentSector,
                finalScore,
                10,
                10,
                10,
                passed,
                System.currentTimeMillis(),
                55L,
                false
        );

        CertificateEntity cert = null;
        if (passed) {
            String certId = QrPassGenerator.generateUniqueCertificateId();
            String organization = "Bharat Coking Coal Limited (BCCL)";
            String moduleName = "Gas Leak & Confined Space Safety";
            long issuedAt = System.currentTimeMillis();
            long expiresAt = issuedAt + (365L * 24 * 60 * 60 * 1000L);
            String status = "VALID";
            String issuer = "Jharkhand Industrial Safety Council (JISC)";

            String token = QrPassGenerator.computeVerificationToken(
                    certId, activeWorkerId, organization, moduleName, finalScore, issuedAt, expiresAt);
            String payload = QrPassGenerator.buildQrPayload(
                    certId, activeWorkerId, activeWorkerName, organization, moduleName, finalScore,
                    issuedAt, expiresAt, status, issuer);

            cert = new CertificateEntity(
                    certId,
                    activeWorkerId,
                    activeWorkerName,
                    organization,
                    moduleName,
                    finalScore,
                    currentSector,
                    "Certified Confined Space Operator",
                    token,
                    token,
                    payload,
                    issuedAt,
                    expiresAt,
                    issuer,
                    status,
                    false
            );

            workerRepository.markWorkerCertified(activeWorkerId);
        }

        assessmentRepository.saveAssessmentAndIssueCertificate(record, cert, () -> {
            runOnUiThread(() -> showEvaluationDialog(passed, finalScore));
        });
    }

    private void showEvaluationDialog(boolean passed, int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(passed ? "Module 2 Completed Successfully! \u2713" : "Assessment Evaluation");
        builder.setMessage(passed ?
                "Outstanding execution! You completed all 10 gas leak and confined space safety protocols with a score of " + score + "%. Your Digital Safety Pass is issued." :
                "You scored " + score + "%. Passing threshold is 80%. Unsafe entry or skipped atmospheric testing caused point deductions. Retry the scenario.");
        builder.setCancelable(false);

        if (passed) {
            builder.setPositiveButton("View Safety Pass", (dialog, which) -> {
                Intent intent = new Intent(GasConfinedSpaceArActivity.this, CertificateActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            builder.setPositiveButton("Retry Scenario", (dialog, which) -> {
                stateManager.reset();
                binding.gasOverlayView.invalidate();
                updateUI();
            });
            builder.setNegativeButton("Dashboard", (dialog, which) -> finish());
        }

        builder.show();
    }

    private void vibrateDevice(long ms) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview();
            } else {
                Toast.makeText(this, "Camera permission needed for simulation view.", Toast.LENGTH_SHORT).show();
            }
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
