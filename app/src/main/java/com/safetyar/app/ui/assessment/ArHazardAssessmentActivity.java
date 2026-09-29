package com.safetyar.app.ui.assessment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.safetyar.app.ar.HazardSimulationEngine;
import com.safetyar.app.data.local.entity.AssessmentRecordEntity;
import com.safetyar.app.data.local.entity.CertificateEntity;
import com.safetyar.app.data.repository.AssessmentRepository;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityArAssessmentBinding;
import com.safetyar.app.domain.model.HazardItem;
import com.safetyar.app.domain.model.IndustrySector;
import com.safetyar.app.ui.certificate.CertificateActivity;
import com.safetyar.app.util.QrPassGenerator;

import java.util.List;
import java.util.UUID;

public class ArHazardAssessmentActivity extends AppCompatActivity {

    private ActivityArAssessmentBinding binding;
    private AssessmentRepository assessmentRepository;
    private WorkerRepository workerRepository;

    private IndustrySector currentSector = IndustrySector.COAL_MINING;
    private List<HazardItem> hazards;
    private HazardItem selectedHazard;
    private int mitigatedCount = 0;

    private CountDownTimer examTimer;
    private static final long EXAM_DURATION_MS = 60000; // 60 seconds
    private long timeRemainingMs = EXAM_DURATION_MS;
    private boolean isExamFinished = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(com.safetyar.app.util.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArAssessmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assessmentRepository = new AssessmentRepository(getApplication());
        workerRepository = new WorkerRepository(getApplication());

        String sectorStr = getIntent().getStringExtra("SECTOR_NAME");
        if (sectorStr != null) {
            try {
                currentSector = IndustrySector.valueOf(sectorStr);
            } catch (Exception ignored) {}
        }

        setupUI();
        loadHazards();
        initializeCamera();
        startExamTimer();
    }

    private void setupUI() {
        binding.tvExamSector.setText(currentSector.getDisplayName() + " Competency Exam");

        binding.assessmentArOverlay.setOnHazardClickListener(hazard -> {
            if (isExamFinished) return;
            selectedHazard = hazard;
            binding.cardExamHazardAction.setVisibility(View.VISIBLE);
            binding.tvExamHazardTitle.setText(hazard.getTitle());
            binding.tvExamHazardDesc.setText("Mitigation SOP: " + hazard.getSopActionRequired());
            vibrateDevice(50);
        });

        binding.btnExamApplyMitigation.setOnClickListener(v -> {
            if (selectedHazard != null && !selectedHazard.isMitigated()) {
                selectedHazard.setMitigated(true);
                mitigatedCount++;
                vibrateDevice(150);

                int currentScore = (mitigatedCount * 100) / hazards.size();
                binding.tvExamScore.setText("SCORE: " + currentScore + "%");
                binding.cardExamHazardAction.setVisibility(View.GONE);
                binding.assessmentArOverlay.invalidate();

                if (mitigatedCount >= hazards.size()) {
                    finishExam(true);
                }
            }
        });
    }

    private void loadHazards() {
        hazards = HazardSimulationEngine.generateHazardsForSector(currentSector);
        binding.assessmentArOverlay.setHazards(hazards);
    }

    private void initializeCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission needed for Exam.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        PreviewView previewView = new PreviewView(this);
        binding.assessmentCameraContainer.addView(previewView);

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
    }

    private void startExamTimer() {
        examTimer = new CountDownTimer(EXAM_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingMs = millisUntilFinished;
                int seconds = (int) (millisUntilFinished / 1000);
                binding.tvExamTimer.setText(String.format("00:%02d REMAINING", seconds));
            }

            @Override
            public void onFinish() {
                binding.tvExamTimer.setText("00:00 EXPIRED");
                finishExam(false);
            }
        }.start();
    }

    private void finishExam(boolean allCleared) {
        if (isExamFinished) return;
        isExamFinished = true;
        if (examTimer != null) examTimer.cancel();

        int finalScore = (mitigatedCount * 100) / hazards.size();
        boolean passed = finalScore >= 80;
        long durationSec = (EXAM_DURATION_MS - timeRemainingMs) / 1000;

        String workerId = "WRK-JH-2026-0891";
        String workerName = "Ramesh Kumar Soren";
        String assessmentId = "ASM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        AssessmentRecordEntity record = new AssessmentRecordEntity(
                assessmentId,
                workerId,
                currentSector.name(),
                currentSector.name(),
                finalScore,
                hazards.size(),
                mitigatedCount,
                mitigatedCount,
                passed,
                System.currentTimeMillis(),
                durationSec,
                false
        );

        CertificateEntity cert = null;
        if (passed) {
            String certId = QrPassGenerator.generateUniqueCertificateId();
            long issuedAt = System.currentTimeMillis();
            long expiresAt = issuedAt + (365L * 24 * 60 * 60 * 1000L); // 1 year validity
            String organization = "Jharkhand State Mineral Development (JSMDC)";
            String moduleName = "Hazard Recognition & Risk Mitigation";
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
                    currentSector.getDisplayName(),
                    "Underground Strata Tech",
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
            runOnUiThread(() -> showCompletionDialog(passed, finalScore));
        });
    }

    private void showCompletionDialog(boolean passed, int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(passed ? "Exam Passed \u2713" : "Assessment Failed");
        builder.setMessage(passed ?
                "Congratulations! You scored " + score + "% and demonstrated DGMS standard safety compliance. Your Digital Safety Pass has been issued." :
                "You scored " + score + "%. The passing threshold is 80%. Please review the SOP modules and retake the practical exam.");
        builder.setCancelable(false);

        if (passed) {
            builder.setPositiveButton("View Safety Pass", (dialog, which) -> {
                Intent intent = new Intent(ArHazardAssessmentActivity.this, CertificateActivity.class);
                intent.putExtra("SECTOR_NAME", currentSector.name());
                startActivity(intent);
                finish();
            });
        } else {
            builder.setPositiveButton("Return to Dashboard", (dialog, which) -> finish());
        }

        builder.show();
    }

    private void vibrateDevice(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (examTimer != null) examTimer.cancel();
    }
}
