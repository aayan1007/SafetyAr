package com.safetyar.app.data.repository;

import com.safetyar.app.R;
import com.safetyar.app.domain.assessment.AssessmentQuestion;
import com.safetyar.app.domain.assessment.QuestionDifficulty;
import com.safetyar.app.domain.assessment.QuestionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssessmentQuestionBank {

    public static List<AssessmentQuestion> getQuestionsForModule(String moduleId) {
        if (moduleId == null) {
            moduleId = "MOD-FIRE-01";
        }

        switch (moduleId) {
            case "MOD-FIRE-01":
                return getFireExplosionQuestions();
            case "MOD-COAL-01":
                return getCoalMiningQuestions();
            case "MOD-STEEL-01":
                return getSteelQuestions();
            case "MOD-MICA-01":
                return getMicaQuestions();
            default:
                return getFireExplosionQuestions();
        }
    }

    private static List<AssessmentQuestion> getFireExplosionQuestions() {
        List<AssessmentQuestion> list = new ArrayList<>();

        // 1. SCENARIO: Electric Short Circuit at Underground Conveyor Drive Head
        list.add(new AssessmentQuestion(
                "Q-FIRE-01",
                "MOD-FIRE-01",
                "SCENARIO: An energized 415V underground conveyor drive motor sparks violently and catches fire with dense black smoke. What is your FIRST immediate action?",
                QuestionType.SCENARIO,
                QuestionDifficulty.INTERMEDIATE,
                Arrays.asList(
                        "Grab a water hose from the haulage line and spray the motor directly",
                        "Locate and activate the Manual Pull Alarm Call Point to sound the mine evacuation siren",
                        "Attempt to dismantle the motor casing using a steel spanner",
                        "Wait for 15 minutes to see if the thermal relay cuts off"
                ),
                "Locate and activate the Manual Pull Alarm Call Point to sound the mine evacuation siren",
                "DGMS CMR Rule 115 mandates alerting all workers and sounding the emergency siren before attempting any local containment.",
                15,
                true, // SAFETY CRITICAL!
                "Emergency Alarm & Evacuation"
        ));

        // 2. IMAGE-BASED: Extinguisher Agent Suitability
        list.add(new AssessmentQuestion(
                "Q-FIRE-02",
                "MOD-FIRE-01",
                "IMAGE HAZARD: Looking at the electrical cable fire hazard illustrated above, which fire extinguisher media is STRICTLY PROHIBITED due to fatal electrocution risk?",
                QuestionType.IMAGE_BASED,
                QuestionDifficulty.BASIC,
                Arrays.asList(
                        "Dry Chemical Powder (DCP)",
                        "Carbon Dioxide (CO2)",
                        "Water / Aqueous Foam",
                        "Clean Agent Gas"
                ),
                "Water / Aqueous Foam",
                "Water is an electrical conductor. Spraying water on energized Class C equipment conducts lethal high-voltage current directly into the operator.",
                20,
                true, // SAFETY CRITICAL!
                "Fire Extinguisher Selection",
                R.drawable.ic_hazard_warning
        ));

        // 3. SEQUENCE_ORDER: Universal PASS Suppression Technique
        list.add(new AssessmentQuestion(
                "Q-FIRE-03",
                "MOD-FIRE-01",
                "Arrange the 4 steps of the universal PASS fire extinguisher protocol in their exact sequential operational order:",
                QuestionType.SEQUENCE_ORDER,
                QuestionDifficulty.INTERMEDIATE,
                Arrays.asList(
                        "PULL the safety ring pin to break the tamper seal",
                        "AIM the discharge horn or nozzle at the BASE of the flames",
                        "SQUEEZE the operating lever to release the powder",
                        "SWEEP the nozzle smoothly from side-to-side across the fuel bed"
                ),
                "0,1,2,3", // Exact sequence indexes: 0 -> 1 -> 2 -> 3
                "The PASS sequence must be executed as Pull -> Aim -> Squeeze -> Sweep. Squeezing before pulling or aiming at upper flames fails to extinguish the base.",
                25,
                false,
                "PASS Protocol"
        ));

        // 4. MULTIPLE_CHOICE: Fire Hazard Standoff Clearance
        list.add(new AssessmentQuestion(
                "Q-FIRE-04",
                "MOD-FIRE-01",
                "According to DGMS standard guidelines, what is the mandatory minimum standoff safety perimeter from an active industrial flame front before deploying DCP?",
                QuestionType.MULTIPLE_CHOICE,
                QuestionDifficulty.BASIC,
                Arrays.asList(
                        "0.5 Meters",
                        "1.0 Meter",
                        "3.0 Meters",
                        "10.0 Meters"
                ),
                "3.0 Meters",
                "A 3-meter safety radius shields the worker from radiant flash burns while keeping them within the effective stream velocity of a DCP extinguisher.",
                15,
                false,
                "Fire Suppression Sequence"
        ));

        // 5. AR_ACTION: Evacuation Navigation to Muster Point
        list.add(new AssessmentQuestion(
                "Q-FIRE-05",
                "MOD-FIRE-01",
                "AR ACTION: Once fire suppression is attempted or smoke limits visibility, what action must the worker perform regarding floor route markings?",
                QuestionType.AR_ACTION,
                QuestionDifficulty.INTERMEDIATE,
                Arrays.asList(
                        "Follow the green illuminated chevron floor arrows toward the designated Assembly Muster Point",
                        "Crawl into an unventilated blind gallery to escape smoke",
                        "Return to the working face to retrieve personal belongings",
                        "Stand stationary and wait for the surface shift boss"
                ),
                "Follow the green illuminated chevron floor arrows toward the designated Assembly Muster Point",
                "Miners must immediately navigate along marked escape routes toward the authenticated muster point for headcount verification.",
                25,
                true, // SAFETY CRITICAL!
                "Emergency Alarm & Evacuation"
        ));

        return list;
    }

    private static List<AssessmentQuestion> getCoalMiningQuestions() {
        List<AssessmentQuestion> list = new ArrayList<>();

        // 1. SCENARIO: Methane Gas Threshold
        list.add(new AssessmentQuestion(
                "Q-COAL-01",
                "MOD-COAL-01",
                "SCENARIO: During mechanical coal cutting, your digital methanometer reads 1.30% CH4 at the return face. What does DGMS CMR-2017 Rule 115 require?",
                QuestionType.SCENARIO,
                QuestionDifficulty.ADVANCED,
                Arrays.asList(
                        "Continue cutting but open auxiliary ventilation brattice",
                        "Immediately cut off electric power to the continuous miner and withdraw all workers to fresh air",
                        "Wait 30 minutes to check if methane naturally disperses",
                        "Light a safety match to test if the gas burns"
                ),
                "Immediately cut off electric power to the continuous miner and withdraw all workers to fresh air",
                "At 1.25% or above, electric power must be immediately isolated and the working place evacuated under DGMS statutory mandate.",
                25,
                true, // SAFETY CRITICAL!
                "Methane & Gas Monitoring"
        ));

        // 2. IMAGE_BASED: Roof Strata Sound Tapping
        list.add(new AssessmentQuestion(
                "Q-COAL-02",
                "MOD-COAL-01",
                "IMAGE HAZARD: While testing the underground shale roof with a testing bar, you hear a hollow, drum-like resonance ('drummy sound'). What does this indicate?",
                QuestionType.IMAGE_BASED,
                QuestionDifficulty.INTERMEDIATE,
                Arrays.asList(
                        "Solid, competent sandstone roof safe to drill",
                        "Delaminated roof strata with imminent fall danger; props or roof bolts must be set immediately",
                        "High moisture content with zero safety hazard",
                        "Air pocket caused by coal seam degassing"
                ),
                "Delaminated roof strata with imminent fall danger; props or roof bolts must be set immediately",
                "A drummy sound indicates separation of the roof layers. Entering an unsupported span under delaminated roof is a primary cause of fatalities.",
                25,
                true, // SAFETY CRITICAL!
                "Roof Strata & Support",
                R.drawable.ic_hazard_warning
        ));

        // 3. SEQUENCE_ORDER: Roof Support Sequence
        list.add(new AssessmentQuestion(
                "Q-COAL-03",
                "MOD-COAL-01",
                "Order the mandatory steps for setting temporary hydraulic prop supports at an advancing mine gallery:",
                QuestionType.SEQUENCE_ORDER,
                QuestionDifficulty.INTERMEDIATE,
                Arrays.asList(
                        "Sound-tap roof from under supported zone to test stability",
                        "Position base plate on clean, solid floor bedrock",
                        "Extend hydraulic ram to firmly contact roof plate",
                        "Drive wedge and lock release valve"
                ),
                "0,1,2,3",
                "Miners must test the roof from safe ground, set the solid footing, extend the prop, and lock the hydraulic wedge.",
                25,
                false,
                "Roof Strata & Support"
        ));

        // 4. MULTIPLE_CHOICE: Conveyor Pull-Cord Safety
        list.add(new AssessmentQuestion(
                "Q-COAL-04",
                "MOD-COAL-01",
                "What is the statutory maximum spacing along an underground trunk belt conveyor where an emergency pull-cord trip wire must be accessible?",
                QuestionType.MULTIPLE_CHOICE,
                QuestionDifficulty.BASIC,
                Arrays.asList(
                        "Continuously along the entire conveyor length without interruption",
                        "Only at the head and tail drive pulleys",
                        "Every 500 meters",
                        "Only at transfer chutes"
                ),
                "Continuously along the entire conveyor length without interruption",
                "DGMS requires emergency stop pull-cords to run continuously along the entire belt length for instantaneous stoppage in case of entanglement.",
                25,
                false,
                "Conveyor & Machinery Safety"
        ));

        return list;
    }

    private static List<AssessmentQuestion> getSteelQuestions() {
        List<AssessmentQuestion> list = new ArrayList<>();

        list.add(new AssessmentQuestion(
                "Q-STEEL-01",
                "MOD-STEEL-01",
                "SCENARIO: Liquid metal is flowing at 1500°C from the blast furnace taphole into the runner. Water enters the molten iron runner trough. What is the immediate catastrophic hazard?",
                QuestionType.SCENARIO,
                QuestionDifficulty.ADVANCED,
                Arrays.asList(
                        "Instantaneous steam explosion that detonates molten iron up to 50 meters",
                        "Normal cooling and solidification of the slag",
                        "Release of harmless steam with no thermal danger",
                        "Extinguishment of the blast furnace fire"
                ),
                "Instantaneous steam explosion that detonates molten iron up to 50 meters",
                "Water trapped beneath 1500°C molten metal instantly vaporizes expanding 1700 times, causing a lethal steam explosion.",
                25,
                true, // SAFETY CRITICAL!
                "Blast Furnace & Molten Metal"
        ));

        list.add(new AssessmentQuestion(
                "Q-STEEL-02",
                "MOD-STEEL-01",
                "What mandatory Personal Protective Equipment (PPE) is required before crossing the cast house floor near an open molten metal runner?",
                QuestionType.MULTIPLE_CHOICE,
                QuestionDifficulty.BASIC,
                Arrays.asList(
                        "Standard cotton shirt and rubber slippers",
                        "Aluminized thermal proximity coat, gold-coated heat visor, and molten-splash spats",
                        "Paper dust mask and safety goggles only",
                        "No special PPE is required if staying 1 meter away"
                ),
                "Aluminized thermal proximity coat, gold-coated heat visor, and molten-splash spats",
                "Cast house workers must be shielded by aluminized heat-reflective gear to prevent 3rd degree burns from radiant energy and flying sparks.",
                25,
                true, // SAFETY CRITICAL!
                "Blast Furnace & Molten Metal"
        ));

        list.add(new AssessmentQuestion(
                "Q-STEEL-03",
                "MOD-STEEL-01",
                "Order the pre-lift crane safety checklist before hoisting a 60-tonne ladle of liquid steel:",
                QuestionType.SEQUENCE_ORDER,
                QuestionDifficulty.INTERMEDIATE,
                Arrays.asList(
                        "Sound warning siren to clear all personnel below crane path",
                        "Inspect wire rope slings and trunnion hooks for thermal fatigue cracks",
                        "Test dual main hoist brakes with trial 100mm test lift",
                        "Execute smooth horizontal transfer at controlled speed"
                ),
                "0,1,2,3",
                "Pre-lift safety protocol: warning siren -> hook inspection -> test brake lift -> controlled transfer.",
                25,
                false,
                "Overhead Crane Safety"
        ));

        list.add(new AssessmentQuestion(
                "Q-STEEL-04",
                "MOD-STEEL-01",
                "IMAGE HAZARD: What toxic, odorless, colorless gas is emitted in massive volumes around blast furnace gas ducts and bleeder valves?",
                QuestionType.IMAGE_BASED,
                QuestionDifficulty.BASIC,
                Arrays.asList(
                        "Carbon Monoxide (CO)",
                        "Oxygen (O2)",
                        "Nitrogen Dioxide (NO2)",
                        "Helium (He)"
                ),
                "Carbon Monoxide (CO)",
                "Blast furnace gas contains 25-30% Carbon Monoxide. It binds to hemoglobin causing rapid unconsciousness and death without portable CO detectors.",
                25,
                false,
                "Blast Furnace Gas Safety",
                R.drawable.ic_hazard_warning
        ));

        return list;
    }

    private static List<AssessmentQuestion> getMicaQuestions() {
        List<AssessmentQuestion> list = new ArrayList<>();

        list.add(new AssessmentQuestion(
                "Q-MICA-01",
                "MOD-MICA-01",
                "SCENARIO: You are assigned to the dry mica crushing plant in Koderma. The water spray dust suppression mist nozzles are clogged. What is your required action?",
                QuestionType.SCENARIO,
                QuestionDifficulty.INTERMEDIATE,
                Arrays.asList(
                        "Operate the crushers dry to meet production quotas",
                        "Halt the crusher immediately and clean misting nozzles before resuming operations",
                        "Sprinkle dry sand over the mica chunks",
                        "Remove your dust respirator to breathe more comfortably"
                ),
                "Halt the crusher immediately and clean misting nozzles before resuming operations",
                "DGMS occupational health regulations prohibit dry processing of silica/mica minerals without functioning wet dust suppression.",
                25,
                true, // SAFETY CRITICAL!
                "Respirable Dust & Silicosis"
        ));

        list.add(new AssessmentQuestion(
                "Q-MICA-02",
                "MOD-MICA-01",
                "What chronic, irreversible lung disease is caused by prolonged inhalation of airborne respirable crystalline silica and mica dust?",
                QuestionType.MULTIPLE_CHOICE,
                QuestionDifficulty.BASIC,
                Arrays.asList(
                        "Silicosis",
                        "Asthma only",
                        "Temporary cough that heals in 2 days",
                        "Common cold"
                ),
                "Silicosis",
                "Inhaling respirable silica crystals causes progressive pulmonary fibrosis and permanent disability (Silicosis).",
                25,
                true, // SAFETY CRITICAL!
                "Respirable Dust & Silicosis"
        ));

        list.add(new AssessmentQuestion(
                "Q-MICA-03",
                "MOD-MICA-01",
                "Arrange the daily respirator pre-shift inspection steps in correct order:",
                QuestionType.SEQUENCE_ORDER,
                QuestionDifficulty.BASIC,
                Arrays.asList(
                        "Inspect silicone facepiece for tears, cracks, or stiffness",
                        "Check that dual P100 particulate filter cartridges are locked tightly",
                        "Perform positive pressure seal check by exhaling gently with valve covered",
                        "Perform negative pressure seal check by inhaling gently with filters covered"
                ),
                "0,1,2,3",
                "Inspection sequence: facepiece check -> cartridge lock -> positive seal -> negative seal.",
                25,
                false,
                "PPE Compliance"
        ));

        list.add(new AssessmentQuestion(
                "Q-MICA-04",
                "MOD-MICA-01",
                "IMAGE HAZARD: At an open-cast mica quarry bench, what is the statutory maximum slope angle permitted to prevent sudden bench collapse?",
                QuestionType.IMAGE_BASED,
                QuestionDifficulty.INTERMEDIATE,
                Arrays.asList(
                        "45 degrees from horizontal",
                        "90 degrees (completely vertical)",
                        "Overhanging inverted slope",
                        "Any angle as long as no rain falls"
                ),
                "45 degrees from horizontal",
                "DGMS Metalliferous Mines Regulations specify bench slopes in soft weathered pegmatite must not exceed 45 degrees.",
                25,
                false,
                "Quarry Bench Stability",
                R.drawable.ic_hazard_warning
        ));

        return list;
    }
}
