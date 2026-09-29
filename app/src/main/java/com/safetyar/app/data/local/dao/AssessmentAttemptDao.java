package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.AnswerEntity;
import com.safetyar.app.data.local.entity.AssessmentAttemptEntity;

import java.util.List;

@Dao
public interface AssessmentAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttempt(AssessmentAttemptEntity attempt);

    @Update
    void updateAttempt(AssessmentAttemptEntity attempt);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAnswers(List<AnswerEntity> answers);

    @Query("SELECT * FROM assessment_attempts WHERE workerId = :workerId ORDER BY completedAt DESC")
    LiveData<List<AssessmentAttemptEntity>> getAttemptsForWorker(String workerId);

    @Query("SELECT * FROM assessment_attempts ORDER BY completedAt DESC")
    LiveData<List<AssessmentAttemptEntity>> getAllAttempts();

    @Query("SELECT * FROM assessment_attempts WHERE isSynced = 0")
    List<AssessmentAttemptEntity> getUnsyncedAttemptsSync();

    @Query("UPDATE assessment_attempts SET isSynced = 1 WHERE attemptId = :attemptId")
    void markAttemptSynced(String attemptId);
}
