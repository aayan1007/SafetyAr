package com.safetyar.app.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.safetyar.app.data.local.dao.ARScenarioDao;
import com.safetyar.app.data.local.dao.AssessmentAttemptDao;
import com.safetyar.app.data.local.dao.AssessmentDao;
import com.safetyar.app.data.local.dao.CertificateDao;
import com.safetyar.app.data.local.dao.OrganizationDao;
import com.safetyar.app.data.local.dao.OrientationProgressDao;
import com.safetyar.app.data.local.dao.QuestionDao;
import com.safetyar.app.data.local.dao.SyncQueueDao;
import com.safetyar.app.data.local.dao.TrainingAssignmentDao;
import com.safetyar.app.data.local.dao.TrainingLessonDao;
import com.safetyar.app.data.local.dao.TrainingModuleDao;
import com.safetyar.app.data.local.dao.TrainingProgressDao;
import com.safetyar.app.data.local.dao.WorkerDao;
import com.safetyar.app.data.local.entity.ARScenarioEntity;
import com.safetyar.app.data.local.entity.AnswerEntity;
import com.safetyar.app.data.local.entity.AssessmentAttemptEntity;
import com.safetyar.app.data.local.entity.AssessmentRecordEntity;
import com.safetyar.app.data.local.entity.CertificateEntity;
import com.safetyar.app.data.local.entity.OrganizationEntity;
import com.safetyar.app.data.local.entity.OrientationProgressEntity;
import com.safetyar.app.data.local.entity.QuestionEntity;
import com.safetyar.app.data.local.entity.QuestionOptionEntity;
import com.safetyar.app.data.local.entity.SyncQueueEntity;
import com.safetyar.app.data.local.entity.TrainingAssignmentEntity;
import com.safetyar.app.data.local.entity.TrainingLessonEntity;
import com.safetyar.app.data.local.entity.TrainingModuleEntity;
import com.safetyar.app.data.local.entity.TrainingProgressEntity;
import com.safetyar.app.data.local.entity.WorkerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {
        WorkerEntity.class,
        OrganizationEntity.class,
        TrainingModuleEntity.class,
        TrainingLessonEntity.class,
        ARScenarioEntity.class,
        QuestionEntity.class,
        QuestionOptionEntity.class,
        AssessmentAttemptEntity.class,
        AssessmentRecordEntity.class,
        AnswerEntity.class,
        TrainingProgressEntity.class,
        CertificateEntity.class,
        SyncQueueEntity.class,
        TrainingAssignmentEntity.class,
        OrientationProgressEntity.class
    },
    version = 5,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract WorkerDao workerDao();
    public abstract OrganizationDao organizationDao();
    public abstract TrainingModuleDao trainingModuleDao();
    public abstract TrainingLessonDao trainingLessonDao();
    public abstract ARScenarioDao arScenarioDao();
    public abstract QuestionDao questionDao();
    public abstract AssessmentAttemptDao assessmentAttemptDao();
    public abstract AssessmentDao assessmentDao();
    public abstract TrainingProgressDao trainingProgressDao();
    public abstract CertificateDao certificateDao();
    public abstract SyncQueueDao syncQueueDao();
    public abstract TrainingAssignmentDao trainingAssignmentDao();
    public abstract OrientationProgressDao orientationProgressDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "safetyar_industrial.db"
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(sRoomDatabaseCallback)
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // 1. Seed Organizations
                OrganizationDao orgDao = INSTANCE.organizationDao();
                List<OrganizationEntity> orgs = new ArrayList<>();
                orgs.add(new OrganizationEntity("ORG-BCCL", "Bharat Coking Coal Limited (BCCL)", "COAL_MINING", "Koyla Bhawan, Dhanbad", "0326-2230190", "DGMS Eastern Zone, Dhanbad"));
                orgs.add(new OrganizationEntity("ORG-TATA", "Tata Steel Limited", "STEEL_MANUFACTURING", "Jamshedpur Works, East Singhbhum", "0657-2431000", "Jharkhand Factory Directorate, Jamshedpur"));
                orgs.add(new OrganizationEntity("ORG-JSMDC", "Jharkhand State Mineral Dev Corp", "MICA_PROCESSING", "Khanij Nigam Bhawan, Ranchi / Koderma", "0651-2490767", "DGMS North Zone, Koderma"));
                orgDao.insertOrganizations(orgs);

                // 2. Seed Workers
                WorkerDao workerDao = INSTANCE.workerDao();
                List<WorkerEntity> demoWorkers = new ArrayList<>();
                long now = System.currentTimeMillis();

                long day12Start = now - (11L * 24 * 60 * 60 * 1000L);
                // Primary SIH 2026 Demo Worker: Ramesh Soren
                demoWorkers.add(new WorkerEntity(
                        "WRK-JH-COAL-0891",
                        "ORG-BCCL",
                        "Ramesh Soren",
                        "JH-MINER-89412",
                        "Underground Drill & Strata Technician",
                        "COAL_MINING",
                        "Bharat Coking Coal Limited (BCCL), Dhanbad",
                        "Moonidih Underground Project, Seam XVI",
                        "02 Sep 2026",
                        day12Start,
                        "MOD-FIRE-01",
                        1,
                        true,
                        now - (2L * 24 * 60 * 60 * 1000L),
                        true
                ));

                long day5Start = now - (4L * 24 * 60 * 60 * 1000L);
                demoWorkers.add(new WorkerEntity(
                        "WRK-JH-STEEL-1045",
                        "ORG-TATA",
                        "Arjun Mahto",
                        "JH-STEEL-10450",
                        "Blast Furnace Taphole & Slag Operator",
                        "STEEL_MANUFACTURING",
                        "Tata Steel Limited, Jamshedpur",
                        "Iron Making Division, Blast Furnace I",
                        "09 Sep 2026",
                        day5Start,
                        "MOD-STEEL-01",
                        0,
                        false,
                        0L,
                        false
                ));

                long day24Start = now - (23L * 24 * 60 * 60 * 1000L);
                demoWorkers.add(new WorkerEntity(
                        "WRK-JH-MICA-0312",
                        "ORG-JSMDC",
                        "Sunita Murmu",
                        "JH-MICA-03125",
                        "Mica Flake Quality & Particulate Specialist",
                        "MICA_PROCESSING",
                        "Jharkhand State Mineral Development (JSMDC)",
                        "Koderma Industrial Mica Flake Unit",
                        "21 Aug 2026",
                        day24Start,
                        "MOD-MICA-01",
                        2,
                        true,
                        now - (2L * 24 * 60 * 60 * 1000L),
                        false
                ));
                workerDao.insertWorkers(demoWorkers);

                // 3. Seed Orientation Progress
                OrientationProgressDao orientDao = INSTANCE.orientationProgressDao();
                orientDao.insertOrientation(new OrientationProgressEntity("ORIENT-COAL-01", "WRK-JH-COAL-0891", day12Start, 12, 11, 40, false, now));
                orientDao.insertOrientation(new OrientationProgressEntity("ORIENT-STEEL-01", "WRK-JH-STEEL-1045", day5Start, 5, 4, 16, false, now));
                orientDao.insertOrientation(new OrientationProgressEntity("ORIENT-MICA-01", "WRK-JH-MICA-0312", day24Start, 24, 23, 80, false, now));

                // 4. Seed Training Modules
                TrainingModuleDao moduleDao = INSTANCE.trainingModuleDao();
                List<TrainingModuleEntity> initialModules = new ArrayList<>();
                initialModules.add(new TrainingModuleEntity(
                        "MOD-COAL-01",
                        "COAL_MINING",
                        "Roof Strata Stability & Methane Detection",
                        "Underground safety SOPs under DGMS Circular 3 of 2019",
                        "DGMS-COAL-R115",
                        "1. Carry methanometer at all working faces.\n2. Tap roof stratum with testing bar to detect hollow drummy sound.\n3. Keep 1.5m minimum clearance from face before support props are wedged.\n4. Never enter unsupported roof spans under any circumstance.",
                        3,
                        80,
                        15,
                        3,
                        true
                ));
                initialModules.add(new TrainingModuleEntity(
                        "MOD-COAL-02",
                        "COAL_MINING",
                        "Armored Face Conveyor & Haulage Line Safety",
                        "Conveyor pull-wire trip switches and pinch-point isolation",
                        "DGMS-MECH-S42",
                        "1. Verify emergency pull-wire tension along entire haulage line.\n2. Lockout/Tagout (LOTO) key must remain with technician during maintenance.\n3. Inspect belt scraper alignment without touching moving components.",
                        3,
                        80,
                        12,
                        2,
                        false
                ));
                initialModules.add(new TrainingModuleEntity(
                        "MOD-STEEL-01",
                        "STEEL_MANUFACTURING",
                        "Blast Furnace Tapping & Molten Metal Splash",
                        "High radiant heat PPE and slag runner standoff regulations",
                        "IS-14489:2018",
                        "1. Don aluminized heat-reflective coat, face shield, and molten-metal spat gaiters.\n2. Maintain 3-meter minimum safety perimeter from open taphole runners.\n3. Check moisture-free condition of slag ladles before molten pouring.",
                        3,
                        80,
                        18,
                        3,
                        false
                ));
                initialModules.add(new TrainingModuleEntity(
                        "MOD-MICA-01",
                        "MICA_PROCESSING",
                        "Respirable Mica Dust & Silicosis Prevention",
                        "Wet suppression and particulate filtration protocols (DGMS/ILO)",
                        "DGMS-OCC-H98",
                        "1. Fit and seal FFP3/N95 dust respirator before entering flake crushing bays.\n2. Ensure wet misting nozzles operate at 2.5 bar water pressure.\n3. Conduct daily HEPA filter vacuum clearing; avoid dry sweeping.",
                        3,
                        80,
                        14,
                        3,
                        false
                ));
                initialModules.add(new TrainingModuleEntity(
                        "MOD-FIRE-01",
                        "COAL_MINING",
                        "Fire & Explosion Emergency Response",
                        "Underground mine fire isolation & PASS extinguisher protocol",
                        "DGMS-COAL-R118-FIRE",
                        "1. Recognize fire and do not panic.\n2. Sound emergency alarm immediately.\n3. Select Dry Chemical Powder (DCP) for methane/electrical fire.\n4. Apply PASS protocol: Pull, Aim, Squeeze, Sweep.\n5. If fire exceeds incipient stage, evacuate along illuminated green floor arrows.\n6. Assemble at surface muster point for headcount.",
                        3,
                        80,
                        12,
                        4,
                        false
                ));
                initialModules.add(new TrainingModuleEntity(
                        "MOD-GAS-01",
                        "COAL_MINING",
                        "Gas Leak & Confined Space Safety",
                        "Atmospheric 4-gas testing, SCBA donning, standby buddy verification, and Form-IV permits",
                        "DGMS-CS-S54:2024",
                        "1. Identify hazardous gas accumulation with multi-gas detector.\n2. Do not enter confined space without Form-IV Entry Permit.\n3. Don full SCBA breathing apparatus and harness.\n4. Maintain communication with standby safety buddy.\n5. Evacuate immediately if combustible gas exceeds 1.25% or O2 drops below 19%.",
                        3,
                        80,
                        18,
                        4,
                        false
                ));
                moduleDao.insertModules(initialModules);

                // 5. Seed Training Lessons
                TrainingLessonDao lessonDao = INSTANCE.trainingLessonDao();
                List<TrainingLessonEntity> lessons = new ArrayList<>();
                lessons.add(new TrainingLessonEntity("LES-COAL-01", "MOD-COAL-01", 1, "Methanometer Operation & Thresholds", "Flammable gas detection must be performed at roof cavities. If methane exceeds 1.25%, cut power and withdraw.", "Methane threshold: 1.25%", 5, true));
                lessons.add(new TrainingLessonEntity("LES-COAL-02", "MOD-COAL-01", 2, "Sounding the Roof with Testing Bar", "Sounding the roof with a 1.8m steel testing bar identifies hollow strata before fractures detach.", "Always stand under supported roof when testing", 5, true));
                lessons.add(new TrainingLessonEntity("LES-COAL-03", "MOD-COAL-01", 3, "Hydraulic Prop Wedging & Bolting", "Set hydraulic props strictly vertical with wooden lid cushions. Never delay support installation.", "Set props within 1.2m of face", 5, true));
                lessonDao.insertLessons(lessons);

                // 6. Seed AR Scenarios
                ARScenarioDao scenarioDao = INSTANCE.arScenarioDao();
                List<ARScenarioEntity> scenarios = new ArrayList<>();
                scenarios.add(new ARScenarioEntity("SCN-COAL-01", "MOD-COAL-01", "COAL_MINING", "Underground Working Face Strata Hazard Audit", "UNDERGROUND_COAL_SEAM", 3, 80, 60, "[]"));
                scenarios.add(new ARScenarioEntity("SCN-STEEL-01", "MOD-STEEL-01", "STEEL_MANUFACTURING", "Blast Furnace Taphole Radiant Heat Perimeter", "BLAST_FURNACE_FLOOR", 3, 80, 60, "[]"));
                scenarios.add(new ARScenarioEntity("SCN-MICA-01", "MOD-MICA-01", "MICA_PROCESSING", "Respirable Dust Cloud & Quarry Slope Check", "MICA_SORTING_SHED", 3, 80, 60, "[]"));
                scenarios.add(new ARScenarioEntity("SCN-FIRE-01", "MOD-FIRE-01", "COAL_MINING", "Underground Coal Fire & PASS Extinguisher Simulation", "UNDERGROUND_COAL_MINE", 3, 80, 60, "[]"));
                scenarios.add(new ARScenarioEntity("SCN-GAS-01", "MOD-GAS-01", "COAL_MINING", "Gas Leak & Confined Space Hazard Simulation", "CONFINED_SPACE_MINE", 3, 80, 60, "[]"));
                scenarioDao.insertScenarios(scenarios);

                // 7. Seed Questions & Options
                QuestionDao qDao = INSTANCE.questionDao();
                List<QuestionEntity> questions = new ArrayList<>();
                questions.add(new QuestionEntity("Q-COAL-01", "MOD-COAL-01", "SCN-COAL-01", "What is the maximum permissible methane concentration before mandatory withdrawal under DGMS Rule 115?", "At 1.25% CH4, all electric power must be disconnected and workers evacuated.", 10));
                qDao.insertQuestions(questions);

                List<QuestionOptionEntity> options = new ArrayList<>();
                options.add(new QuestionOptionEntity("OPT-01", "Q-COAL-01", "0.75%", false));
                options.add(new QuestionOptionEntity("OPT-02", "Q-COAL-01", "1.25%", true));
                options.add(new QuestionOptionEntity("OPT-03", "Q-COAL-01", "2.00%", false));
                options.add(new QuestionOptionEntity("OPT-04", "Q-COAL-01", "5.00%", false));
                qDao.insertQuestionOptions(options);

                // 8. Seed Training Assignments
                TrainingAssignmentDao assignDao = INSTANCE.trainingAssignmentDao();
                assignDao.insertAssignment(new TrainingAssignmentEntity("ASGN-COAL-01", "WRK-JH-COAL-0891", "MOD-FIRE-01", "Safety Overman B. N. Singh", now - (5L * 24 * 60 * 60 * 1000L), now + (2L * 24 * 60 * 60 * 1000L), "COMPLETED", "HIGH"));
                assignDao.insertAssignment(new TrainingAssignmentEntity("ASGN-COAL-02", "WRK-JH-COAL-0891", "MOD-GAS-01", "Safety Overman B. N. Singh", now - (1L * 24 * 60 * 60 * 1000L), now + (6L * 24 * 60 * 60 * 1000L), "ASSIGNED", "HIGH"));
                assignDao.insertAssignment(new TrainingAssignmentEntity("ASGN-STEEL-01", "WRK-JH-STEEL-1045", "MOD-STEEL-01", "Shift Supervisor R. K. Verma", now, now + (5L * 24 * 60 * 60 * 1000L), "ASSIGNED", "HIGH"));
                assignDao.insertAssignment(new TrainingAssignmentEntity("ASGN-MICA-01", "WRK-JH-MICA-0312", "MOD-MICA-01", "Unit Officer P. Murmu", now, now + (3L * 24 * 60 * 60 * 1000L), "ASSIGNED", "MEDIUM"));

                // 9. Seed Assessment Records
                AssessmentDao assessDao = INSTANCE.assessmentDao();
                assessDao.insertAssessment(new AssessmentRecordEntity(
                        "ASSESS-FIRE-001",
                        "WRK-JH-COAL-0891",
                        "MOD-FIRE-01",
                        "COAL_MINING",
                        94,
                        10,
                        9,
                        9,
                        true,
                        now - (2L * 24 * 60 * 60 * 1000L),
                        180,
                        true
                ));

                // 10. Seed Sample Certificates for Demonstration
                CertificateDao certDao = INSTANCE.certificateDao();
                List<CertificateEntity> demoCerts = new ArrayList<>();

                // Valid Certificate (Ramesh Soren - BCCL)
                String certId1 = "SAFETYAR-JH-2026-000001";
                long issued1 = now - (10L * 24 * 60 * 60 * 1000L);
                long expires1 = issued1 + (365L * 24 * 60 * 60 * 1000L);
                String token1 = com.safetyar.app.util.QrPassGenerator.computeVerificationToken(
                        certId1, "WRK-JH-COAL-0891", "Bharat Coking Coal Limited (BCCL)",
                        "Fire & Explosion Emergency Response", 94, issued1, expires1);
                String payload1 = com.safetyar.app.util.QrPassGenerator.buildQrPayload(
                        certId1, "WRK-JH-COAL-0891", "Ramesh Soren",
                        "Bharat Coking Coal Limited (BCCL)", "Fire & Explosion Emergency Response",
                        94, issued1, expires1, "VALID", "Jharkhand Industrial Safety Council (JISC)");
                demoCerts.add(new CertificateEntity(
                        certId1, "WRK-JH-COAL-0891", "Ramesh Soren",
                        "Bharat Coking Coal Limited (BCCL)", "Fire & Explosion Emergency Response",
                        94, "COAL_MINING", "Underground Drill & Strata Technician",
                        token1, token1, payload1, issued1, expires1,
                        "Jharkhand Industrial Safety Council (JISC)", "VALID", true
                ));

                // Expired Certificate (Arjun Mahto - Tata Steel)
                String certId2 = "SAFETYAR-JH-2026-000002";
                long issued2 = now - (400L * 24 * 60 * 60 * 1000L);
                long expires2 = now - (35L * 24 * 60 * 60 * 1000L);
                String token2 = com.safetyar.app.util.QrPassGenerator.computeVerificationToken(
                        certId2, "WRK-JH-STEEL-1045", "Tata Steel Limited",
                        "Blast Furnace Tapping & Molten Metal Splash", 88, issued2, expires2);
                String payload2 = com.safetyar.app.util.QrPassGenerator.buildQrPayload(
                        certId2, "WRK-JH-STEEL-1045", "Arjun Mahto",
                        "Tata Steel Limited", "Blast Furnace Tapping & Molten Metal Splash",
                        88, issued2, expires2, "EXPIRED", "Jharkhand Industrial Safety Council (JISC)");
                demoCerts.add(new CertificateEntity(
                        certId2, "WRK-JH-STEEL-1045", "Arjun Mahto",
                        "Tata Steel Limited", "Blast Furnace Tapping & Molten Metal Splash",
                        88, "STEEL_MANUFACTURING", "Blast Furnace Taphole & Slag Operator",
                        token2, token2, payload2, issued2, expires2,
                        "Jharkhand Industrial Safety Council (JISC)", "EXPIRED", true
                ));

                // Revoked Certificate (Sunita Murmu - JSMDC)
                String certId3 = "SAFETYAR-JH-2026-000003";
                long issued3 = now - (60L * 24 * 60 * 60 * 1000L);
                long expires3 = issued3 + (365L * 24 * 60 * 60 * 1000L);
                String token3 = com.safetyar.app.util.QrPassGenerator.computeVerificationToken(
                        certId3, "WRK-JH-MICA-0312", "Jharkhand State Mineral Dev Corp",
                        "Respirable Mica Dust & Silicosis Prevention", 82, issued3, expires3);
                String payload3 = com.safetyar.app.util.QrPassGenerator.buildQrPayload(
                        certId3, "WRK-JH-MICA-0312", "Sunita Murmu",
                        "Jharkhand State Mineral Dev Corp", "Respirable Mica Dust & Silicosis Prevention",
                        82, issued3, expires3, "REVOKED", "Jharkhand Industrial Safety Council (JISC)");
                demoCerts.add(new CertificateEntity(
                        certId3, "WRK-JH-MICA-0312", "Sunita Murmu",
                        "Jharkhand State Mineral Dev Corp", "Respirable Mica Dust & Silicosis Prevention",
                        82, "MICA_PROCESSING", "Mica Flake Quality Specialist",
                        token3, token3, payload3, issued3, expires3,
                        "Jharkhand Industrial Safety Council (JISC)", "REVOKED", true
                ));

                certDao.insertCertificates(demoCerts);
            });
        }
    };
}
