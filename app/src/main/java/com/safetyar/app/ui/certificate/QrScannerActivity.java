package com.safetyar.app.ui.certificate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.safetyar.app.R;
import com.safetyar.app.data.service.CertificateVerificationService;
import com.safetyar.app.data.service.LocalCertificateVerificationService;
import com.safetyar.app.databinding.ActivityQrScannerBinding;
import com.safetyar.app.util.LocaleHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QrScannerActivity extends AppCompatActivity {

    private ActivityQrScannerBinding binding;
    private CertificateVerificationService verificationService;
    private boolean isScanned = false;
    private String lastVerifiedCertId = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        verificationService = new LocalCertificateVerificationService(this);

        setupHeaderAndControls();
        setupSimulationButtons();
        setupManualSearch();
        setupCameraScanner();
    }

    private void setupHeaderAndControls() {
        binding.btnBackFromScanner.setOnClickListener(v -> finish());

        binding.btnScanAgain.setOnClickListener(v -> {
            isScanned = false;
            binding.cardScanResult.setVisibility(View.GONE);
            binding.barcodeScannerView.resume();
        });

        binding.btnViewFullPass.setOnClickListener(v -> {
            if (lastVerifiedCertId != null && !lastVerifiedCertId.isEmpty()) {
                Intent certIntent = new Intent(QrScannerActivity.this, CertificateActivity.class);
                certIntent.putExtra(CertificateActivity.EXTRA_CERTIFICATE_ID, lastVerifiedCertId);
                startActivity(certIntent);
            }
        });
    }

    private void setupSimulationButtons() {
        binding.btnSimValid.setOnClickListener(v -> {
            triggerVerification(LocalCertificateVerificationService.SIM_VALID);
        });

        binding.btnSimExpired.setOnClickListener(v -> {
            triggerVerification(LocalCertificateVerificationService.SIM_EXPIRED);
        });

        binding.btnSimRevoked.setOnClickListener(v -> {
            triggerVerification(LocalCertificateVerificationService.SIM_REVOKED);
        });

        binding.btnSimNotFound.setOnClickListener(v -> {
            triggerVerification(LocalCertificateVerificationService.SIM_NOT_FOUND);
        });
    }

    private void setupManualSearch() {
        binding.btnVerifyManualId.setOnClickListener(v -> {
            String query = binding.etManualCertId.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "Please enter a Certificate ID", Toast.LENGTH_SHORT).show();
                return;
            }
            hideKeyboard();
            triggerVerification(query);
        });
    }

    private void setupCameraScanner() {
        binding.barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (isScanned || result == null || result.getText() == null) return;
                isScanned = true;
                binding.barcodeScannerView.pause();
                vibrateDevice(80);

                triggerVerification(result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {}
        });
    }

    private void triggerVerification(String query) {
        binding.barcodeScannerView.pause();
        isScanned = true;

        verificationService.verify(query, this::displayVerificationResult);
    }

    private void displayVerificationResult(CertificateVerificationService.VerificationResult res) {
        binding.cardScanResult.setVisibility(View.VISIBLE);
        this.lastVerifiedCertId = res.getCertificateId();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String issueStr = (res.getIssuedTimestamp() > 0) ? sdf.format(new Date(res.getIssuedTimestamp())) : "N/A";
        String expireStr = (res.getExpiresTimestamp() > 0) ? sdf.format(new Date(res.getExpiresTimestamp())) : "N/A";

        binding.tvScanResultCertId.setText(res.getCertificateId());

        switch (res.getStatus()) {
            case VALID:
                vibrateDevice(50);
                binding.cardScanResult.setStrokeColor(getColor(R.color.hazard_green));
                binding.tvScanResultBadge.setText("STATUS: VALID \u2713");
                binding.tvScanResultBadge.setTextColor(getColor(R.color.hazard_green));
                binding.tvScanResultBadge.setBackgroundResource(R.drawable.badge_certified);

                binding.tvScanWorkerName.setText(res.getWorkerName() + " (" + res.getWorkerId() + ")");
                binding.tvScanOrganization.setText(res.getOrganization());
                binding.tvScanModuleAndScore.setText("Module: " + res.getTrainingModule() + " \u2022 Score: " + res.getScore() + "%");
                binding.tvScanDates.setText("Issued: " + issueStr + " | Valid Until: " + expireStr);
                binding.tvScanCertMessage.setText(res.getMessage());
                binding.tvScanComplianceReason.setText(res.getComplianceNote());
                binding.btnViewFullPass.setVisibility(View.VISIBLE);
                break;

            case EXPIRED:
                vibrateDevice(200);
                binding.cardScanResult.setStrokeColor(getColor(R.color.hazard_yellow));
                binding.tvScanResultBadge.setText("STATUS: EXPIRED \u26A0");
                binding.tvScanResultBadge.setTextColor(getColor(R.color.hazard_yellow));
                binding.tvScanResultBadge.setBackgroundResource(R.drawable.badge_certified);

                binding.tvScanWorkerName.setText(res.getWorkerName() + " (" + res.getWorkerId() + ")");
                binding.tvScanOrganization.setText(res.getOrganization());
                binding.tvScanModuleAndScore.setText("Module: " + res.getTrainingModule() + " \u2022 Score: " + res.getScore() + "%");
                binding.tvScanDates.setText("Issued: " + issueStr + " | Expired: " + expireStr);
                binding.tvScanCertMessage.setText(res.getMessage());
                binding.tvScanComplianceReason.setText(res.getComplianceNote());
                binding.btnViewFullPass.setVisibility(View.VISIBLE);
                break;

            case REVOKED:
                vibrateDevice(400);
                binding.cardScanResult.setStrokeColor(getColor(R.color.hazard_red));
                binding.tvScanResultBadge.setText("STATUS: REVOKED \u2716");
                binding.tvScanResultBadge.setTextColor(getColor(R.color.hazard_red));
                binding.tvScanResultBadge.setBackgroundResource(R.drawable.badge_certified);

                binding.tvScanWorkerName.setText(res.getWorkerName() + " (" + res.getWorkerId() + ")");
                binding.tvScanOrganization.setText(res.getOrganization());
                binding.tvScanModuleAndScore.setText("Module: " + res.getTrainingModule() + " \u2022 Score: " + res.getScore() + "%");
                binding.tvScanDates.setText("Issued: " + issueStr + " | Action Date: " + expireStr);
                binding.tvScanCertMessage.setText(res.getMessage());
                binding.tvScanComplianceReason.setText(res.getComplianceNote());
                binding.btnViewFullPass.setVisibility(View.GONE);
                break;

            case NOT_FOUND:
            default:
                vibrateDevice(500);
                binding.cardScanResult.setStrokeColor(getColor(R.color.hazard_red));
                binding.tvScanResultBadge.setText("STATUS: NOT FOUND \u2753");
                binding.tvScanResultBadge.setTextColor(getColor(R.color.hazard_red));
                binding.tvScanResultBadge.setBackgroundResource(R.drawable.badge_certified);

                binding.tvScanWorkerName.setText("Unregistered Worker / Unknown Pass");
                binding.tvScanOrganization.setText("Organization: Not in DGMS Registry");
                binding.tvScanModuleAndScore.setText("Module: Unknown \u2022 Score: N/A");
                binding.tvScanDates.setText("Security Audit: Tamper / Counterfeit Alert");
                binding.tvScanCertMessage.setText(res.getMessage());
                binding.tvScanComplianceReason.setText(res.getComplianceNote());
                binding.btnViewFullPass.setVisibility(View.GONE);
                break;
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void vibrateDevice(long milliseconds) {
        try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isScanned) {
            binding.barcodeScannerView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.barcodeScannerView.pause();
    }
}
