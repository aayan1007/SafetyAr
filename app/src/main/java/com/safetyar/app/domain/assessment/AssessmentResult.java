package com.safetyar.app.domain.assessment;

import java.util.ArrayList;
import java.util.List;

public class AssessmentResult {

    public static class WeakAreaItem {
        private final String category;
        private final int wrongCount;
        private final int totalCount;
        private final String recommendation;

        public WeakAreaItem(String category, int wrongCount, int totalCount, String recommendation) {
            this.category = category;
            this.wrongCount = wrongCount;
            this.totalCount = totalCount;
            this.recommendation = recommendation;
        }

        public String getCategory() {
            return category;
        }

        public int getWrongCount() {
            return wrongCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public String getRecommendation() {
            return recommendation;
        }
    }

    private final int totalScorePossible;
    private final int scoreObtained;
    private final int percentage;
    private final boolean passed;
    private final int passThreshold;
    private final int correctAnswersCount;
    private final int incorrectAnswersCount;
    private final int criticalMistakesCount;
    private final boolean failedDueToCriticalMistake;
    private final List<String> criticalMistakeExplanations;
    private final List<WeakAreaItem> weakAreas;

    public AssessmentResult(int totalScorePossible,
                            int scoreObtained,
                            int percentage,
                            boolean passed,
                            int passThreshold,
                            int correctAnswersCount,
                            int incorrectAnswersCount,
                            int criticalMistakesCount,
                            boolean failedDueToCriticalMistake,
                            List<String> criticalMistakeExplanations,
                            List<WeakAreaItem> weakAreas) {
        this.totalScorePossible = totalScorePossible;
        this.scoreObtained = scoreObtained;
        this.percentage = percentage;
        this.passed = passed;
        this.passThreshold = passThreshold;
        this.correctAnswersCount = correctAnswersCount;
        this.incorrectAnswersCount = incorrectAnswersCount;
        this.criticalMistakesCount = criticalMistakesCount;
        this.failedDueToCriticalMistake = failedDueToCriticalMistake;
        this.criticalMistakeExplanations = criticalMistakeExplanations != null ? criticalMistakeExplanations : new ArrayList<>();
        this.weakAreas = weakAreas != null ? weakAreas : new ArrayList<>();
    }

    public int getTotalScorePossible() {
        return totalScorePossible;
    }

    public int getScoreObtained() {
        return scoreObtained;
    }

    public int getPercentage() {
        return percentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public int getPassThreshold() {
        return passThreshold;
    }

    public int getCorrectAnswersCount() {
        return correctAnswersCount;
    }

    public int getIncorrectAnswersCount() {
        return incorrectAnswersCount;
    }

    public int getCriticalMistakesCount() {
        return criticalMistakesCount;
    }

    public boolean isFailedDueToCriticalMistake() {
        return failedDueToCriticalMistake;
    }

    public List<String> getCriticalMistakeExplanations() {
        return criticalMistakeExplanations;
    }

    public List<WeakAreaItem> getWeakAreas() {
        return weakAreas;
    }
}
