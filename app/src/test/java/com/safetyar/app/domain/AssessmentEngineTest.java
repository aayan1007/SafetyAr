package com.safetyar.app.domain;

import com.safetyar.app.domain.assessment.AssessmentEngine;
import com.safetyar.app.domain.assessment.AssessmentQuestion;
import com.safetyar.app.domain.assessment.AssessmentResult;
import com.safetyar.app.domain.assessment.QuestionType;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AssessmentEngineTest {

    private AssessmentEngine defaultEngine;
    private AssessmentEngine strictEngine;

    @Before
    public void setUp() {
        defaultEngine = new AssessmentEngine(70); // 70% standard threshold
        strictEngine = new AssessmentEngine(80);  // 80% DGMS high-risk threshold
    }

    @Test
    public void testSuccessfulAssessmentPasses() {
        List<AssessmentQuestion> questions = new ArrayList<>();
        questions.add(new AssessmentQuestion("Q1", "Fire Protocol", QuestionType.MULTIPLE_CHOICE,
                "What is the first step in PASS?", Arrays.asList("Pull", "Aim", "Squeeze", "Sweep"),
                "Pull", "Pull the pin", 10, false));
        questions.add(new AssessmentQuestion("Q2", "Fire Protocol", QuestionType.MULTIPLE_CHOICE,
                "What extinguisher for Class C electrical?", Arrays.asList("Water", "DCP", "Foam"),
                "DCP", "Use dry powder", 10, false));

        Map<String, String> answers = new HashMap<>();
        answers.put("Q1", "Pull");
        answers.put("Q2", "DCP");

        AssessmentResult result = defaultEngine.evaluate(questions, answers);

        assertTrue("Expected assessment to pass", result.isPassed());
        assertEquals("Expected 100% score", 100, result.getPercentage());
        assertEquals(0, result.getCriticalMistakesCount());
        assertFalse(result.isFailedDueToCriticalMistake());
    }

    @Test
    public void testScoreBelowThresholdFails() {
        List<AssessmentQuestion> questions = new ArrayList<>();
        questions.add(new AssessmentQuestion("Q1", "Strata", QuestionType.MULTIPLE_CHOICE,
                "Testing bar question", Arrays.asList("A", "B"), "A", "Sounding", 10, false));
        questions.add(new AssessmentQuestion("Q2", "Strata", QuestionType.MULTIPLE_CHOICE,
                "Prop wedging question", Arrays.asList("A", "B"), "A", "Props", 10, false));

        Map<String, String> answers = new HashMap<>();
        answers.put("Q1", "A"); // 1 correct = 50%
        answers.put("Q2", "B"); // incorrect

        AssessmentResult result = defaultEngine.evaluate(questions, answers);

        assertFalse("Expected 50% to fail 70% threshold", result.isPassed());
        assertEquals(50, result.getPercentage());
        assertEquals(0, result.getCriticalMistakesCount());
        assertFalse(result.isFailedDueToCriticalMistake());
    }

    @Test
    public void testZeroToleranceCriticalSafetyViolationFailsDespiteHighNumericalScore() {
        // 5 questions, 20 points each. Total = 100 points.
        // Worker answers 4 questions correctly = 80% (well above 70% threshold!)
        // BUT the 1 incorrect question is marked `isSafetyCritical = true`!
        List<AssessmentQuestion> questions = new ArrayList<>();
        questions.add(new AssessmentQuestion("Q1", "General", QuestionType.MULTIPLE_CHOICE, "Q1", Arrays.asList("A", "B"), "A", "Exp", 20, false));
        questions.add(new AssessmentQuestion("Q2", "General", QuestionType.MULTIPLE_CHOICE, "Q2", Arrays.asList("A", "B"), "A", "Exp", 20, false));
        questions.add(new AssessmentQuestion("Q3", "General", QuestionType.MULTIPLE_CHOICE, "Q3", Arrays.asList("A", "B"), "A", "Exp", 20, false));
        questions.add(new AssessmentQuestion("Q4", "General", QuestionType.MULTIPLE_CHOICE, "Q4", Arrays.asList("A", "B"), "A", "Exp", 20, false));
        // Critical Question:
        questions.add(new AssessmentQuestion("Q_CRITICAL", "Confined Space", QuestionType.SCENARIO,
                "Can you enter a confined space before atmospheric gas testing?",
                Arrays.asList("Yes", "Never without atmospheric test and permit"),
                "Never without atmospheric test and permit",
                "Fatal atmospheric hazard under DGMS S54", 20, true)); // CRITICAL!

        Map<String, String> answers = new HashMap<>();
        answers.put("Q1", "A"); // correct
        answers.put("Q2", "A"); // correct
        answers.put("Q3", "A"); // correct
        answers.put("Q4", "A"); // correct
        answers.put("Q_CRITICAL", "Yes"); // WRONG critical unsafe action!

        AssessmentResult result = defaultEngine.evaluate(questions, answers);

        assertEquals("Numerical score is 80%", 80, result.getPercentage());
        assertTrue("Numerical score exceeds 70% pass threshold", result.getPercentage() >= defaultEngine.getPassThreshold());
        assertEquals("1 critical safety mistake recorded", 1, result.getCriticalMistakesCount());

        // Zero-tolerance rule validation
        assertFalse("Must FAIL due to life-safety violation regardless of 80% numerical score", result.isPassed());
        assertTrue("Must flag as failedDueToCriticalMistake", result.isFailedDueToCriticalMistake());
        assertFalse("Critical explanations must not be empty", result.getCriticalExplanations().isEmpty());
    }

    @Test
    public void testStrictPassThresholdEnforcement() {
        List<AssessmentQuestion> questions = new ArrayList<>();
        questions.add(new AssessmentQuestion("Q1", "General", QuestionType.MULTIPLE_CHOICE, "Q1", Arrays.asList("A", "B"), "A", "Exp", 75, false));
        questions.add(new AssessmentQuestion("Q2", "General", QuestionType.MULTIPLE_CHOICE, "Q2", Arrays.asList("A", "B"), "A", "Exp", 25, false));

        Map<String, String> answers = new HashMap<>();
        answers.put("Q1", "A"); // 75% score

        AssessmentResult passInDefault = defaultEngine.evaluate(questions, answers);
        AssessmentResult failInStrict = strictEngine.evaluate(questions, answers);

        assertTrue("75% passes 70% threshold", passInDefault.isPassed());
        assertFalse("75% fails strict 80% threshold", failInStrict.isPassed());
    }

    @Test
    public void testWeakAreaDiagnosticsGeneratedOnFailure() {
        List<AssessmentQuestion> questions = new ArrayList<>();
        questions.add(new AssessmentQuestion("Q1", "Extinguisher Selection", QuestionType.MULTIPLE_CHOICE,
                "Extinguisher type", Arrays.asList("Water", "DCP"), "DCP", "DGMS rule", 10, false));

        Map<String, String> answers = new HashMap<>();
        answers.put("Q1", "Water"); // Wrong

        AssessmentResult result = defaultEngine.evaluate(questions, answers);

        assertFalse(result.isPassed());
        assertEquals(1, result.getWeakAreas().size());
        assertEquals("Extinguisher Selection", result.getWeakAreas().get(0).getCategory());
        assertTrue("Remediation recommendation must include DGMS circular advice",
                result.getWeakAreas().get(0).getRecommendation().contains("DGMS"));
    }
}
