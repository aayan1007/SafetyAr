package com.safetyar.app.ui.language;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.safetyar.app.databinding.ActivityLanguageSelectionBinding;
import com.safetyar.app.ui.auth.LoginActivity;
import com.safetyar.app.util.LocaleHelper;

public class LanguageSelectionActivity extends AppCompatActivity {

    private ActivityLanguageSelectionBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardLangEnglish.setOnClickListener(v -> selectLanguage(LocaleHelper.LANG_ENGLISH));
        binding.cardLangHindi.setOnClickListener(v -> selectLanguage(LocaleHelper.LANG_HINDI));
        binding.cardLangSantali.setOnClickListener(v -> selectLanguage(LocaleHelper.LANG_SANTALI));
    }

    private void selectLanguage(String langCode) {
        LocaleHelper.setLocale(this, langCode);

        // Advance to Worker Login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
