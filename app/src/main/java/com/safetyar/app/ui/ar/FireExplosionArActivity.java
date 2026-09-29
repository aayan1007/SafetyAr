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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.safetyar.app.R;
import com.safetyar.app.ar.ARSessionManager;
import com.safetyar.app.ar.FireExplosionOverlayView;
import com.safetyar.app.ar.FireExplosionStateManager;
import com.safetyar.app.data.local.entity.AssessmentRecordEntity;
import com.safetyar.app.data.local.entity.CertificateEntity;
import com.safetyar.app.data.repository.AssessmentRepository;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityFireExplosionArBinding;
import com.safetyar.app.ui.certificate.CertificateActivity;
import com.safetyar.app.util.LocaleHelper;
import com.safetyar.app.util.QrPassGenerator;

import java.util.UUID;

public class FireExplosionArActivity extends AppCompatActivity {

    private ActivityFireExplosionArBinding binding;
    private FireExplosionStateManager stateManager;
    private ARSessionManager arSessionManager;
    private AssessmentRepository assessmentRepository;
    private WorkerRepository workerRepository;
    private String activeWorkerId = "WRK-JH-COAL-0891";
    private String activeWorkerName = "Ramesh Soren";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFireExplosionArBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        stateManager = new FireExplosionStateManager();
        arSessionManager = new ARSessionManager();
        assessmentRepository = new AssessmentRepository(getApplication());
        workerRepository = new WorkerRepository(getApplication());

        workerRepository.getActiveWorker().observe(this, worker -> {
            if (worker != null) {
                activeWorkerId = worker.getWorkerId();
                activeWorkerName = worker.getFullName();
            }
        });

        binding.fireOverlayView.setStateManager(stateManager);

