package com.safetyar.app.domain;

import com.safetyar.app.domain.orientation.OrientationTracker;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class OrientationTrackerTest {

    @Test
    public void testCalculateCurrentDay() {
        long now = System.currentTimeMillis();

        // Same day -> Day 1
        int day1 = OrientationTracker.calculateCurrentDay(now, now);
        assertEquals(1, day1);

        // 11 days later -> Day 12 (Shubham Mahindre's demo state)
        long day12Time = now + TimeUnit.DAYS.toMillis(11);
        int day12 = OrientationTracker.calculateCurrentDay(now, day12Time);
        assertEquals(12, day12);

        // 45 days later -> Capped at 30 days
        long day45Time = now + TimeUnit.DAYS.toMillis(45);
        int day30 = OrientationTracker.calculateCurrentDay(now, day45Time);
        assertEquals(30, day30);
    }

    @Test
    public void testCalculateCompletionPercentage() {
        assertEquals(0, OrientationTracker.calculateCompletionPercentage(0));
        assertEquals(40, OrientationTracker.calculateCompletionPercentage(12)); // 12 / 30 = 40%
        assertEquals(50, OrientationTracker.calculateCompletionPercentage(15)); // 15 / 30 = 50%
        assertEquals(100, OrientationTracker.calculateCompletionPercentage(30)); // 30 / 30 = 100%
        assertEquals(100, OrientationTracker.calculateCompletionPercentage(35)); // capped at 100%
    }

    @Test
    public void testIsOrientationCompleted() {
        assertFalse(OrientationTracker.isOrientationCompleted(0));
        assertFalse(OrientationTracker.isOrientationCompleted(12));
        assertFalse(OrientationTracker.isOrientationCompleted(29));
        assertTrue(OrientationTracker.isOrientationCompleted(30));
        assertTrue(OrientationTracker.isOrientationCompleted(31));
    }

    @Test
    public void testGetRemainingDays() {
        assertEquals(30, OrientationTracker.getRemainingDays(0));
        assertEquals(18, OrientationTracker.getRemainingDays(12)); // 30 - 12 = 18
        assertEquals(0, OrientationTracker.getRemainingDays(30));
        assertEquals(0, OrientationTracker.getRemainingDays(35));
    }

    @Test
    public void testOrientationPhases() {
        assertTrue(OrientationTracker.getOrientationPhase(3).contains("Phase 1"));
        assertTrue(OrientationTracker.getOrientationPhase(12).contains("Phase 2"));
        assertTrue(OrientationTracker.getOrientationPhase(18).contains("Phase 3"));
        assertTrue(OrientationTracker.getOrientationPhase(28).contains("Phase 4"));
    }
}
