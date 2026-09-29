package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.safetyar.app.data.local.entity.QuestionEntity;
import com.safetyar.app.data.local.entity.QuestionOptionEntity;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestions(List<QuestionEntity> questions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestionOptions(List<QuestionOptionEntity> options);

    @Query("SELECT * FROM questions WHERE moduleId = :moduleId")
    LiveData<List<QuestionEntity>> getQuestionsForModule(String moduleId);

    @Query("SELECT * FROM questions WHERE moduleId = :moduleId")
    List<QuestionEntity> getQuestionsForModuleSync(String moduleId);

    @Query("SELECT * FROM question_options WHERE questionId = :questionId")
    List<QuestionOptionEntity> getOptionsForQuestionSync(String questionId);
}
