package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "answers")
public class AnswerEntity {
    @PrimaryKey
    @NonNull
    private String answerId;
    private String attemptId;
    private String questionId;
    private String selectedOptionId;
    private boolean isCorrect;
    private long answeredAt;

    public AnswerEntity(@NonNull String answerId, String attemptId, String questionId,
                        String selectedOptionId, boolean isCorrect, long answeredAt) {
        this.answerId = answerId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
        this.isCorrect = isCorrect;
        this.answeredAt = answeredAt;
    }

    @NonNull
    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(@NonNull String answerId) {
        this.answerId = answerId;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(String selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public long getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(long answeredAt) {
        this.answeredAt = answeredAt;
    }
}
