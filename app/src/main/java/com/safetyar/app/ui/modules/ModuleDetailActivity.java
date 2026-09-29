package com.safetyar.app.ui.modules;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.safetyar.app.data.repository.TrainingRepository;
import com.safetyar.app.databinding.ActivityModuleDetailBinding;
import com.safetyar.app.ui.ar.ArSafetyTrainingActivity;

public class ModuleDetailActivity extends AppCompatActivity {

    private ActivityModuleDetailBinding binding;
    private TrainingRepository trainingRepository;
    private String currentSector = "COAL_MINING";

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.safetyar.app.util.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModuleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        trainingRepository = new TrainingRepository(getApplication());

        String moduleId = getIntent().getStringExtra("MODULE_ID");
        currentSector = getIntent().getStringExtra("SECTOR_NAME");
        if (currentSector == null) currentSector = "COAL_MINING";

        if (moduleId != null) {
            trainingRepository.getModuleById(moduleId).observe(this, module -> {
                if (module != null) {
                    binding.tvDetailRegulation.setText(module.getDgmsRegulationCode());
                    binding.tvDetailTitle.setText(module.getTitle());
                    binding.tvDetailSubtitle.setText(module.getSubtitle());
                    binding.tvDetailSopGuidelines.setText(module.getSopGuidelines());
                }
            });

            trainingRepository.getLessonsForModule(moduleId).observe(this, lessons -> {
                if (lessons != null && !lessons.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("OFFLINE FIELD LESSONS:\n\n");
                    for (com.safetyar.app.data.local.entity.TrainingLessonEntity l : lessons) {
                        sb.append("Lesson ").append(l.getLessonNumber()).append(": ").append(l.getTitle()).append(" (").append(l.getDurationMinutes()).append(" min)\n");
                        sb.append("• ").append(l.getContentBody()).append("\n");
                        sb.append("Rule: ").append(l.getKeyTakeaways()).append("\n\n");
                    }
                    sb.append("MANDATORY SAFETY SOP CHECKLIST:\n\n");
                    sb.append(binding.tvDetailSopGuidelines.getText());
                    binding.tvDetailSopGuidelines.setText(sb.toString());
                }
            });
        }

        binding.btnBackFromDetail.setOnClickListener(v -> finish());

        binding.btnLaunchArFromDetail.setOnClickListener(v -> {
            if (moduleId != null && moduleId.contains("FIRE")) {
                Intent intent = new Intent(ModuleDetailActivity.this, ModuleLearningActivity.class);
                intent.putExtra("MODULE_ID", moduleId);
                intent.putExtra("SECTOR_NAME", currentSector);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ModuleDetailActivity.this, ArSafetyTrainingActivity.class);
                intent.putExtra("SECTOR_NAME", currentSector);
                startActivity(intent);
            }
        });
    }
}
