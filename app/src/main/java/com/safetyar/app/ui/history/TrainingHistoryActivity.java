package com.safetyar.app.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.safetyar.app.data.local.entity.AssessmentRecordEntity;
import com.safetyar.app.data.repository.AssessmentRepository;
import com.safetyar.app.databinding.ActivityTrainingHistoryBinding;
import com.safetyar.app.ui.certificate.CertificateActivity;
import com.safetyar.app.ui.modules.ModuleLearningActivity;
import com.safetyar.app.util.LocaleHelper;

public class TrainingHistoryActivity extends AppCompatActivity {

    private ActivityTrainingHistoryBinding binding;
    private AssessmentRepository assessmentRepository;
    private TrainingHistoryAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assessmentRepository = new AssessmentRepository(getApplication());

        binding.btnBackFromHistory.setOnClickListener(v -> finish());

        adapter = new TrainingHistoryAdapter(new TrainingHistoryAdapter.OnHistoryActionListener() {
            @Override
            public void onViewCertificate(AssessmentRecordEntity record) {
                Intent certIntent = new Intent(TrainingHistoryActivity.this, CertificateActivity.class);
                // Optionally look up specific certificate ID if available
                startActivity(certIntent);
            }

            @Override
            public void onReviewTraining(AssessmentRecordEntity record) {
                Intent learnIntent = new Intent(TrainingHistoryActivity.this, ModuleLearningActivity.class);
                learnIntent.putExtra("EXTRA_MODULE_ID", record.getModuleId());
                startActivity(learnIntent);
            }
        });

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);

        assessmentRepository.getAllAssessments().observe(this, records -> {
            if (records == null || records.isEmpty()) {
                binding.tvHistoryEmpty.setVisibility(View.VISIBLE);
                binding.rvHistory.setVisibility(View.GONE);
            } else {
                binding.tvHistoryEmpty.setVisibility(View.GONE);
                binding.rvHistory.setVisibility(View.VISIBLE);
                adapter.setRecords(records);
            }
        });
    }
}
