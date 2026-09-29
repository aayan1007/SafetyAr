package com.safetyar.app.ar;

public class GasConfinedSpaceStateManager {

    public enum Step {
        STEP_1_IDENTIFY_GAS_LEAK(1, "Identify Gas Leak Source"),
        STEP_2_IDENTIFY_DANGER_ZONE(2, "Demarcate Gas Danger Zone"),
        STEP_3_AVOID_DANGER_ZONE(3, "Establish Safe Standoff Perimeter"),
        STEP_4_IDENTIFY_CONFINED_SPACE(4, "Locate Confined Space Entrance"),
        STEP_5_SELECT_PPE(5, "Equip SCBA & Safety Retrieval Harness"),
        STEP_6_USE_GAS_DETECTOR(6, "Atmospheric Testing (4-Gas Detector)"),
        STEP_7_FOLLOW_BUDDY_SYSTEM(7, "Station Safety Standby Buddy"),
        STEP_8_VERIFY_AUTHORIZATION(8, "Verify Entry Permit-to-Work"),
        STEP_9_SAFE_ENTRY_PROCEDURE(9, "Execute Controlled Tethered Entry"),
        STEP_10_EMERGENCY_EVACUATION(10, "Rapid Emergency Evacuation");

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
        ACTION_TAP_GAS_LEAK,
        ACTION_TAP_DANGER_ZONE,
        ACTION_ESTABLISH_SAFE_STANDOFF,
        ACTION_TAP_CONFINED_ENTRANCE,
        ACTION_SELECT_DUST_MASK,      // Unsafe action
        ACTION_SELECT_SCBA_PPE,       // Correct PPE
        ACTION_USE_GAS_DETECTOR,
        ACTION_VERIFY_BUDDY,
        ACTION_SOLO_ENTRY_ATTEMPT,    // Unsafe action
        ACTION_VERIFY_PERMIT,
        ACTION_SAFE_ENTRY,
        ACTION_UNSAFE_PREMATURE_ENTRY,// Unsafe action
        ACTION_EMERGENCY_EVACUATE
    }

    public static class StepResult {
        public final boolean isCorrect;
        public final String feedbackMessage;
        public final Step currentStep;
        public final int score;
        public final boolean isFinished;
        public final boolean passed;
        public final boolean isUnsafeAction;

        public StepResult(boolean isCorrect, String feedbackMessage, Step currentStep,
                          int score, boolean isFinished, boolean passed, boolean isUnsafeAction) {
            this.isCorrect = isCorrect;
            this.feedbackMessage = feedbackMessage;
            this.currentStep = currentStep;
            this.score = score;
            this.isFinished = isFinished;
            this.passed = passed;
            this.isUnsafeAction = isUnsafeAction;
        }
    }

    private Step currentStep = Step.STEP_1_IDENTIFY_GAS_LEAK;
    private int score = 100;
    private boolean isFinished = false;

    // Sub-state flags
    private boolean isGasLeakIdentified = false;
    private boolean isDangerZoneIdentified = false;
    private boolean isSafeStandoffEstablished = false;
    private boolean isConfinedSpaceIdentified = false;
    private boolean isPpeEquipped = false;
    private boolean isGasTested = false;
    private boolean isBuddyStationed = false;
    private boolean isPermitAuthorized = false;
    private boolean isEntryCompleted = false;
    private boolean isEvacuated = false;

    public void reset() {
        currentStep = Step.STEP_1_IDENTIFY_GAS_LEAK;
        score = 100;
        isFinished = false;
        isGasLeakIdentified = false;
        isDangerZoneIdentified = false;
        isSafeStandoffEstablished = false;
        isConfinedSpaceIdentified = false;
        isPpeEquipped = false;
        isGasTested = false;
        isBuddyStationed = false;
        isPermitAuthorized = false;
        isEntryCompleted = false;
        isEvacuated = false;
    }

