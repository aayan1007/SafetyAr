package com.safetyar.app.domain.orientation;

import java.util.concurrent.TimeUnit;

/**
 * Domain utility for tracking the statutory 30-day industrial onboarding
 * and safety orientation mandate for underground mining and heavy industrial units.
 */
public class OrientationTracker {

    public static final int MANDATORY_ORIENTATION_DAYS = 30;

    /**
     * Calculates the current orientation day (1 to 30) based on start timestamp.
     */
    public static int calculateCurrentDay(long startTimestamp, long currentTimestamp) {
        if (currentTimestamp < startTimestamp) {
            return 1;
        }
        long diffMillis = currentTimestamp - startTimestamp;
        long daysDiff = TimeUnit.MILLISECONDS.toDays(diffMillis);
        int day = (int) daysDiff + 1; // 1-indexed
        return Math.min(Math.max(day, 1), MANDATORY_ORIENTATION_DAYS);
    }

    /**
     * Calculates orientation completion percentage (0% to 100%).
     */
    public static int calculateCompletionPercentage(int daysCompleted) {
        if (daysCompleted <= 0) return 0;
        if (daysCompleted >= MANDATORY_ORIENTATION_DAYS) return 100;
        return (int) Math.round(((double) daysCompleted / MANDATORY_ORIENTATION_DAYS) * 100.0);
    }

    /**
     * Determines whether the statutory orientation requirement is satisfied.
     */
    public static boolean isOrientationCompleted(int daysCompleted) {
        return daysCompleted >= MANDATORY_ORIENTATION_DAYS;
    }

    /**
     * Returns remaining days required before autonomous working authorization.
     */
    public static int getRemainingDays(int currentDay) {
        return Math.max(0, MANDATORY_ORIENTATION_DAYS - currentDay);
    }

    /**
     * Returns the DGMS curriculum phase for the given orientation day.
     */
    public static String getOrientationPhase(int currentDay) {
        if (currentDay <= 7) {
            return "Phase 1: Mine Surface Induction, PPE Compliance & Strata Basics";
        } else if (currentDay <= 15) {
            return "Phase 2: Underground Mine Ventilation, Methane & Gas Detection";
        } else if (currentDay <= 22) {
            return "Phase 3: Armored Haulage, Machinery Isolation & LOTO Procedures";
        } else {
            return "Phase 4: Emergency Self-Rescuer Donning & Final Certification Audit";
        }
    }
}
