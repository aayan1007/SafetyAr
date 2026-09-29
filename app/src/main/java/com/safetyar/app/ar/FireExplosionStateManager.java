package com.safetyar.app.ar;

public class FireExplosionStateManager {

    public enum Step {
        STEP_1_RECOGNIZE_FIRE(1, "Recognize Fire Hazard Zone"),
        STEP_2_IDENTIFY_ALARM(2, "Locate Emergency Alarm"),
        STEP_3_ACTIVATE_ALARM(3, "Activate Emergency Alarm Siren"),
        STEP_4_SELECT_EXTINGUISHER(4, "Select DCP Fire Extinguisher"),
        STEP_5_PASS_PULL(5, "PASS: Pull Safety Pin"),
        STEP_6_PASS_AIM(6, "PASS: Aim Nozzle at Base of Fire"),
        STEP_7_PASS_SQUEEZE(7, "PASS: Squeeze Trigger Lever"),
        STEP_8_PASS_SWEEP(8, "PASS: Sweep Side-to-Side"),
        STEP_9_FOLLOW_EXIT_ROUTE(9, "Follow Marked Evacuation Route"),
        STEP_10_REACH_ASSEMBLY(10, "Reach Safe Assembly Point");

        private final int stepNumber;
        private final String title;

        Step(int stepNumber, String title) {
            this.stepNumber = stepNumber;
            this.title = title;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public String getTitle() {
            return title;
        }
    }

    public enum UserAction {
        ACTION_TAP_FIRE,
        ACTION_TAP_ALARM,
        ACTION_ACTIVATE_ALARM,
        ACTION_SELECT_DCP_EXTINGUISHER,
        ACTION_SELECT_WATER_EXTINGUISHER,
        ACTION_PASS_PULL,
        ACTION_PASS_AIM,
        ACTION_PASS_SQUEEZE,
        ACTION_PASS_SWEEP,
        ACTION_TAP_EXIT_ROUTE,
        ACTION_TAP_ASSEMBLY_POINT
    }

    public static class StepResult {
        public final boolean isCorrect;
        public final String feedbackMessage;
        public final Step currentStep;
        public final int score;
        public final boolean isFinished;
        public final boolean passed;

        public StepResult(boolean isCorrect, String feedbackMessage, Step currentStep,
                          int score, boolean isFinished, boolean passed) {
            this.isCorrect = isCorrect;
            this.feedbackMessage = feedbackMessage;
            this.currentStep = currentStep;
            this.score = score;
            this.isFinished = isFinished;
            this.passed = passed;
        }
    }

    private Step currentStep = Step.STEP_1_RECOGNIZE_FIRE;
    private int score = 100;
    private boolean isFinished = false;

    // Extinguisher state
    private boolean isPinPulled = false;
    private boolean isAimedAtBase = false;
    private boolean isSqueezed = false;
    private boolean isFireExtinguished = false;
    private boolean isAlarmSounded = false;

