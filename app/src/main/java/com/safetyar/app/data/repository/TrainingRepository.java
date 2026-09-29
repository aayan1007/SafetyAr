package com.safetyar.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.safetyar.app.data.local.AppDatabase;
import com.safetyar.app.data.local.dao.TrainingAssignmentDao;
import com.safetyar.app.data.local.dao.TrainingLessonDao;
import com.safetyar.app.data.local.dao.TrainingModuleDao;
import com.safetyar.app.data.local.entity.TrainingAssignmentEntity;
import com.safetyar.app.data.local.entity.TrainingLessonEntity;
import com.safetyar.app.data.local.entity.TrainingModuleEntity;

import java.util.List;

public class TrainingRepository {
    private final TrainingModuleDao moduleDao;
    private final TrainingLessonDao lessonDao;
    private final TrainingAssignmentDao assignmentDao;
    private final LiveData<List<TrainingModuleEntity>> allModules;

    public TrainingRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        moduleDao = db.trainingModuleDao();
        lessonDao = db.trainingLessonDao();
        assignmentDao = db.trainingAssignmentDao();
        allModules = moduleDao.getAllModules();
    }

    public LiveData<List<TrainingModuleEntity>> getAllModules() {
        return allModules;
    }

    public LiveData<List<TrainingModuleEntity>> getModulesBySector(String sector) {
        return moduleDao.getModulesBySector(sector);
    }

    public LiveData<TrainingModuleEntity> getModuleById(String moduleId) {
        return moduleDao.getModuleById(moduleId);
    }

    public LiveData<List<TrainingLessonEntity>> getLessonsForModule(String moduleId) {
        return lessonDao.getLessonsForModule(moduleId);
    }

    public LiveData<TrainingAssignmentEntity> getActiveAssignment(String workerId) {
        return assignmentDao.getActiveAssignment(workerId);
    }

    public void markModuleCompleted(String moduleId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            TrainingModuleEntity module = moduleDao.getModuleByIdSync(moduleId);
            if (module != null) {
                module.setCompleted(true);
                moduleDao.updateModule(module);
            }
        });
    }

    public LiveData<Integer> getCompletedModulesCount() {
        return moduleDao.getCompletedModulesCount();
    }
}
