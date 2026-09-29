package com.safetyar.app.domain.assessment;

public enum QuestionType {
    MULTIPLE_CHOICE("Multiple Choice"),
    IMAGE_BASED("Image Hazard Identification"),
    SCENARIO("Industrial Scenario"),
    SEQUENCE_ORDER("Sequential Protocol Order"),
    AR_ACTION("AR Practical Action");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
