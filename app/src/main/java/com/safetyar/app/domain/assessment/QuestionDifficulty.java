package com.safetyar.app.domain.assessment;

public enum QuestionDifficulty {
    BASIC("Basic"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");

    private final String label;

    QuestionDifficulty(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
