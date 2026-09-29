package com.safetyar.app.ui.assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;
import com.safetyar.app.R;
import com.safetyar.app.data.local.entity.AnswerEntity;
import com.safetyar.app.data.local.entity.AssessmentAttemptEntity;
import com.safetyar.app.data.local.entity.CertificateEntity;
import com.safetyar.app.data.repository.AssessmentQuestionBank;
import com.safetyar.app.data.repository.AssessmentRepository;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityAssessmentEngineBinding;
import com.safetyar.app.domain.assessment.AssessmentEngine;
import com.safetyar.app.domain.assessment.AssessmentQuestion;
import com.safetyar.app.domain.assessment.AssessmentResult;
import com.safetyar.app.domain.assessment.QuestionType;
import com.safetyar.app.ui.certificate.CertificateActivity;
import com.safetyar.app.ui.modules.ModuleLearningActivity;
import com.safetyar.app.util.LocaleHelper;
import com.safetyar.app.util.QrPassGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AssessmentEngineActivity extends AppCompatActivity {

    private ActivityAssessmentEngineBinding binding;
    private AssessmentRepository assessmentRepository;
    private WorkerRepository workerRepository;

    private String moduleId = "MOD-FIRE-01";
    private String sectorName = "COAL_MINING";
    private int passThreshold = 70; // Configurable pass threshold (e.g. 70%)
    private String workerId = "WRK-JH-COAL-0891";
    private String workerName = "Ramesh Soren";

    private List<AssessmentQuestion> questionList;
    private int currentIndex = 0;
    private final Map<String, String> userAnswers = new HashMap<>();

    // Sequence question state
    private final List<Integer> selectedSequenceOrder = new ArrayList<>();

    // Timer state
    private CountDownTimer examTimer;
    private static final long EXAM_DURATION_MS = 5 * 60 * 1000; // 5 minutes
    private long startTimeMs;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssessmentEngineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assessmentRepository = new AssessmentRepository(getApplication());
        workerRepository = new WorkerRepository(getApplication());

        if (getIntent().hasExtra("MODULE_ID")) {
            moduleId = getIntent().getStringExtra("MODULE_ID");
        }
        if (getIntent().hasExtra("SECTOR_NAME")) {
            sectorName = getIntent().getStringExtra("SECTOR_NAME");
        }
        if (getIntent().hasExtra("PASS_THRESHOLD")) {
            passThreshold = getIntent().getIntExtra("PASS_THRESHOLD", 70);
        }

        workerRepository.getActiveWorker().observe(this, worker -> {
            if (worker != null) {
                workerId = worker.getWorkerId();
                workerName = worker.getFullName();
            }
        });

        loadQuestions();
        setupClickListeners();
        startTimer();
        displayQuestion(currentIndex);
    }

    private void loadQuestions() {
        questionList = AssessmentQuestionBank.getQuestionsForModule(moduleId);
        if (questionList == null || questionList.isEmpty()) {
            questionList = AssessmentQuestionBank.getQuestionsForModule("MOD-FIRE-01");
        }
    }

    private void setupClickListeners() {
        binding.btnBackFromAssessment.setOnClickListener(v -> finish());

        binding.btnResetSequence.setOnClickListener(v -> {
            selectedSequenceOrder.clear();
            renderSequenceOptions(questionList.get(currentIndex));
        });

        binding.btnSubmitAnswer.setOnClickListener(v -> {
            if (recordCurrentAnswer()) {
                if (currentIndex < questionList.size() - 1) {
                    currentIndex++;
                    displayQuestion(currentIndex);
                } else {
                    finishAssessment();
                }
            }
        });

        binding.btnReviewTraining.setOnClickListener(v -> {
            Intent intent = new Intent(AssessmentEngineActivity.this, ModuleLearningActivity.class);
            intent.putExtra("MODULE_ID", moduleId);
            startActivity(intent);
            finish();
        });

        binding.btnRetryAssessment.setOnClickListener(v -> {
            userAnswers.clear();
            selectedSequenceOrder.clear();
            currentIndex = 0;
            binding.layoutAssessmentResult.setVisibility(View.GONE);
            binding.containerExamActive.setVisibility(View.VISIBLE);
            startTimer();
            displayQuestion(currentIndex);
        });

        binding.btnReturnDashboard.setOnClickListener(v -> finish());

        binding.btnViewCertificate.setOnClickListener(v -> {
            Intent intent = new Intent(AssessmentEngineActivity.this, CertificateActivity.class);
            intent.putExtra("SECTOR_NAME", sectorName);
            startActivity(intent);
            finish();
        });
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questionList.size()) return;

        AssessmentQuestion q = questionList.get(index);

        // Update Counter & Progress
        binding.tvQuestionCounter.setText(String.format(Locale.getDefault(), "Question %d of %d", index + 1, questionList.size()));
        int progress = (int) (((float) (index + 1) / questionList.size()) * 100);
        binding.progressAssessment.setProgress(progress);

        // Next button text
        if (index == questionList.size() - 1) {
            binding.btnSubmitAnswer.setText("Finish & Submit Exam \u2192");
        } else {
            binding.btnSubmitAnswer.setText("Next Question \u2192");
        }

        // Category & Difficulty
        binding.tvQuestionCategory.setText(q.getCategory() != null ? q.getCategory().toUpperCase(Locale.ROOT) : "GENERAL SAFETY");
        binding.tvDifficultyBadge.setText(String.format(Locale.getDefault(), "%s \u2022 %d PTS", q.getDifficulty().getLabel().toUpperCase(Locale.ROOT), q.getScore()));

        // Safety Critical Banner
        if (q.isSafetyCritical()) {
            binding.tvSafetyCriticalBadge.setVisibility(View.VISIBLE);
        } else {
            binding.tvSafetyCriticalBadge.setVisibility(View.GONE);
        }

        // Image View (for IMAGE_BASED)
        if (q.getType() == QuestionType.IMAGE_BASED && q.getImageDrawableRes() != 0) {
            binding.ivQuestionHazardImage.setVisibility(View.VISIBLE);
            binding.ivQuestionHazardImage.setImageResource(q.getImageDrawableRes());
        } else {
            binding.ivQuestionHazardImage.setVisibility(View.GONE);
        }

        // Question Text
        binding.tvQuestionText.setText(q.getQuestionText());

        // Reset and show options depending on QuestionType
        binding.rgOptionsContainer.removeAllViews();
        binding.rgOptionsContainer.clearCheck();
        selectedSequenceOrder.clear();

        if (q.getType() == QuestionType.SEQUENCE_ORDER) {
            binding.rgOptionsContainer.setVisibility(View.GONE);
            binding.layoutArActionContainer.setVisibility(View.GONE);
            binding.layoutSequenceContainer.setVisibility(View.VISIBLE);
            binding.tvAnswerInstruction.setText("TAP ITEMS IN THE EXACT OPERATIONAL SEQUENCE:");
            renderSequenceOptions(q);
        } else if (q.getType() == QuestionType.AR_ACTION) {
            binding.layoutSequenceContainer.setVisibility(View.GONE);
            binding.layoutArActionContainer.setVisibility(View.VISIBLE);
            binding.rgOptionsContainer.setVisibility(View.VISIBLE);
            binding.tvAnswerInstruction.setText("SELECT THE MANDATORY AR FIELD ACTION:");
            renderRadioOptions(q);
        } else {
            binding.layoutSequenceContainer.setVisibility(View.GONE);
            binding.layoutArActionContainer.setVisibility(View.GONE);
            binding.rgOptionsContainer.setVisibility(View.VISIBLE);
            binding.tvAnswerInstruction.setText("SELECT THE CORRECT OPTION:");
            renderRadioOptions(q);
        }
    }

    private void renderRadioOptions(AssessmentQuestion q) {
        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            String optionText = options.get(i);
            RadioButton rb = new RadioButton(this);
            rb.setId(View.generateViewId());
            rb.setText(optionText);
            rb.setTextColor(getColor(R.color.text_on_dark));
            rb.setTextSize(14f);
            rb.setPadding(16, 20, 16, 20);

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 0, 10);
            rb.setLayoutParams(params);

            binding.rgOptionsContainer.addView(rb);

            // Restore previous selection if any
            if (optionText.equals(userAnswers.get(q.getQuestionId()))) {
                rb.setChecked(true);
            }
        }
    }

    private void renderSequenceOptions(AssessmentQuestion q) {
        binding.containerSequenceItems.removeAllViews();
        List<String> options = q.getOptions();

        for (int i = 0; i < options.size(); i++) {
            final int optionIndex = i;
            String optionText = options.get(i);

            MaterialCardView card = new MaterialCardView(this);
            card.setCardBackgroundColor(getColor(R.color.surface_card));
            card.setStrokeColor(getColor(R.color.border_subtle));
            card.setStrokeWidth(2);
            card.setRadius(24f);

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setPadding(24, 24, 24, 24);

            TextView tvStep = new TextView(this);
            tvStep.setTextSize(13f);
            tvStep.setTextColor(getColor(R.color.safety_primary));

            int posInOrder = selectedSequenceOrder.indexOf(optionIndex);
            if (posInOrder != -1) {
                tvStep.setText(String.format(Locale.getDefault(), "[Step %d] ", posInOrder + 1));
                card.setStrokeColor(getColor(R.color.safety_primary));
                card.setCardBackgroundColor(getColor(R.color.safety_secondary_container));
            } else {
                tvStep.setText("[ ] ");
            }

            TextView tvText = new TextView(this);
            tvText.setText(optionText);
            tvText.setTextColor(getColor(R.color.text_on_dark));
            tvText.setTextSize(14f);

            itemLayout.addView(tvStep);
            itemLayout.addView(tvText);
            card.addView(itemLayout);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 10, 0, 10);
            card.setLayoutParams(lp);

            card.setOnClickListener(v -> {
                if (!selectedSequenceOrder.contains(optionIndex)) {
                    selectedSequenceOrder.add(optionIndex);
                    renderSequenceOptions(q);
                }
            });

            binding.containerSequenceItems.addView(card);
        }
    }

    private boolean recordCurrentAnswer() {
        AssessmentQuestion q = questionList.get(currentIndex);

        if (q.getType() == QuestionType.SEQUENCE_ORDER) {
            if (selectedSequenceOrder.size() < q.getOptions().size()) {
                Toast.makeText(this, "Please select all items in sequence.", Toast.LENGTH_SHORT).show();
                return false;
            }
            List<String> orderStrings = new ArrayList<>();
            for (Integer idx : selectedSequenceOrder) {
                orderStrings.add(String.valueOf(idx));
            }
            String answerSequence = TextUtils.join(",", orderStrings);
            userAnswers.put(q.getQuestionId(), answerSequence);
            return true;
        } else {
            int selectedId = binding.rgOptionsContainer.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an answer before continuing.", Toast.LENGTH_SHORT).show();
                return false;
            }
            RadioButton rb = binding.rgOptionsContainer.findViewById(selectedId);
            if (rb != null) {
                userAnswers.put(q.getQuestionId(), rb.getText().toString());
                return true;
            }
            return false;
        }
    }

    private void finishAssessment() {
        if (examTimer != null) {
            examTimer.cancel();
        }

        AssessmentEngine engine = new AssessmentEngine(passThreshold);
        AssessmentResult result = engine.evaluate(questionList, userAnswers);

        long durationSec = (System.currentTimeMillis() - startTimeMs) / 1000L;
        String attemptId = "ATT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);

        // Build Room entities
        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity(
                attemptId,
                workerId,
                moduleId,
                "SCENARIO-" + moduleId,
                sectorName,
                result.getScoreObtained(),
                result.getTotalScorePossible(),
                result.isPassed(),
                startTimeMs,
                System.currentTimeMillis(),
                durationSec,
                false
        );

        List<AnswerEntity> answers = new ArrayList<>();
        for (AssessmentQuestion q : questionList) {
            String ans = userAnswers.get(q.getQuestionId());
            boolean isCorrect = q.validateAnswer(ans);
            answers.add(new AnswerEntity(
                    UUID.randomUUID().toString(),
                    attemptId,
                    q.getQuestionId(),
                    ans != null ? ans : "NONE",
                    isCorrect,
                    System.currentTimeMillis()
            ));
        }

        assessmentRepository.saveAttemptWithAnswers(attempt, answers, null);

        // Issue Certificate if Passed
        if (result.isPassed()) {
            issueCertificateIfPassed(result);
        }

        renderResult(result);
    }

    private void issueCertificateIfPassed(AssessmentResult result) {
        String certId = QrPassGenerator.generateUniqueCertificateId();
        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + (365L * 24 * 60 * 60 * 1000L);
        String organization = "Bharat Coking Coal Limited (BCCL)";
        String status = "VALID";
        String issuer = "Jharkhand Industrial Safety Council (JISC)";
        int finalScore = result.getPercentage();

        String token = QrPassGenerator.computeVerificationToken(
                certId, workerId, organization, moduleId, finalScore, issuedAt, expiresAt);
        String payload = QrPassGenerator.buildQrPayload(
                certId, workerId, workerName, organization, moduleId, finalScore,
                issuedAt, expiresAt, status, issuer);

        CertificateEntity cert = new CertificateEntity(
                certId,
                workerId,
                workerName,
                organization,
                moduleId,
                finalScore,
                sectorName,
                "Certified Safety Operator",
                token,
                token,
                payload,
                issuedAt,
                expiresAt,
                issuer,
                status,
                false
        );

        assessmentRepository.saveAssessmentAndIssueCertificate(null, cert, null);
        workerRepository.markWorkerCertified(workerId);
    }

    private void renderResult(AssessmentResult result) {
        binding.containerExamActive.setVisibility(View.GONE);
        binding.layoutAssessmentResult.setVisibility(View.VISIBLE);

        binding.tvResultScoreDisplay.setText(String.format(Locale.getDefault(), "%d%% \u2022 %d / %d Points",
                result.getPercentage(), result.getScoreObtained(), result.getTotalScorePossible()));
        binding.tvResultPassThreshold.setText(String.format(Locale.getDefault(), "Passing Threshold: %d%%", result.getPassThreshold()));

        binding.tvCorrectCount.setText(String.valueOf(result.getCorrectAnswersCount()));
        binding.tvIncorrectCount.setText(String.valueOf(result.getIncorrectAnswersCount()));
        binding.tvCriticalMistakesCount.setText(String.valueOf(result.getCriticalMistakesCount()));

        if (result.isPassed()) {
            binding.cardResultHero.setStrokeColor(getColor(R.color.hazard_green));
            binding.ivResultIcon.setImageResource(R.drawable.ic_check_circle);
            binding.ivResultIcon.setColorFilter(getColor(R.color.hazard_green));
            binding.tvResultDecisionTitle.setText("COMPETENCY EXAM PASSED");
            binding.tvResultDecisionTitle.setTextColor(getColor(R.color.hazard_green));

            binding.cardCriticalSafetyViolation.setVisibility(View.GONE);
            binding.btnViewCertificate.setVisibility(View.VISIBLE);
        } else {
            binding.cardResultHero.setStrokeColor(getColor(R.color.hazard_red));
            binding.ivResultIcon.setImageResource(R.drawable.ic_hazard_warning);
            binding.ivResultIcon.setColorFilter(getColor(R.color.hazard_red));
            binding.tvResultDecisionTitle.setText("COMPETENCY EXAM FAILED");
            binding.tvResultDecisionTitle.setTextColor(getColor(R.color.hazard_red));

            binding.btnViewCertificate.setVisibility(View.GONE);

            // Zero-Tolerance Critical Failure Warning
            if (result.isFailedDueToCriticalMistake()) {
                binding.cardCriticalSafetyViolation.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder();
                sb.append("CRITICAL SAFETY VIOLATION OCCURRED:\n\n");
                for (String detail : result.getCriticalMistakeExplanations()) {
                    sb.append("• ").append(detail).append("\n\n");
                }
                sb.append("Even though your numerical score was ").append(result.getPercentage())
                        .append("%, under DGMS zero-tolerance life-safety regulations, an unsafe critical action causes mandatory failure. Retraining is required.");
                binding.tvCriticalViolationDetail.setText(sb.toString());
            } else {
                binding.cardCriticalSafetyViolation.setVisibility(View.GONE);
            }
        }

        // Weak Areas Diagnosis
        List<AssessmentResult.WeakAreaItem> weakAreas = result.getWeakAreas();
        if (weakAreas != null && !weakAreas.isEmpty()) {
            binding.cardWeakAreas.setVisibility(View.VISIBLE);
            binding.containerWeakAreaItems.removeAllViews();

            for (AssessmentResult.WeakAreaItem item : weakAreas) {
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(14, 14, 14, 14);
                card.setBackgroundResource(R.drawable.badge_warning);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(0, 8, 0, 8);
                card.setLayoutParams(lp);

                TextView tvTopic = new TextView(this);
                tvTopic.setText(String.format(Locale.getDefault(), "%s (%d / %d Errors)", item.getCategory(), item.getWrongCount(), item.getTotalCount()));
                tvTopic.setTextColor(getColor(R.color.hazard_orange));
                tvTopic.setTextSize(13f);
                tvTopic.setTypeface(null, android.graphics.Typeface.BOLD);

                TextView tvRec = new TextView(this);
                tvRec.setText(item.getRecommendation());
                tvRec.setTextColor(getColor(R.color.text_on_dark));
                tvRec.setTextSize(12f);
                tvRec.setLineSpacing(0f, 1.2f);
                tvRec.setPadding(0, 4, 0, 0);

                card.addView(tvTopic);
                card.addView(tvRec);
                binding.containerWeakAreaItems.addView(card);
            }
        } else {
            binding.cardWeakAreas.setVisibility(View.GONE);
        }
    }

    private void startTimer() {
        startTimeMs = System.currentTimeMillis();
        if (examTimer != null) {
            examTimer.cancel();
        }

        examTimer = new CountDownTimer(EXAM_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                binding.tvAssessmentTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                binding.tvAssessmentTimer.setText("00:00");
                Toast.makeText(AssessmentEngineActivity.this, "Exam time expired. Submitting answers…", Toast.LENGTH_SHORT).show();
                recordCurrentAnswer();
                finishAssessment();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (examTimer != null) {
            examTimer.cancel();
        }
    }
}
