package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.TrainingLessonEntity;

import java.util.List;

@Dao
public interface TrainingLessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLessons(List<TrainingLessonEntity> lessons);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLesson(TrainingLessonEntity lesson);

    @Update
    void updateLesson(TrainingLessonEntity lesson);

    @Query("SELECT * FROM training_lessons WHERE moduleId = :moduleId ORDER BY lessonNumber ASC")
    LiveData<List<TrainingLessonEntity>> getLessonsForModule(String moduleId);

    @Query("SELECT * FROM training_lessons WHERE moduleId = :moduleId ORDER BY lessonNumber ASC")
    List<TrainingLessonEntity> getLessonsForModuleSync(String moduleId);

    @Query("SELECT * FROM training_lessons WHERE lessonId = :lessonId LIMIT 1")
    LiveData<TrainingLessonEntity> getLessonById(String lessonId);
}
