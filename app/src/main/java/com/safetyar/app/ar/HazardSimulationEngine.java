package com.safetyar.app.ar;

import com.safetyar.app.domain.model.HazardItem;
import com.safetyar.app.domain.model.IndustrySector;

import java.util.ArrayList;
import java.util.List;

public class HazardSimulationEngine {

    public static List<HazardItem> generateHazardsForSector(IndustrySector sector) {
        List<HazardItem> hazards = new ArrayList<>();

        if (sector == IndustrySector.COAL_MINING) {
            hazards.add(new HazardItem(
                    "HZ-COAL-01",
                    "Unsupported Roof Strata (Sagging Rock)",
                    "Severe fracture line visible in immediate roof stratum. High risk of fatal roof fall under DGMS Circular 3 of 2019.",
                    "Set steel prop / roof bolt immediately. Withdraw all personnel until support is erected.",
                    sector,
                    HazardItem.Severity.CRITICAL,
                    0.0f, 0.6f, -1.8f
            ));
            hazards.add(new HazardItem(
                    "HZ-COAL-02",
                    "Methane Gas Pocket (>1.25% CH4)",
                    "Methane accumulation detected near roof cavity. Highly explosive atmosphere per DGMS Rule 115.",
                    "Isolate electrical power, activate auxiliary ventilation duct, and notify mine overman.",
                    sector,
                    HazardItem.Severity.CRITICAL,
                    -0.8f, 0.4f, -2.2f
            ));
            hazards.add(new HazardItem(
                    "HZ-COAL-03",
                    "Exposed Conveyor Belt Nip Point",
                    "Rotating return drum guard missing on armored conveyor. Extreme entanglement danger.",
                    "Apply Lockout/Tagout (LOTO) padlock on isolation switch before replacing mesh guard.",
                    sector,
                    HazardItem.Severity.HIGH,
                    0.7f, -0.4f, -1.5f
            ));
        } else if (sector == IndustrySector.STEEL_MANUFACTURING) {
            hazards.add(new HazardItem(
                    "HZ-STEEL-01",
                    "Molten Slag Runner Splatter Zone",
                    "Active blast furnace slag runner at 1450°C. Standoff safety boundary violated (IS-14489:2018).",
                    "Evacuate to 3-meter thermal zone. Don aluminized heat-reflective suit and face shield.",
                    sector,
                    HazardItem.Severity.CRITICAL,
                    -0.4f, -0.3f, -2.0f
            ));
            hazards.add(new HazardItem(
                    "HZ-STEEL-02",
                    "Suspended Ladle Overhead Crane Corridor",
                    "Overhead crane transporting 120-ton liquid steel ladle without audible siren clearance.",
                    "Clear travel bay immediately; never walk beneath suspended hot metal charges.",
                    sector,
                    HazardItem.Severity.HIGH,
                    0.0f, 0.8f, -2.5f
            ));
            hazards.add(new HazardItem(
                    "HZ-STEEL-03",
                    "Damaged High-Current Furnace Cable",
                    "Insulation abrasion on 415V secondary transformer feeder near hydraulic lines.",
                    "Press emergency stop circuit breaker and report to electrical safety engineer.",
                    sector,
                    HazardItem.Severity.HIGH,
                    0.6f, -0.2f, -1.6f
            ));
        } else { // MICA_PROCESSING
            hazards.add(new HazardItem(
                    "HZ-MICA-01",
                    "Respirable Mica/Silica Dust Concentration",
                    "Dry sorting dust density exceeds 3 mg/m³. Severe long-term Silicosis risk (DGMS/ILO standards).",
                    "Engage wet mist dust suppression nozzles. Wear certified FFP3/N95 respirator.",
                    sector,
                    HazardItem.Severity.CRITICAL,
                    0.2f, 0.1f, -1.7f
            ));
            hazards.add(new HazardItem(
                    "HZ-MICA-02",
                    "Quarry Pit Bench Overhang",
                    "Tension cracks identified on opencast bench crest. Imminent rockslide hazard.",
                    "Barricade danger perimeter with red flags; bench height must not exceed safe digging depth.",
                    sector,
                    HazardItem.Severity.HIGH,
                    -0.7f, 0.5f, -2.3f
            ));
            hazards.add(new HazardItem(
                    "HZ-MICA-03",
                    "Inadequate Footwear in Cobbing Yard",
                    "Worker operating with non-safety footwear amongst sharp pegmatite & quartz fragments.",
                    "Equip puncture-resistant steel-toe ankle-support boots before entering yard.",
                    sector,
                    HazardItem.Severity.MODERATE,
                    0.5f, -0.5f, -1.4f
            ));
        }

        return hazards;
    }
}