    public StepResult processAction(UserAction action) {
        if (isFinished) {
            return new StepResult(true, "Scenario already completed.", currentStep, score, true, score >= 80);
        }

        switch (currentStep) {
            case STEP_1_RECOGNIZE_FIRE:
                if (action == UserAction.ACTION_TAP_FIRE) {
                    currentStep = Step.STEP_2_IDENTIFY_ALARM;
                    return new StepResult(true, "Correct action. Fire hazard recognized (3m Standoff Perimeter established).", currentStep, score, false, false);
                } else if (action == UserAction.ACTION_PASS_PULL || action == UserAction.ACTION_SELECT_DCP_EXTINGUISHER) {
                    score = Math.max(0, score - 15);
                    return new StepResult(false, "Unsafe action. Raise the emergency alarm first before attempting suppression.", currentStep, score, false, false);
                } else {
                    return new StepResult(false, "Identify the active fire hazard first.", currentStep, score, false, false);
                }

            case STEP_2_IDENTIFY_ALARM:
                if (action == UserAction.ACTION_TAP_ALARM) {
                    currentStep = Step.STEP_3_ACTIVATE_ALARM;
                    return new StepResult(true, "Correct action. Manual Call Point located.", currentStep, score, false, false);
                } else {
                    return new StepResult(false, "Locate the emergency pull alarm station.", currentStep, score, false, false);
                }

            case STEP_3_ACTIVATE_ALARM:
                if (action == UserAction.ACTION_ACTIVATE_ALARM) {
                    isAlarmSounded = true;
                    currentStep = Step.STEP_4_SELECT_EXTINGUISHER;
                    return new StepResult(true, "Correct action. Emergency siren activated. Rescue team alerted.", currentStep, score, false, false);
                } else {
                    return new StepResult(false, "Unsafe action. Break glass / pull alarm lever to sound siren.", currentStep, score, false, false);
                }

            case STEP_4_SELECT_EXTINGUISHER:
                if (action == UserAction.ACTION_SELECT_DCP_EXTINGUISHER) {
                    currentStep = Step.STEP_5_PASS_PULL;
                    return new StepResult(true, "Correct action. Dry Chemical Powder (DCP) selected for industrial fire.", currentStep, score, false, false);
                } else if (action == UserAction.ACTION_SELECT_WATER_EXTINGUISHER) {
                    score = Math.max(0, score - 20);
                    return new StepResult(false, "Incorrect. Select the appropriate extinguisher (DCP / CO2). Water conducts electrical & chemical flashover.", currentStep, score, false, false);
                } else {
                    return new StepResult(false, "Select the correct fire extinguisher type from the station.", currentStep, score, false, false);
                }

            case STEP_5_PASS_PULL:
                if (action == UserAction.ACTION_PASS_PULL) {
                    isPinPulled = true;
                    currentStep = Step.STEP_6_PASS_AIM;
                    return new StepResult(true, "Correct action. (P - Pull) Safety pin removed.", currentStep, score, false, false);
                } else if (action == UserAction.ACTION_PASS_SQUEEZE) {
                    score = Math.max(0, score - 10);
                    return new StepResult(false, "Incorrect. Pull the safety pin first before squeezing lever.", currentStep, score, false, false);
                } else {
                    return new StepResult(false, "Follow PASS protocol: Pull the safety pin.", currentStep, score, false, false);
                }

            case STEP_6_PASS_AIM:
                if (action == UserAction.ACTION_PASS_AIM) {
                    isAimedAtBase = true;
                    currentStep = Step.STEP_7_PASS_SQUEEZE;
                    return new StepResult(true, "Correct action. (A - Aim) Nozzle aimed directly at base of flames.", currentStep, score, false, false);
                } else {
                    return new StepResult(false, "Aim nozzle at base of the fire, not the top of the flames.", currentStep, score, false, false);
                }

            case STEP_7_PASS_SQUEEZE:
                if (action == UserAction.ACTION_PASS_SQUEEZE) {
                    isSqueezed = true;
                    currentStep = Step.STEP_8_PASS_SWEEP;
                    return new StepResult(true, "Correct action. (S - Squeeze) Continuous discharge initiated.", currentStep, score, false, false);
                } else {
                    return new StepResult(false, "Squeeze the operating lever to release extinguishing agent.", currentStep, score, false, false);
                }

            case STEP_8_PASS_SWEEP:
                if (action == UserAction.ACTION_PASS_SWEEP) {
                    isFireExtinguished = true;
                    currentStep = Step.STEP_9_FOLLOW_EXIT_ROUTE;
                    return new StepResult(true, "Correct action. (S - Sweep) Side-to-side motion smothered flames. Fire Neutralized!", currentStep, score, false, false);
                } else {
                    return new StepResult(false, "Sweep nozzle side-to-side across the base of fire.", currentStep, score, false, false);
                }

            case STEP_9_FOLLOW_EXIT_ROUTE:
                if (action == UserAction.ACTION_TAP_EXIT_ROUTE) {
                    currentStep = Step.STEP_10_REACH_ASSEMBLY;
                    return new StepResult(true, "Correct action. Evacuating along marked green directional route.", currentStep, score, false, false);
                } else {
                    score = Math.max(0, score - 10);
                    return new StepResult(false, "Unsafe action. Evacuate through the marked emergency route arrows.", currentStep, score, false, false);
                }

            case STEP_10_REACH_ASSEMBLY:
                if (action == UserAction.ACTION_TAP_ASSEMBLY_POINT) {
                    isFinished = true;
                    boolean passed = score >= 80;
                    return new StepResult(true, "Correct action. Safe Assembly Point reached. Headcount confirmed.", currentStep, score, true, passed);
                } else {
                    return new StepResult(false, "Proceed to the green Muster Assembly Point sign.", currentStep, score, false, false);
                }

            default:
                return new StepResult(false, "Invalid action.", currentStep, score, false, false);
        }
    }

    public Step getCurrentStep() {
        return currentStep;
    }

    public int getScore() {
        return score;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isAlarmSounded() {
        return isAlarmSounded;
    }

    public boolean isFireExtinguished() {
        return isFireExtinguished;
    }

    public boolean isPinPulled() {
        return isPinPulled;
    }

    public boolean isAimedAtBase() {
        return isAimedAtBase;
    }

    public boolean isSqueezed() {
        return isSqueezed;
    }
}