    public StepResult processAction(UserAction action) {
        if (isFinished) {
            return new StepResult(true, "Scenario already completed.", currentStep, score, true, score >= 80, false);
        }

        // Global Unsafe Action Check: Premature entry before checks
        if (action == UserAction.ACTION_UNSAFE_PREMATURE_ENTRY) {
            score = Math.max(0, score - 25);
            return new StepResult(
                    false,
                    "UNSAFE ACTION: Do not enter without required authorization and atmospheric testing.",
                    currentStep,
                    score,
                    false,
                    false,
                    true
            );
        }

        switch (currentStep) {
            case STEP_1_IDENTIFY_GAS_LEAK:
                if (action == UserAction.ACTION_TAP_GAS_LEAK) {
                    isGasLeakIdentified = true;
                    currentStep = Step.STEP_2_IDENTIFY_DANGER_ZONE;
                    return new StepResult(true, "Correct. Gas leak source identified at pipe flange.", currentStep, score, false, false, false);
                } else if (action == UserAction.ACTION_TAP_CONFINED_ENTRANCE || action == UserAction.ACTION_SAFE_ENTRY) {
                    score = Math.max(0, score - 20);
                    return new StepResult(false, "UNSAFE ACTION: Do not enter without required authorization and atmospheric testing.", currentStep, score, false, false, true);
                } else {
                    return new StepResult(false, "Locate and tap the leaking gas flange first.", currentStep, score, false, false, false);
                }

            case STEP_2_IDENTIFY_DANGER_ZONE:
                if (action == UserAction.ACTION_TAP_DANGER_ZONE) {
                    isDangerZoneIdentified = true;
                    currentStep = Step.STEP_3_AVOID_DANGER_ZONE;
                    return new StepResult(true, "Correct. Red hazard boundary identified around toxic gas cloud.", currentStep, score, false, false, false);
                } else {
                    return new StepResult(false, "Identify the red circular gas hazard zone boundary.", currentStep, score, false, false, false);
                }

            case STEP_3_AVOID_DANGER_ZONE:
                if (action == UserAction.ACTION_ESTABLISH_SAFE_STANDOFF) {
                    isSafeStandoffEstablished = true;
                    currentStep = Step.STEP_4_IDENTIFY_CONFINED_SPACE;
                    return new StepResult(true, "Safe standoff established upwind from flammable gas zone.", currentStep, score, false, false, false);
                } else {
                    return new StepResult(false, "Establish safe standoff upwind before proceeding.", currentStep, score, false, false, false);
                }

            case STEP_4_IDENTIFY_CONFINED_SPACE:
                if (action == UserAction.ACTION_TAP_CONFINED_ENTRANCE) {
                    isConfinedSpaceIdentified = true;
                    currentStep = Step.STEP_5_SELECT_PPE;
                    return new StepResult(true, "Confined space entrance located. Mandatory pre-entry controls active.", currentStep, score, false, false, false);
                } else {
                    return new StepResult(false, "Locate the confined space manhole/portal entrance.", currentStep, score, false, false, false);
                }

            case STEP_5_SELECT_PPE:
                if (action == UserAction.ACTION_SELECT_SCBA_PPE) {
                    isPpeEquipped = true;
                    currentStep = Step.STEP_6_USE_GAS_DETECTOR;
                    return new StepResult(true, "Self-Contained Breathing Apparatus (SCBA) & retrieval harness equipped.", currentStep, score, false, false, false);
                } else if (action == UserAction.ACTION_SELECT_DUST_MASK) {
                    score = Math.max(0, score - 20);
                    return new StepResult(false, "UNSAFE ACTION: Dust masks provide ZERO protection against toxic gases or oxygen deficiency! Equip SCBA.", currentStep, score, false, false, true);
                } else {
                    return new StepResult(false, "Equip certified breathing apparatus at the PPE station.", currentStep, score, false, false, false);
                }

            case STEP_6_USE_GAS_DETECTOR:
                if (action == UserAction.ACTION_USE_GAS_DETECTOR) {
                    isGasTested = true;
                    currentStep = Step.STEP_7_FOLLOW_BUDDY_SYSTEM;
                    return new StepResult(true, "Atmospheric testing complete (O2: 20.9%, CH4: 0%, H2S: 0 ppm, CO: 0 ppm). Atmosphere certified breathable.", currentStep, score, false, false, false);
                } else if (action == UserAction.ACTION_SAFE_ENTRY || action == UserAction.ACTION_TAP_CONFINED_ENTRANCE) {
                    score = Math.max(0, score - 25);
                    return new StepResult(false, "UNSAFE ACTION: Do not enter without required authorization and atmospheric testing.", currentStep, score, false, false, true);
                } else {
                    return new StepResult(false, "Activate the 4-gas detector to sample internal atmosphere.", currentStep, score, false, false, false);
                }

            case STEP_7_FOLLOW_BUDDY_SYSTEM:
                if (action == UserAction.ACTION_VERIFY_BUDDY) {
                    isBuddyStationed = true;
                    currentStep = Step.STEP_8_VERIFY_AUTHORIZATION;
                    return new StepResult(true, "Qualified safety observer stationed outside with lifeline and air monitor.", currentStep, score, false, false, false);
                } else if (action == UserAction.ACTION_SOLO_ENTRY_ATTEMPT) {
                    score = Math.max(0, score - 25);
                    return new StepResult(false, "UNSAFE ACTION: Solitary entry into confined spaces is strictly prohibited by DGMS! Station a qualified safety buddy at the entrance.", currentStep, score, false, false, true);
                } else {
                    return new StepResult(false, "Verify that your standby safety buddy is stationed at the portal.", currentStep, score, false, false, false);
                }

            case STEP_8_VERIFY_AUTHORIZATION:
                if (action == UserAction.ACTION_VERIFY_PERMIT) {
                    isPermitAuthorized = true;
                    currentStep = Step.STEP_9_SAFE_ENTRY_PROCEDURE;
                    return new StepResult(true, "Confined Space Entry Permit (DGMS Form IV) verified and countersigned by Safety Officer.", currentStep, score, false, false, false);
                } else if (action == UserAction.ACTION_SAFE_ENTRY) {
                    score = Math.max(0, score - 20);
                    return new StepResult(false, "UNSAFE ACTION: Do not enter without required authorization and atmospheric testing.", currentStep, score, false, false, true);
                } else {
                    return new StepResult(false, "Verify signed supervisor authorization permit before entering.", currentStep, score, false, false, false);
                }

            case STEP_9_SAFE_ENTRY_PROCEDURE:
                if (action == UserAction.ACTION_SAFE_ENTRY) {
                    isEntryCompleted = true;
                    currentStep = Step.STEP_10_EMERGENCY_EVACUATION;
                    return new StepResult(true, "Tethered entry performed successfully with continuous monitoring. Sudden gas leak alarm triggered! EVACUATE NOW!", currentStep, score, false, false, false);
                } else {
                    return new StepResult(false, "Execute controlled entry with connected retrieval lifeline.", currentStep, score, false, false, false);
                }

            case STEP_10_EMERGENCY_EVACUATION:
                if (action == UserAction.ACTION_EMERGENCY_EVACUATE) {
                    isEvacuated = true;
                    isFinished = true;
                    boolean passed = score >= 80;
                    return new StepResult(true, "Emergency evacuation executed safely along marked route to assembly area.", currentStep, score, true, passed, false);
                } else {
                    return new StepResult(false, "Follow the emergency escape route arrows to the safe assembly point immediately!", currentStep, score, false, false, false);
                }

            default:
                return new StepResult(false, "Invalid state transition.", currentStep, score, false, false, false);
        }
    }

    // State getters
    public Step getCurrentStep() {
        return currentStep;
    }

    public int getScore() {
        return score;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isGasLeakIdentified() {
        return isGasLeakIdentified;
    }

    public boolean isDangerZoneIdentified() {
        return isDangerZoneIdentified;
    }

    public boolean isSafeStandoffEstablished() {
        return isSafeStandoffEstablished;
    }

    public boolean isConfinedSpaceIdentified() {
        return isConfinedSpaceIdentified;
    }

    public boolean isPpeEquipped() {
        return isPpeEquipped;
    }

    public boolean isGasTested() {
        return isGasTested;
    }

    public boolean isBuddyStationed() {
        return isBuddyStationed;
    }

    public boolean isPermitAuthorized() {
        return isPermitAuthorized;
    }

    public boolean isEntryCompleted() {
        return isEntryCompleted;
    }

    public boolean isEvacuated() {
        return isEvacuated;
    }
}
