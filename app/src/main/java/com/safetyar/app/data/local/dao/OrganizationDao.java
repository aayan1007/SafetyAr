package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.safetyar.app.data.local.entity.OrganizationEntity;

import java.util.List;

@Dao
public interface OrganizationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrganization(OrganizationEntity org);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrganizations(List<OrganizationEntity> orgs);

    @Query("SELECT * FROM organizations WHERE orgId = :orgId LIMIT 1")
    LiveData<OrganizationEntity> getOrganizationById(String orgId);

    @Query("SELECT * FROM organizations WHERE orgId = :orgId LIMIT 1")
    OrganizationEntity getOrganizationByIdSync(String orgId);

    @Query("SELECT * FROM organizations ORDER BY orgName ASC")
    LiveData<List<OrganizationEntity>> getAllOrganizations();
}
