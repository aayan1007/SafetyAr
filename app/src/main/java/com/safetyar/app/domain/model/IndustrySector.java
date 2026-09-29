package com.safetyar.app.domain.model;

/**
 * Industrial sectors in Jharkhand targeted by SafetyAR.
 */
public enum IndustrySector {
    COAL_MINING("Coal Mining", "Underground & Opencast Coal Mines (DGMS Standard)", "Dhanbad, Bokaro, Ramgarh"),
    STEEL_MANUFACTURING("Steel & Metallurgy", "Blast Furnaces, Rolling Mills & Casting Plants", "Jamshedpur, Bokaro"),
    MICA_PROCESSING("Mica Processing", "Mica Flake Sorting, Cutting & Silicosis Control", "Koderma, Giridih");

    private final String displayName;
    private final String description;
    private final String miningBelt;

    IndustrySector(String displayName, String description, String miningBelt) {
        this.displayName = displayName;
        this.description = description;
        this.miningBelt = miningBelt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getMiningBelt() {
        return miningBelt;
    }
}
