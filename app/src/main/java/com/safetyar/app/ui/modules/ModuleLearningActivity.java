package com.safetyar.app.ui.modules;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.safetyar.app.R;
import com.safetyar.app.data.local.AppDatabase;
import com.safetyar.app.data.local.entity.TrainingProgressEntity;
import com.safetyar.app.data.repository.WorkerRepository;
import com.safetyar.app.databinding.ActivityModuleLearningBinding;
import com.safetyar.app.ui.ar.FireExplosionArActivity;
import com.safetyar.app.ui.assessment.AssessmentEngineActivity;
import com.safetyar.app.ui.certificate.CertificateActivity;
import com.safetyar.app.util.LocaleHelper;

import java.util.UUID;

public class ModuleLearningActivity extends AppCompatActivity {

    private ActivityModuleLearningBinding binding;
    private WorkerRepository workerRepository;
    private int currentStep = 1;
    private static final int TOTAL_STEPS = 11;
    private String moduleId = "MOD-FIRE-01";
    private String workerId = "WRK-JH-COAL-0891";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModuleLearningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workerRepository = new WorkerRepository(getApplication());
        workerRepository.getActiveWorker().observe(this, worker -> {
            if (worker != null) {
                workerId = worker.getWorkerId();
            }
        });

        if (getIntent().hasExtra("MODULE_ID")) {
            moduleId = getIntent().getStringExtra("MODULE_ID");
        }

