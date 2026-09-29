package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.TrainingAssignmentEntity;

import java.util.List;

@Dao
public interface TrainingAssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAssignments(List<TrainingAssignmentEntity> assignments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAssignment(TrainingAssignmentEntity assignment);

    @Update
    void updateAssignment(TrainingAssignmentEntity assignment);

    @Query("SELECT * FROM training_assignments WHERE workerId = :workerId ORDER BY dueDate ASC")
    LiveData<List<TrainingAssignmentEntity>> getAssignmentsForWorker(String workerId);

    @Query("SELECT * FROM training_assignments WHERE workerId = :workerId AND status = 'ASSIGNED' LIMIT 1")
    LiveData<TrainingAssignmentEntity> getActiveAssignment(String workerId);

    @Query("SELECT * FROM training_assignments WHERE workerId = :workerId AND status = 'ASSIGNED' LIMIT 1")
    TrainingAssignmentEntity getActiveAssignmentSync(String workerId);
}
