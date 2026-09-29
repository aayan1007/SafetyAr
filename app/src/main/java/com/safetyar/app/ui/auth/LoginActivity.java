package com.safetyar.app.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.safetyar.app.data.local.entity.WorkerEntity;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityLoginBinding;
import com.safetyar.app.ui.dashboard.MainActivity;
import com.safetyar.app.ui.language.LanguageSelectionActivity;
import com.safetyar.app.util.LocaleHelper;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private WorkerRepository workerRepository;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workerRepository = new WorkerRepository(getApplication());

        // 1-Tap Demo Worker Selectors for SIH Presentation
        binding.cardDemoCoal.setOnClickListener(v -> loginWithWorkerId("WRK-JH-COAL-0891"));
        binding.cardDemoSteel.setOnClickListener(v -> loginWithWorkerId("WRK-JH-STEEL-1045"));
        binding.cardDemoMica.setOnClickListener(v -> loginWithWorkerId("WRK-JH-MICA-0312"));

        // Manual Worker ID Sign-In
        binding.btnLogin.setOnClickListener(v -> {
            String inputId = binding.etWorkerId.getText() != null ? binding.etWorkerId.getText().toString().trim() : "";
            if (inputId.isEmpty()) {
                binding.tilWorkerId.setError("Please enter a valid Worker or Miner ID");
                return;
            }
            binding.tilWorkerId.setError(null);

            workerRepository.loginWithId(inputId, new WorkerRepository.LoginResultCallback() {
                @Override
                public void onSuccess(WorkerEntity worker) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Welcome, " + worker.getFullName(), Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        binding.btnLanguageQuickSwitch.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LanguageSelectionActivity.class);
            startActivity(intent);
        });
    }

    private void loginWithWorkerId(String workerId) {
        workerRepository.switchActiveWorker(workerId, () -> {
            runOnUiThread(this::navigateToDashboard);
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
