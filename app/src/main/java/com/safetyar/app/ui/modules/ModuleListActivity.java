package com.safetyar.app.ui.modules;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.safetyar.app.data.local.entity.TrainingModuleEntity;
import com.safetyar.app.data.repository.TrainingRepository;
import com.safetyar.app.databinding.ActivityModuleListBinding;

public class ModuleListActivity extends AppCompatActivity {

    private ActivityModuleListBinding binding;
    private TrainingRepository trainingRepository;
    private ModuleAdapter adapter;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.safetyar.app.util.LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModuleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        trainingRepository = new TrainingRepository(getApplication());

        String sector = getIntent().getStringExtra("SECTOR_NAME");
        if (sector == null) {
            sector = "COAL_MINING";
        }

        setupRecyclerView();
        observeModules(sector);

        binding.btnBackFromModules.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ModuleAdapter(module -> {
            Intent intent = new Intent(ModuleListActivity.this, ModuleDetailActivity.class);
            intent.putExtra("MODULE_ID", module.getModuleId());
            intent.putExtra("SECTOR_NAME", module.getSector());
            startActivity(intent);
        });

        binding.rvModules.setLayoutManager(new LinearLayoutManager(this));
        binding.rvModules.setAdapter(adapter);
    }

    private void observeModules(String sector) {
        trainingRepository.getModulesBySector(sector).observe(this, modules -> {
            if (modules != null) {
                adapter.setModules(modules);
            }
        });
    }
}
