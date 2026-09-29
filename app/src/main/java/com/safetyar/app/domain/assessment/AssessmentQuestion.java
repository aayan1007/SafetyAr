package com.safetyar.app.domain.assessment;

import java.util.ArrayList;
import java.util.List;

public class AssessmentQuestion {

    private String questionId;
    private String moduleId;
    private String questionText;
    private QuestionType type;
    private QuestionDifficulty difficulty;
    private List<String> options;
    private String correctAnswer;
    private String explanation;
    private int score;
    private boolean isSafetyCritical;
    private String category;
    private int imageDrawableRes;

    public AssessmentQuestion(String questionId,
                              String moduleId,
                              String questionText,
                              QuestionType type,
                              QuestionDifficulty difficulty,
                              List<String> options,
                              String correctAnswer,
                              String explanation,
                              int score,
                              boolean isSafetyCritical,
                              String category,
                              int imageDrawableRes) {
        this.questionId = questionId;
        this.moduleId = moduleId;
        this.questionText = questionText;
        this.type = type;
        this.difficulty = difficulty;
        this.options = options != null ? options : new ArrayList<>();
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.score = score;
        this.isSafetyCritical = isSafetyCritical;
        this.category = category;
        this.imageDrawableRes = imageDrawableRes;
    }

    public AssessmentQuestion(String questionId,
                              String moduleId,
                              String questionText,
                              QuestionType type,
                              QuestionDifficulty difficulty,
                              List<String> options,
                              String correctAnswer,
                              String explanation,
                              int score,
                              boolean isSafetyCritical,
                              String category) {
        this(questionId, moduleId, questionText, type, difficulty, options, correctAnswer, explanation, score, isSafetyCritical, category, 0);
    }

    public boolean validateAnswer(String userAnswer) {
        if (userAnswer == null || correctAnswer == null) {
            return false;
        }

        if (type == QuestionType.SEQUENCE_ORDER) {
            // Compare normalized sequence strings (e.g. "0,1,2,3")
            return correctAnswer.trim().replaceAll("\\s+", "").equalsIgnoreCase(userAnswer.trim().replaceAll("\\s+", ""));
        } else {
            return correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
        }
    }

    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isSafetyCritical() {
        return isSafetyCritical;
    }

    public void setSafetyCritical(boolean safetyCritical) {
        isSafetyCritical = safetyCritical;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImageDrawableRes() {
        return imageDrawableRes;
    }

    public void setImageDrawableRes(int imageDrawableRes) {
        this.imageDrawableRes = imageDrawableRes;
    }
}
