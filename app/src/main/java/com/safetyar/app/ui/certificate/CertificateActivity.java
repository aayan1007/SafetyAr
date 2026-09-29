package com.safetyar.app.ui.certificate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.safetyar.app.R;
import com.safetyar.app.data.local.entity.CertificateEntity;
import com.safetyar.app.data.repository.AssessmentRepository;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityCertificateBinding;
import com.safetyar.app.util.LocaleHelper;
import com.safetyar.app.util.QrPassGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CertificateActivity extends AppCompatActivity {

    public static final String EXTRA_CERTIFICATE_ID = "EXTRA_CERTIFICATE_ID";

    private ActivityCertificateBinding binding;
    private AssessmentRepository assessmentRepository;
    private WorkerRepository workerRepository;

    private CertificateEntity currentCertificate;
    private String currentQrPayload = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCertificateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assessmentRepository = new AssessmentRepository(getApplication());
        workerRepository = new WorkerRepository(getApplication());

        binding.btnBackFromCert.setOnClickListener(v -> finish());

        setupActionButtons();
        loadCertificate();
    }

    private void setupActionButtons() {
        // 1. Export Certificate Image (PNG) via FileProvider
        binding.btnExportCertImage.setOnClickListener(v -> exportCertificateAsImage());

        // 2. Share Credential Summary (Text)
        binding.btnShareCertificate.setOnClickListener(v -> shareCertificateText());
    }

    private void loadCertificate() {
        String specificCertId = getIntent().getStringExtra(EXTRA_CERTIFICATE_ID);

        if (specificCertId != null && !specificCertId.trim().isEmpty()) {
            assessmentRepository.getCertificateById(specificCertId).observe(this, cert -> {
                if (cert != null) {
                    displayCertificate(cert);
                } else {
                    loadLatestOrDemo();
                }
            });
        } else {
            loadLatestOrDemo();
        }
    }

    private void loadLatestOrDemo() {
        workerRepository.getActiveWorker().observe(this, worker -> {
            String workerId = (worker != null) ? worker.getWorkerId() : "WRK-JH-COAL-0891";
            assessmentRepository.getLatestCertificate(workerId).observe(this, cert -> {
                if (cert != null) {
                    displayCertificate(cert);
                } else {
                    generateDefaultDemoCertificate(worker != null ? worker.getFullName() : "Ramesh Soren",
                            workerId,
                            worker != null ? worker.getEnterprise() : "Bharat Coking Coal Limited (BCCL)");
                }
            });
        });
    }

    private void displayCertificate(CertificateEntity cert) {
        this.currentCertificate = cert;

        // 1. Worker Name
        binding.tvCertWorkerName.setText(cert.getWorkerName());

        // 2. Worker ID
        binding.tvCertWorkerId.setText(cert.getWorkerId());

        // 3. Organization
        binding.tvCertOrganization.setText(cert.getOrganization());

        // 4. Training Module
        binding.tvCertTrainingModule.setText(cert.getTrainingModule());

        // 5. Score
        binding.tvCertScore.setText(cert.getScore() + "% (Competency Grade A)");

        // 6. Issue Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        binding.tvCertIssueDate.setText(sdf.format(new Date(cert.getIssuedTimestamp())));

        // 7. Validity Date
        binding.tvCertValidityDate.setText(sdf.format(new Date(cert.getExpiresTimestamp())) + " (365 Days)");

        // 8. Certificate ID
        binding.tvCertId.setText(cert.getCertificateId());

        // Cryptographic Hash / Verification Token
        String token = (cert.getVerificationToken() != null && !cert.getVerificationToken().isEmpty())
                ? cert.getVerificationToken() : cert.getCertificateHash();
        binding.tvCertHash.setText("SHA256 Token: " + (token != null && token.length() > 24 ? token.substring(0, 24) + "..." : token));

        // Status Badge
        String status = (cert.getStatus() != null) ? cert.getStatus().toUpperCase(Locale.ROOT) : "VALID";
        binding.tvCertStatusBadge.setText(status);
        if ("VALID".equals(status)) {
            binding.tvCertStatusBadge.setTextColor(getColor(R.color.hazard_green));
            binding.tvCertStatusBadge.setBackgroundResource(R.drawable.badge_certified);
            binding.cardCertificate.setStrokeColor(getColor(R.color.safety_primary));
        } else if ("EXPIRED".equals(status)) {
            binding.tvCertStatusBadge.setTextColor(getColor(R.color.hazard_yellow));
            binding.cardCertificate.setStrokeColor(getColor(R.color.hazard_yellow));
        } else {
            binding.tvCertStatusBadge.setTextColor(getColor(R.color.hazard_red));
            binding.cardCertificate.setStrokeColor(getColor(R.color.hazard_red));
        }

        // 9. QR Code
        currentQrPayload = cert.getQrCodePayload();
        renderQrCode(currentQrPayload);
    }

    private void generateDefaultDemoCertificate(String workerName, String workerId, String organization) {
        String certId = "SAFETYAR-JH-2026-000001";
        String module = "Fire & Explosion Emergency Response";
        int score = 94;
        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + (365L * 24 * 60 * 60 * 1000L);
        String issuer = "Jharkhand Industrial Safety Council (JISC)";
        String status = "VALID";

        String token = QrPassGenerator.computeVerificationToken(certId, workerId, organization, module, score, issuedAt, expiresAt);
        String payload = QrPassGenerator.buildQrPayload(certId, workerId, workerName, organization, module, score, issuedAt, expiresAt, status, issuer);

        CertificateEntity demoCert = new CertificateEntity(
                certId, workerId, workerName, organization, module, score,
                "COAL_MINING", "Underground Drill & Strata Technician",
                token, token, payload, issuedAt, expiresAt, issuer, status, false
        );

        displayCertificate(demoCert);
    }

    private void renderQrCode(String payload) {
        if (payload == null || payload.trim().isEmpty()) return;
        try {
            Bitmap qrBitmap = QrPassGenerator.generateQrCodeBitmap(payload, 500, 500);
            binding.ivQrCode.setImageBitmap(qrBitmap);
        } catch (Exception e) {
            Toast.makeText(this, "QR generation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportCertificateAsImage() {
        try {
            // 1. Measure and capture Card View to Bitmap
            View card = binding.cardCertificate;
            int width = card.getWidth();
            int height = card.getHeight();
            if (width <= 0 || height <= 0) {
                card.measure(View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                             View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                width = card.getMeasuredWidth();
                height = card.getMeasuredHeight();
                card.layout(0, 0, width, height);
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            card.draw(canvas);

            // 2. Save Bitmap to external cache directory
            File cachePath = new File(getExternalCacheDir(), "certificates");
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }

            String certId = (currentCertificate != null) ? currentCertificate.getCertificateId() : "SAFETYAR-JH-2026-000001";
            File imageFile = new File(cachePath, "Certificate_" + certId + ".png");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            // 3. Share via FileProvider
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SafetyAR Industrial Safety Pass - " + certId);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Official Government of Jharkhand Industrial Safety Credential\n" +
                    "Certificate ID: " + certId + "\n" +
                    "Worker: " + ((currentCertificate != null) ? currentCertificate.getWorkerName() : "Ramesh Soren") + "\n" +
                    "Council: Jharkhand Industrial Safety Council (DGMS Standard)");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Export / Save Safety Certificate"));

            Toast.makeText(this, "Certificate image ready for export", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to export certificate image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareCertificateText() {
        if (currentCertificate == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String issueStr = sdf.format(new Date(currentCertificate.getIssuedTimestamp()));
        String expireStr = sdf.format(new Date(currentCertificate.getExpiresTimestamp()));

        String shareBody =
                "=========================================\n" +
                "GOVERNMENT OF JHARKHAND\n" +
                "INDUSTRIAL SAFETY COUNCIL\n" +
                "AR Vocational Safety Credential\n" +
                "=========================================\n" +
                "Certificate ID: " + currentCertificate.getCertificateId() + "\n" +
                "Worker Name: " + currentCertificate.getWorkerName() + "\n" +
                "Worker ID: " + currentCertificate.getWorkerId() + "\n" +
                "Organization: " + currentCertificate.getOrganization() + "\n" +
                "Training Module: " + currentCertificate.getTrainingModule() + "\n" +
                "Score Achieved: " + currentCertificate.getScore() + "%\n" +
                "Status: " + currentCertificate.getStatus() + "\n" +
                "Issue Date: " + issueStr + "\n" +
                "Valid Until: " + expireStr + "\n" +
                "Verification Hash: " + currentCertificate.getVerificationToken() + "\n" +
                "=========================================\n" +
                "Verify via SafetyAR Supervisor Scanner\n" +
                "Payload: " + currentQrPayload;

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "SafetyAR Credential - " + currentCertificate.getCertificateId());
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sendIntent, "Share Safety Credential"));
    }
}
