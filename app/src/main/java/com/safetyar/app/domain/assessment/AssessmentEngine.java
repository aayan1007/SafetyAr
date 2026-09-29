package com.safetyar.app.domain.assessment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssessmentEngine {

    public static final int DEFAULT_PASS_THRESHOLD = 70; // 70% passing standard
    private final int passThreshold;

    public AssessmentEngine(int passThreshold) {
        this.passThreshold = passThreshold > 0 ? passThreshold : DEFAULT_PASS_THRESHOLD;
    }

    public AssessmentEngine() {
        this(DEFAULT_PASS_THRESHOLD);
    }

    public int getPassThreshold() {
        return passThreshold;
    }

    public AssessmentResult evaluate(List<AssessmentQuestion> questions, Map<String, String> userAnswers) {
        int totalScorePossible = 0;
        int scoreObtained = 0;
        int correctCount = 0;
        int incorrectCount = 0;
        int criticalMistakesCount = 0;

        List<String> criticalExplanations = new ArrayList<>();
        Map<String, Integer> categoryWrongCounts = new HashMap<>();
        Map<String, Integer> categoryTotalCounts = new HashMap<>();

        if (questions != null) {
            for (AssessmentQuestion q : questions) {
                totalScorePossible += q.getScore();
                String category = q.getCategory() != null ? q.getCategory() : "General Industrial Safety";

                categoryTotalCounts.put(category, categoryTotalCounts.getOrDefault(category, 0) + 1);

                String userAnswer = userAnswers != null ? userAnswers.get(q.getQuestionId()) : null;
                boolean isCorrect = q.validateAnswer(userAnswer);

                if (isCorrect) {
                    scoreObtained += q.getScore();
                    correctCount++;
                } else {
                    incorrectCount++;
                    categoryWrongCounts.put(category, categoryWrongCounts.getOrDefault(category, 0) + 1);

                    if (q.isSafetyCritical()) {
                        criticalMistakesCount++;
                        criticalExplanations.add(q.getQuestionText() + "\nRule: " + q.getExplanation());
                    }
                }
            }
        }

        int percentage = totalScorePossible > 0 ? (int) Math.round(((double) scoreObtained / totalScorePossible) * 100.0) : 0;

        // CRITICAL ZERO-TOLERANCE RULE:
        // A single critical unsafe action causes immediate failure even if numerical score >= passThreshold!
        boolean failedDueToCriticalMistake = (percentage >= passThreshold) && (criticalMistakesCount > 0);
        boolean passed = (percentage >= passThreshold) && (criticalMistakesCount == 0);

        // Build Weak Areas list
        List<AssessmentResult.WeakAreaItem> weakAreas = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryWrongCounts.entrySet()) {
            String cat = entry.getKey();
            int wrong = entry.getValue();
            int total = categoryTotalCounts.getOrDefault(cat, wrong);
            String recommendation = getRemediationAdvice(cat);
            weakAreas.add(new AssessmentResult.WeakAreaItem(cat, wrong, total, recommendation));
        }

        return new AssessmentResult(
                totalScorePossible,
                scoreObtained,
                percentage,
                passed,
                passThreshold,
                correctCount,
                incorrectCount,
                criticalMistakesCount,
                failedDueToCriticalMistake,
                criticalExplanations,
                weakAreas
        );
    }

    private String getRemediationAdvice(String category) {
        if (category == null) return "Review standard operating procedures.";
        switch (category.toLowerCase()) {
            case "fire extinguisher selection":
            case "extinguisher selection":
                return "Re-read DGMS Circular 03/2019: Never deploy water on energized Class C equipment. Review DCP and CO2 ratings.";
            case "pass protocol":
            case "fire suppression sequence":
                return "Review the 4-step PASS sequence: Pull Pin -> Aim at Base -> Squeeze Trigger -> Sweep Side-to-Side.";
            case "emergency alarm & evacuation":
            case "evacuation route":
                return "Always sound the manual pull call point BEFORE attempting fire suppression, then evacuate along green floor markers.";
            case "methane & gas monitoring":
                return "Re-study methanometer calibration and CMR Rule 115: cutting must halt if CH4 exceeds 1.25%.";
            case "roof strata & support":
                return "Practice sound tapping with the safety bar. Never enter spans under unsupported shale roof.";
            case "blast furnace & molten metal":
                return "Ensure aluminized proximity suit and face shield are sealed before tapping runner gates.";
            case "respirable dust & silicosis":
                return "Always inspect N95/P100 silicone seal integrity and activate mist suppression nozzles before crushing mica.";
            default:
                return "Review the microlearning safety theory and complete interactive AR practice before retaking the assessment.";
        }
    }
}
