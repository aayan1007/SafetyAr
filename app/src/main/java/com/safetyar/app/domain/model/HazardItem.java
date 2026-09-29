package com.safetyar.app.domain.model;

import java.io.Serializable;

/**
 * Represents a spatial hazard simulated in the AR environment.
 */
public class HazardItem implements Serializable {
    private String id;
    private String title;
    private String description;
    private String sopActionRequired;
    private IndustrySector sector;
    private Severity severity;
    private float posX; // Normalized AR space coordinates
    private float posY;
    private float posZ;
    private boolean isIdentified;
    private boolean isMitigated;

    public enum Severity {
        CRITICAL(3, "CRITICAL HAZARD", "#D32F2F"),
        HIGH(2, "HIGH RISK", "#F57C00"),
        MODERATE(1, "SOP VIOLATION", "#FBC02D");

        private final int level;
        private final String label;
        private final String colorHex;

        Severity(int level, String label, String colorHex) {
            this.level = level;
            this.label = label;
            this.colorHex = colorHex;
        }

        public int getLevel() {
            return level;
        }

        public String getLabel() {
            return label;
        }

        public String getColorHex() {
            return colorHex;
        }
    }

    public HazardItem(String id, String title, String description, String sopActionRequired,
                      IndustrySector sector, Severity severity, float posX, float posY, float posZ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.sopActionRequired = sopActionRequired;
        this.sector = sector;
        this.severity = severity;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.isIdentified = false;
        this.isMitigated = false;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSopActionRequired() {
        return sopActionRequired;
    }

    public IndustrySector getSector() {
        return sector;
    }

    public Severity getSeverity() {
        return severity;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getPosZ() {
        return posZ;
    }

    public boolean isIdentified() {
        return isIdentified;
    }

    public void setIdentified(boolean identified) {
        isIdentified = identified;
    }

    public boolean isMitigated() {
        return isMitigated;
    }

    public void setMitigated(boolean mitigated) {
        isMitigated = mitigated;
    }
}
