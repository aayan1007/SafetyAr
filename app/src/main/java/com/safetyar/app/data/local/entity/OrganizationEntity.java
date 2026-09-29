package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "organizations")
public class OrganizationEntity {
    @PrimaryKey
    @NonNull
    private String orgId;
    private String orgName;
    private String sector;       // COAL_MINING, STEEL_MANUFACTURING, MICA_PROCESSING
    private String headquarters; // e.g. "Koyla Bhawan, Dhanbad"
    private String contactNumber;
    private String dgmsZone;     // e.g. "DGMS Eastern Zone (Ranchi/Dhanbad)"

    public OrganizationEntity(@NonNull String orgId, String orgName, String sector,
                              String headquarters, String contactNumber, String dgmsZone) {
        this.orgId = orgId;
        this.orgName = orgName;
        this.sector = sector;
        this.headquarters = headquarters;
        this.contactNumber = contactNumber;
        this.dgmsZone = dgmsZone;
    }

    @NonNull
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(@NonNull String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDgmsZone() {
        return dgmsZone;
    }

    public void setDgmsZone(String dgmsZone) {
        this.dgmsZone = dgmsZone;
    }
}