        setupClickListeners();
        initializeCamera();
        updateUI();
    }

    private void setupClickListeners() {
        binding.btnBackFromFireAr.setOnClickListener(v -> finish());

        // AR Direct Touch Callbacks
        binding.fireOverlayView.setOnArObjectClickListener(new FireExplosionOverlayView.OnArObjectClickListener() {
            @Override
            public void onFireTapped() {
                executeAction(FireExplosionStateManager.UserAction.ACTION_TAP_FIRE);
            }

            @Override
            public void onAlarmTapped() {
                if (stateManager.getCurrentStep() == FireExplosionStateManager.Step.STEP_2_IDENTIFY_ALARM) {
                    executeAction(FireExplosionStateManager.UserAction.ACTION_TAP_ALARM);
                } else if (stateManager.getCurrentStep() == FireExplosionStateManager.Step.STEP_3_ACTIVATE_ALARM) {
                    executeAction(FireExplosionStateManager.UserAction.ACTION_ACTIVATE_ALARM);
                }
            }

            @Override
            public void onExitRouteTapped() {
                executeAction(FireExplosionStateManager.UserAction.ACTION_TAP_EXIT_ROUTE);
            }

            @Override
            public void onAssemblyPointTapped() {
                executeAction(FireExplosionStateManager.UserAction.ACTION_TAP_ASSEMBLY_POINT);
            }
        });

        // Dynamic Contextual Action Button
        binding.btnContextualAction.setOnClickListener(v -> {
            switch (stateManager.getCurrentStep()) {
                case STEP_1_RECOGNIZE_FIRE:
                    executeAction(FireExplosionStateManager.UserAction.ACTION_TAP_FIRE);
                    break;
                case STEP_2_IDENTIFY_ALARM:
                    executeAction(FireExplosionStateManager.UserAction.ACTION_TAP_ALARM);
                    break;
                case STEP_3_ACTIVATE_ALARM:
                    executeAction(FireExplosionStateManager.UserAction.ACTION_ACTIVATE_ALARM);
                    break;
                case STEP_9_FOLLOW_EXIT_ROUTE:
                    executeAction(FireExplosionStateManager.UserAction.ACTION_TAP_EXIT_ROUTE);
                    break;
                case STEP_10_REACH_ASSEMBLY:
                    executeAction(FireExplosionStateManager.UserAction.ACTION_TAP_ASSEMBLY_POINT);
                    break;
                default:
                    break;
            }
        });

        // Extinguisher Selection Buttons
        binding.btnSelectDcp.setOnClickListener(v ->
                executeAction(FireExplosionStateManager.UserAction.ACTION_SELECT_DCP_EXTINGUISHER));

        binding.btnSelectWater.setOnClickListener(v ->
                executeAction(FireExplosionStateManager.UserAction.ACTION_SELECT_WATER_EXTINGUISHER));

        // PASS Controls
        binding.btnPassPull.setOnClickListener(v ->
                executeAction(FireExplosionStateManager.UserAction.ACTION_PASS_PULL));

        binding.btnPassAim.setOnClickListener(v ->
                executeAction(FireExplosionStateManager.UserAction.ACTION_PASS_AIM));

        binding.btnPassSqueeze.setOnClickListener(v -> {
            binding.fireOverlayView.setSpraying(true);
            executeAction(FireExplosionStateManager.UserAction.ACTION_PASS_SQUEEZE);
            v.postDelayed(() -> binding.fireOverlayView.setSpraying(false), 1200);
        });

        binding.btnPassSweep.setOnClickListener(v -> {
            binding.fireOverlayView.setSpraying(true);
            executeAction(FireExplosionStateManager.UserAction.ACTION_PASS_SWEEP);
            v.postDelayed(() -> {
                binding.fireOverlayView.setSpraying(false);
                binding.fireOverlayView.invalidate();
            }, 1500);
        });
    }

    private void executeAction(FireExplosionStateManager.UserAction action) {
        FireExplosionStateManager.StepResult result = stateManager.processAction(action);

        // Immediate Audio / Haptic Feedback
        if (result.isCorrect) {
            vibrateDevice(60);
            binding.cardFeedbackBanner.setStrokeColor(getColor(R.color.hazard_green));
            binding.tvImmediateFeedback.setTextColor(getColor(R.color.hazard_green));
        } else {
            vibrateDevice(200);
            binding.cardFeedbackBanner.setStrokeColor(getColor(R.color.hazard_red));
            binding.tvImmediateFeedback.setTextColor(getColor(R.color.hazard_red));
        }

        binding.tvImmediateFeedback.setText(result.feedbackMessage);
        binding.tvFireScoreBadge.setText("SCORE: " + result.score);
        binding.fireOverlayView.invalidate();

        updateUI();

        if (result.isFinished) {
            onScenarioCompleted(result.passed, result.score);
        }
    }

    private void updateUI() {
        FireExplosionStateManager.Step step = stateManager.getCurrentStep();
        binding.tvFireCurrentStep.setText("Step " + step.getStepNumber() + " of 10: " + step.getTitle());

        // Toggle UI panels based on step
        if (step == FireExplosionStateManager.Step.STEP_4_SELECT_EXTINGUISHER) {
            binding.layoutExtinguisherSelection.setVisibility(View.VISIBLE);
            binding.layoutPassController.setVisibility(View.GONE);
            binding.btnContextualAction.setVisibility(View.GONE);
        } else if (step.getStepNumber() >= 5 && step.getStepNumber() <= 8) {
            binding.layoutExtinguisherSelection.setVisibility(View.GONE);
            binding.layoutPassController.setVisibility(View.VISIBLE);
            binding.btnContextualAction.setVisibility(View.GONE);

            // Update PASS button highlight states
            binding.btnPassPull.setEnabled(!stateManager.isPinPulled());
            binding.btnPassAim.setEnabled(stateManager.isPinPulled() && !stateManager.isAimedAtBase());
            binding.btnPassSqueeze.setEnabled(stateManager.isAimedAtBase() && !stateManager.isSqueezed());
            binding.btnPassSweep.setEnabled(stateManager.isSqueezed() && !stateManager.isFireExtinguished());
            binding.tvPassProgressLabel.setText(stateManager.isFireExtinguished() ? "Status: Neutralized \u2713" : "Extinguishing Active");
        } else {
            binding.layoutExtinguisherSelection.setVisibility(View.GONE);
            binding.layoutPassController.setVisibility(View.GONE);
            binding.btnContextualAction.setVisibility(View.VISIBLE);

            if (step == FireExplosionStateManager.Step.STEP_1_RECOGNIZE_FIRE) {
                binding.btnContextualAction.setText("1. Recognize Fire Hazard Zone");
            } else if (step == FireExplosionStateManager.Step.STEP_2_IDENTIFY_ALARM) {
                binding.btnContextualAction.setText("2. Locate Manual Alarm Call Point");
            } else if (step == FireExplosionStateManager.Step.STEP_3_ACTIVATE_ALARM) {
                binding.btnContextualAction.setText("3. Sound Emergency Siren \u26A0");
            } else if (step == FireExplosionStateManager.Step.STEP_9_FOLLOW_EXIT_ROUTE) {
                binding.btnContextualAction.setText("9. Evacuate Along Floor Direction Arrows \u2192");
            } else if (step == FireExplosionStateManager.Step.STEP_10_REACH_ASSEMBLY) {
                binding.btnContextualAction.setText("10. Confirm Assembly at Muster Point \u2605");
            }
        }
    }

    private void onScenarioCompleted(boolean passed, int finalScore) {
        String workerId = activeWorkerId;
        String workerName = activeWorkerName;
        String attemptId = "ATT-FIRE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        AssessmentRecordEntity record = new AssessmentRecordEntity(
                attemptId,
                workerId,
                "MOD-FIRE-01",
                "COAL_MINING",
                finalScore,
                10,
                10,
                10,
                passed,
                System.currentTimeMillis(),
                45L,
                false
        );

        CertificateEntity cert = null;
        if (passed) {
            String certId = QrPassGenerator.generateUniqueCertificateId();
            String organization = "Bharat Coking Coal Limited (BCCL)";
            String moduleName = "Fire & Explosion Emergency Response";
            long issuedAt = System.currentTimeMillis();
            long expiresAt = issuedAt + (365L * 24 * 60 * 60 * 1000L);
            String status = "VALID";
            String issuer = "Jharkhand Industrial Safety Council (JISC)";

            String token = QrPassGenerator.computeVerificationToken(
                    certId, workerId, organization, moduleName, finalScore, issuedAt, expiresAt);
            String payload = QrPassGenerator.buildQrPayload(
                    certId, workerId, workerName, organization, moduleName, finalScore,
                    issuedAt, expiresAt, status, issuer);

            cert = new CertificateEntity(
                    certId,
                    workerId,
                    workerName,
                    organization,
                    moduleName,
                    finalScore,
                    "COAL_MINING",
                    "Emergency First Responder & Fire Safety Tech",
                    token,
                    token,
                    payload,
                    issuedAt,
                    expiresAt,
                    issuer,
                    status,
                    false
            );

            workerRepository.markWorkerCertified(workerId);
        }

        assessmentRepository.saveAssessmentAndIssueCertificate(record, cert, () -> {
            runOnUiThread(() -> showEvaluationDialog(passed, finalScore));
        });
    }

    private void showEvaluationDialog(boolean passed, int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(passed ? "AR Assessment Passed! \u2713" : "Assessment Evaluation");
        builder.setMessage(passed ?
                "Exceptional work! You completed all 10 emergency actions in the correct order with a score of " + score + "%. Your Fire & Explosion Safety Credential has been issued." :
                "You scored " + score + "%. DGMS passing standard is 80%. Review PASS procedures and retake the practical scenario.");
        builder.setCancelable(false);

        if (passed) {
            builder.setPositiveButton("View Fire Safety Pass", (dialog, which) -> {
                Intent intent = new Intent(FireExplosionArActivity.this, CertificateActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            builder.setPositiveButton("Return to Dashboard", (dialog, which) -> finish());
        }

        builder.show();
    }

    private void initializeCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission needed for AR view.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        PreviewView previewView = new PreviewView(this);
        binding.fireCameraContainer.addView(previewView);

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

        arSessionManager.initializeSession(this, new ARSessionManager.SessionCallback() {
            @Override
            public void onSessionReady(com.google.ar.core.Session session) {}

            @Override
            public void onSessionError(String errorMessage) {}
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
