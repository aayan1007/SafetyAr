package com.safetyar.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.safetyar.app.data.local.AppDatabase;
import com.safetyar.app.data.local.dao.ARScenarioDao;
import com.safetyar.app.data.local.dao.QuestionDao;
import com.safetyar.app.data.local.dao.TrainingLessonDao;
import com.safetyar.app.data.local.entity.ARScenarioEntity;
import com.safetyar.app.data.local.entity.QuestionEntity;
import com.safetyar.app.data.local.entity.TrainingLessonEntity;

import java.util.List;

public class ARScenarioRepository {

    private final ARScenarioDao arScenarioDao;
    private final QuestionDao questionDao;
    private final TrainingLessonDao trainingLessonDao;

    public ARScenarioRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        arScenarioDao = db.arScenarioDao();
        questionDao = db.questionDao();
        trainingLessonDao = db.trainingLessonDao();
    }

    public LiveData<ARScenarioEntity> getScenarioByModuleId(String moduleId) {
        return arScenarioDao.getScenarioByModuleId(moduleId);
    }

    public LiveData<List<ARScenarioEntity>> getScenariosBySector(String sector) {
        return arScenarioDao.getScenariosBySector(sector);
    }

    public LiveData<List<QuestionEntity>> getQuestionsForModule(String moduleId) {
        return questionDao.getQuestionsForModule(moduleId);
    }

    public LiveData<List<TrainingLessonEntity>> getLessonsForModule(String moduleId) {
        return trainingLessonDao.getLessonsForModule(moduleId);
    }
}
