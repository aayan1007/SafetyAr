package com.safetyar.app.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.safetyar.app.R;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityWorkerProfileBinding;
import com.safetyar.app.ui.auth.LoginActivity;
import com.safetyar.app.ui.language.LanguageSelectionActivity;
import com.safetyar.app.util.LocaleHelper;

public class WorkerProfileActivity extends AppCompatActivity {

    private ActivityWorkerProfileBinding binding;
    private WorkerRepository workerRepository;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workerRepository = new WorkerRepository(getApplication());

        binding.btnBackFromProfile.setOnClickListener(v -> finish());

        workerRepository.getActiveWorker().observe(this, worker -> {
            if (worker != null) {
                binding.tvProfileName.setText(worker.getFullName());
                binding.tvProfileId.setText("ID: " + worker.getWorkerId());
                binding.tvProfileOrg.setText(worker.getEnterprise());
                binding.tvProfileDept.setText(worker.getDepartment());
                binding.tvProfileTrade.setText(worker.getTrade());
                binding.tvProfileJoining.setText(worker.getJoiningDate());

                if (worker.isCertified()) {
                    binding.tvProfileStatusBadge.setText(getString(R.string.worker_status_certified));
                    binding.tvProfileStatusBadge.setTextColor(getColor(R.color.hazard_green));
                } else {
                    binding.tvProfileStatusBadge.setText(getString(R.string.worker_status_pending));
                    binding.tvProfileStatusBadge.setTextColor(getColor(R.color.hazard_orange));
                }

                // 30-Day Orientation Tracker calculation
                int day = worker.calculateOrientationDay();
                int percent = worker.calculateOrientationPercentage();
                binding.tvProfileOrientationDay.setText(String.format(getString(R.string.orientation_day_format), day));
                binding.tvProfileOrientationPercent.setText(String.format(getString(R.string.orientation_percent_format), percent));
                binding.progressProfileOrientation.setProgress(percent);
            }
        });

        binding.btnProfileChangeLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerProfileActivity.this, LanguageSelectionActivity.class);
            startActivity(intent);
        });

        binding.btnProfileLogout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