        setupButtons();
        renderStep(currentStep);
    }

    private void setupButtons() {
        binding.btnBackFromLearning.setOnClickListener(v -> finish());

        binding.btnPreviousStep.setOnClickListener(v -> {
            if (currentStep > 1) {
                currentStep--;
                renderStep(currentStep);
            }
        });

        binding.btnNextStep.setOnClickListener(v -> {
            if (currentStep < TOTAL_STEPS) {
                currentStep++;
                renderStep(currentStep);
                updateProgress(currentStep);
            } else {
                // Completed 11 steps -> navigate to Certificate
                updateProgress(TOTAL_STEPS);
                Intent intent = new Intent(ModuleLearningActivity.this, CertificateActivity.class);
                intent.putExtra("SECTOR_NAME", "COAL_MINING");
                startActivity(intent);
                finish();
            }
        });

        binding.btnLaunchArAction.setOnClickListener(v -> {
            if (currentStep == 6 || currentStep == 7) {
                Intent intent = new Intent(ModuleLearningActivity.this, FireExplosionArActivity.class);
                intent.putExtra("MODULE_ID", moduleId);
                intent.putExtra("MODE", "PRACTICE");
                startActivity(intent);
            } else if (currentStep == 8) {
                Intent intent = new Intent(ModuleLearningActivity.this, AssessmentEngineActivity.class);
                intent.putExtra("MODULE_ID", moduleId);
                intent.putExtra("SECTOR_NAME", "COAL_MINING");
                intent.putExtra("PASS_THRESHOLD", 70);
                startActivity(intent);
            } else if (currentStep == 11) {
                Intent intent = new Intent(ModuleLearningActivity.this, CertificateActivity.class);
                intent.putExtra("SECTOR_NAME", "COAL_MINING");
                startActivity(intent);
            }
        });
    }

    private void renderStep(int step) {
        binding.btnPreviousStep.setEnabled(step > 1);
        int progress = (int) (((float) step / TOTAL_STEPS) * 100);
        binding.progressLearningStepper.setProgress(progress);
        binding.tvStepNumberBadge.setText(String.format("STEP %d OF %d", step, TOTAL_STEPS));

        if (step == TOTAL_STEPS) {
            binding.btnNextStep.setText("Finish & View Safety Pass \u2192");
        } else {
            binding.btnNextStep.setText("Next Step \u2192");
        }

        switch (step) {
            case 1:
                binding.tvLearningStepIndicator.setText("Step 1 of 11: Introduction");
                binding.tvStepTitle.setText("1. Introduction: Thermal & Explosion Risks");
                binding.tvStepBody.setText(
                        "Underground coal seams and metallurgy facilities contain severe combustible hazards, including spontaneous coal dust ignition and volatile methane pockets.\n\n" +
                        "According to DGMS (Directorate General of Mines Safety) Circular No. 3 of 2019 and Coal Mines Regulations (CMR 2017) Rule 115:\n\n" +
                        "• Any untamed spark or ignition must be controlled within the initial 60 seconds.\n" +
                        "• Miners and plant technicians must recognize the thermal hazard instantly, alert fellow workers, and follow strict sequential containment protocols.\n\n" +
                        "This training module qualifies you in practical execution of fire suppression and safe evacuation."
                );
                binding.btnLaunchArAction.setVisibility(View.GONE);
                break;

            case 2:
                binding.tvLearningStepIndicator.setText("Step 2 of 11: Learning Objectives");
                binding.tvStepTitle.setText("2. Certified Competency Objectives");
                binding.tvStepBody.setText(
                        "By the conclusion of this 11-step microlearning module, you will demonstrate in augmented reality:\n\n" +
                        "1. Immediate Hazard Zone Recognition:\n" +
                        "   Establish a mandatory 3-meter safety perimeter from an active thermal source.\n\n" +
                        "2. Alarm Siren Activation:\n" +
                        "   Locate and activate the red Manual Call Point (MCP) before suppression.\n\n" +
                        "3. PASS Protocol Mastery:\n" +
                        "   Deploy a Dry Chemical Powder (DCP) extinguisher using Pull, Aim, Squeeze, Sweep.\n\n" +
                        "4. Evacuation & Muster Navigation:\n" +
                        "   Traverse marked green evacuation floor pathways to reach the designated Safe Assembly Point."
                );
                binding.btnLaunchArAction.setVisibility(View.GONE);
                break;

            case 3:
                binding.tvLearningStepIndicator.setText("Step 3 of 11: Safety Theory");
                binding.tvStepTitle.setText("3. Fire Science & DGMS Regulations");
                binding.tvStepBody.setText(
                        "Understanding the Fire Tetrahedron is critical for industrial safety:\n\n" +
                        "• Fuel: Combustible coal dust, hydraulic oil, conveyor rubber belts.\n" +
                        "• Oxygen: Forced underground mine ventilation air currents.\n" +
                        "• Heat: Electrical friction, overheating conveyor rollers, cable short-circuits.\n" +
                        "• Chemical Chain Reaction: Sustained radical recombination.\n\n" +
                        "DGMS CLASSIFICATION RULES:\n\n" +
                        "• Class A: Ordinary combustibles (timber props, coal, paper).\n" +
                        "• Class B: Flammable liquids (lubricants, diesel, transformer oil).\n" +
                        "• Class C: Energized electrical equipment (switchgears, substations).\n\n" +
                        "CRITICAL WARNING:\n" +
                        "NEVER use water or foam on Class C electrical fires! Doing so will cause lethal electrocution. Always deploy Dry Chemical Powder (DCP) or Carbon Dioxide (CO2)."
                );
                binding.btnLaunchArAction.setVisibility(View.GONE);
                break;

            case 4:
                binding.tvLearningStepIndicator.setText("Step 4 of 11: Visual Explanation");
                binding.tvStepTitle.setText("4. The PASS Suppression Method");
                binding.tvStepBody.setText(
                        "Industrial fire extinguishers must be operated following the universal PASS method:\n\n" +
                        "1. [P] PULL THE PIN:\n" +
                        "   Break the tamper-evident plastic seal and pull the ring pin out of the handle.\n\n" +
                        "2. [A] AIM AT THE BASE:\n" +
                        "   Aim the discharge nozzle or hose horn directly at the base of the flames, NOT at the rising smoke or top flames.\n\n" +
                        "3. [S] SQUEEZE THE LEVER:\n" +
                        "   Firmly squeeze the operating lever to release the pressurized extinguishing agent.\n\n" +
                        "4. [S] SWEEP SIDE-TO-SIDE:\n" +
                        "   Sweep across the entire fuel footprint until the fire is completely extinguished and glowing embers are smothered."
                );
                binding.btnLaunchArAction.setVisibility(View.GONE);
                break;

            case 5:
                binding.tvLearningStepIndicator.setText("Step 5 of 11: AR Demonstration");
                binding.tvStepTitle.setText("5. Augmented Reality Safety Environment");
                binding.tvStepBody.setText(
                        "During the interactive AR simulation, your device camera overlays real-time 3D safety elements into your physical room:\n\n" +
                        "• 3D Fire Flames: Dynamic thermal particle simulation.\n" +
                        "• 3-Meter Perimeter Ring: Pulsing orange warning boundary demarcating the hazardous standoff zone.\n" +
                        "• Manual Call Point: Wall-mounted red emergency siren station.\n" +
                        "• Extinguisher Station: Choice between Dry Chemical Powder (DCP) and Water.\n" +
                        "• Evacuation Guide Arrows: Illuminated directional chevrons on the floor.\n" +
                        "• Emergency Exit & Muster Banner: Green LED exit sign and safe assembly flag."
                );
                binding.btnLaunchArAction.setVisibility(View.GONE);
                break;

            case 6:
                binding.tvLearningStepIndicator.setText("Step 6 of 11: Pre-AR Instructions");
                binding.tvStepTitle.setText("6. Environment Calibration & Physical Clearance");
                binding.tvStepBody.setText(
                        "Before activating your camera for the AR module, verify these safety readiness requirements:\n\n" +
                        "1. Physical Clearance:\n" +
                        "   Ensure a 2m x 2m clear floor space free of physical obstacles, tripping hazards, or steps.\n\n" +
                        "2. Adequate Lighting:\n" +
                        "   Ensure the room has sufficient lighting for camera feature tracking and plane detection.\n\n" +
                        "3. Device Posture:\n" +
                        "   Hold the phone firmly at waist height, pointing slightly downward (approx. 45 degrees) toward the floor.\n\n" +
                        "4. Audio & Haptics:\n" +
                        "   Ensure sound and vibration are enabled to receive tactile feedback during PIN pull and alarm siren."
                );
                binding.btnLaunchArAction.setVisibility(View.VISIBLE);
                binding.btnLaunchArAction.setText("Open Camera Calibration \u2192");
                break;

            case 7:
                binding.tvLearningStepIndicator.setText("Step 7 of 11: Interactive AR Scenario");
                binding.tvStepTitle.setText("7. Guided Interactive AR Simulation");
                binding.tvStepBody.setText(
                        "You will now enter the guided AR environment.\n\n" +
                        "Follow the HUD prompts on your display:\n" +
                        "1. Tap on the Fire to recognize and measure the hazard perimeter.\n" +
                        "2. Tap the Emergency Alarm box to trigger the loud factory siren.\n" +
                        "3. Select the correct DCP Extinguisher from the equipment rack.\n" +
                        "4. Execute PASS: Tap 'Pull Pin' \u2192 'Aim Base' \u2192 'Squeeze Lever' \u2192 'Sweep'.\n" +
                        "5. Follow the illuminated floor chevrons through the emergency exit.\n" +
                        "6. Confirm safety at the green Assembly Muster Point.\n\n" +
                        "Visual guides and hints are enabled in this mode."
                );
                binding.btnLaunchArAction.setVisibility(View.VISIBLE);
                binding.btnLaunchArAction.setText("Launch Interactive AR Simulation \u2192");
                break;

            case 8:
                binding.tvLearningStepIndicator.setText("Step 8 of 11: Practice & Assessment");
                binding.tvStepTitle.setText("8. Strict Sequential Competency Exam");
                binding.tvStepBody.setText(
                        "TEST INSTRUCTIONS:\n\n" +
                        "In this phase, you will be evaluated under strict DGMS inspection rules without assistance hints:\n\n" +
                        "• Maximum Score: 100 Points.\n" +
                        "• Passing Threshold: 80 Points.\n" +
                        "• Order Enforcement: Actions must be completed in exact safety sequence. Skipping the alarm or attempting suppression with the pin still inserted will incur point penalties.\n" +
                        "• Wrong Extinguisher Penalty: Selecting Water on an energized fire deducts 20 points.\n\n" +
                        "Tap the button below when you are ready to begin the scored practical assessment."
                );
                binding.btnLaunchArAction.setVisibility(View.VISIBLE);
                binding.btnLaunchArAction.setText("Start Scored AR Exam \u2192");
                break;

            case 9:
                binding.tvLearningStepIndicator.setText("Step 9 of 11: Score Breakdown");
                binding.tvStepTitle.setText("9. Performance & Scoring Matrix");
                binding.tvStepBody.setText(
                        "Your safety actions are evaluated against the National Safety Council and DGMS benchmark:\n\n" +
                        "• Hazard Identification (3m Standoff): +10 Points\n" +
                        "• Early Alarm Activation: +15 Points\n" +
                        "• Correct Extinguisher Selection (DCP): +15 Points\n" +
                        "• PASS Step 1 (Pull Safety Pin): +10 Points\n" +
                        "• PASS Step 2 (Aim at Fire Base): +10 Points\n" +
                        "• PASS Step 3 (Squeeze Lever): +10 Points\n" +
                        "• PASS Step 4 (Sweep Motion): +10 Points\n" +
                        "• Route Evacuation Compliance: +10 Points\n" +
                        "• Assembly Muster Check-in: +10 Points\n\n" +
                        "Total Possible: 100 Points. Passing Standard: 80 Points."
                );
                binding.btnLaunchArAction.setVisibility(View.GONE);
                break;

            case 10:
                binding.tvLearningStepIndicator.setText("Step 10 of 11: Pass/Fail Evaluation");
                binding.tvStepTitle.setText("10. Competency Decision: PASS");
                binding.tvStepBody.setText(
                        "EVALUATION CRITERIA:\n\n" +
                        "Scores of 80% and above achieve CERTIFIED COMPLIANCE status.\n\n" +
                        "• Score >= 80%: PASSED\n" +
                        "  The worker is deemed competent to work safely in hazardous coal seam and plant environments.\n\n" +
                        "• Score < 80%: FAILED\n" +
                        "  Mandatory retraining required. The worker must re-take the microlearning modules and AR practical assessment.\n\n" +
                        "Your record is saved locally in the encrypted SQLite Room Database and queued for immediate DGMS portal synchronization."
                );
                binding.btnLaunchArAction.setVisibility(View.GONE);
                break;

            case 11:
                binding.tvLearningStepIndicator.setText("Step 11 of 11: Certificate Eligibility");
                binding.tvStepTitle.setText("11. Digital Safety Pass Issued");
                binding.tvStepBody.setText(
                        "CONGRATULATIONS!\n\n" +
                        "You have successfully satisfied all requirements of Module 1: Fire & Explosion Response.\n\n" +
                        "Your credential has been approved by the Jharkhand Industrial Safety Council (SIH 2026):\n\n" +
                        "• Official Certificate Code: CERT-DGMS-FIRE-2026\n" +
                        "• Cryptographic QR Pass: Generated and validated for 365 days\n" +
                        "• Orientation Credits: Added to your 30-Day New Worker Orientation roadmap\n" +
                        "• Verification: Readable offline by mine supervisors and safety auditors\n\n" +
                        "Tap below to view your Digital Safety Pass."
                );
                binding.btnLaunchArAction.setVisibility(View.VISIBLE);
                binding.btnLaunchArAction.setText("View Digital Safety Pass \u2192");
                break;
        }
    }

    private void updateProgress(int step) {
        int percent = (int) (((float) step / TOTAL_STEPS) * 100);
        String status = step == TOTAL_STEPS ? "COMPLETED" : "IN_PROGRESS";

        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            TrainingProgressEntity progress = db.trainingProgressDao().getModuleProgressSync(workerId, moduleId);
            if (progress == null) {
                progress = new TrainingProgressEntity(
                        UUID.randomUUID().toString(),
                        workerId,
                        moduleId,
                        status,
                        percent,
                        System.currentTimeMillis()
                );
                db.trainingProgressDao().insertProgress(progress);
            } else {
                progress.setStatus(status);
                progress.setPercentComplete(Math.max(progress.getPercentComplete(), percent));
                progress.setLastAccessedAt(System.currentTimeMillis());
                db.trainingProgressDao().updateProgress(progress);
            }
        });
    }
}
