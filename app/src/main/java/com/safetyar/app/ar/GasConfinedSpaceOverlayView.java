package com.safetyar.app.ar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GasConfinedSpaceOverlayView extends View {

    public interface OnGasScenarioObjectClickListener {
        void onGasLeakTapped();
        void onDangerZoneTapped();
        void onConfinedSpaceTapped();
        void onPpeStationTapped();
        void onGasDetectorTapped();
        void onBuddyTapped();
        void onPermitTapped();
        void onEvacuationRouteTapped();
        void onPlaneTapped(float x, float y);
    }

    private static class GasParticle {
        float x, y;
        float vx, vy;
        float size;
        float maxLife;
        float life;
        int color;
    }

    private final List<GasParticle> gasParticles = new ArrayList<>();
    private final Random random = new Random();

    private GasConfinedSpaceStateManager stateManager;
    private OnGasScenarioObjectClickListener listener;

    private boolean isScenarioPlaced = true;
    private float anchorX = -1;
    private float anchorY = -1;
    private float animPhase = 0f;

    // Paints
    private final Paint gasPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hazardZonePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint safeZonePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint portalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint reticlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hudBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Touch hit-test regions
    private final RectF gasLeakRect = new RectF();
    private final RectF dangerZoneRect = new RectF();
    private final RectF portalRect = new RectF();
    private final RectF ppeRect = new RectF();
    private final RectF detectorRect = new RectF();
    private final RectF buddyRect = new RectF();
    private final RectF permitRect = new RectF();
    private final RectF evacuationRect = new RectF();

    public GasConfinedSpaceOverlayView(Context context) {
        super(context);
        init();
    }

    public GasConfinedSpaceOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        hazardZonePaint.setStyle(Paint.Style.STROKE);
        hazardZonePaint.setStrokeWidth(4f);
        hazardZonePaint.setColor(Color.parseColor("#FF1744"));
        hazardZonePaint.setPathEffect(new DashPathEffect(new float[]{20f, 15f}, 0f));

        safeZonePaint.setStyle(Paint.Style.STROKE);
        safeZonePaint.setStrokeWidth(3.5f);
        safeZonePaint.setColor(Color.parseColor("#10B981"));
        safeZonePaint.setPathEffect(new DashPathEffect(new float[]{16f, 12f}, 0f));

        reticlePaint.setColor(Color.parseColor("#00E5FF"));
        reticlePaint.setStyle(Paint.Style.STROKE);
        reticlePaint.setStrokeWidth(3f);

        hudBgPaint.setColor(Color.parseColor("#E60B0F19"));
        hudBgPaint.setStyle(Paint.Style.FILL);

        labelBgPaint.setColor(Color.parseColor("#E60B0F19"));
        labelBgPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(24f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setStrokeWidth(6f);
        arrowPaint.setColor(Color.parseColor("#10B981"));
        arrowPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setStateManager(GasConfinedSpaceStateManager manager) {
        this.stateManager = manager;
        postInvalidate();
    }

    public void setOnGasScenarioObjectClickListener(OnGasScenarioObjectClickListener listener) {
        this.listener = listener;
    }

    public void placeScenarioAt(float x, float y) {
        this.anchorX = x;
        this.anchorY = y;
        this.isScenarioPlaced = true;
        postInvalidate();
    }

    public boolean isScenarioPlaced() {
        return isScenarioPlaced;
    }

    public void setScenarioPlaced(boolean placed) {
        this.isScenarioPlaced = placed;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        animPhase += 0.05f;

        float width = getWidth();
        float height = getHeight();
        if (width == 0 || height == 0) return;

        float cx = anchorX > 0 ? anchorX : width / 2f;
        float cy = anchorY > 0 ? anchorY : height / 2f;

        GasConfinedSpaceStateManager.Step currentStep = stateManager != null ?
                stateManager.getCurrentStep() : GasConfinedSpaceStateManager.Step.STEP_1_IDENTIFY_GAS_LEAK;

        // 1. Draw Volumetric Toxic Gas Plume & Flange Joint (Steps 1, 2, 3, or always in background)
        drawVolumetricGasPlume(canvas, cx - 110f, cy + 20f);

        // 2. Draw Context-Relevant Objects (Avoid screen clutter!)
        switch (currentStep) {
            case STEP_1_IDENTIFY_GAS_LEAK:
            case STEP_2_IDENTIFY_DANGER_ZONE:
            case STEP_3_AVOID_DANGER_ZONE:
                drawDangerAndSafeZones(canvas, cx - 110f, cy + 20f, cx, cy);
                break;

            case STEP_4_IDENTIFY_CONFINED_SPACE:
            case STEP_9_SAFE_ENTRY_PROCEDURE:
                drawConfinedSpacePortal(canvas, cx + 130f, cy - 30f);
                break;

            case STEP_5_SELECT_PPE:
                drawPpeStation(canvas, cx + 120f, cy + 50f);
                break;

            case STEP_6_USE_GAS_DETECTOR:
                drawDigital4GasTelemetry(canvas, width, height);
                break;

            case STEP_7_FOLLOW_BUDDY_SYSTEM:
                drawStandbyBuddyPost(canvas, cx + 120f, cy + 50f);
                break;

            case STEP_8_VERIFY_AUTHORIZATION:
                drawSupervisorPermitBadge(canvas, cx, cy - 140f);
                break;

            case STEP_10_EMERGENCY_EVACUATION:
                drawEvacuationRoute(canvas, cx, cy + 120f, width);
                break;
        }

        // 3. Draw Center Tactical Reticle
        drawTacticalReticle(canvas, width / 2f, height / 2f);

        postInvalidateDelayed(33); // ~30 FPS
    }

    private void drawVolumetricGasPlume(Canvas canvas, float gx, float gy) {
        // Metallic Pipe Flange Joint
        Paint pipePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pipePaint.setColor(Color.parseColor("#475569"));
        canvas.drawRoundRect(new RectF(gx - 26f, gy + 35f, gx + 26f, gy + 55f), 6f, 6f, pipePaint);

        Paint boltPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boltPaint.setColor(Color.parseColor("#94A3B8"));
        canvas.drawCircle(gx - 14f, gy + 45f, 3.5f, boltPaint);
        canvas.drawCircle(gx + 14f, gy + 45f, 3.5f, boltPaint);

        gasLeakRect.set(gx - 60f, gy - 60f, gx + 60f, gy + 60f);

        // Spawn drifting toxic gas particles
        for (int i = 0; i < 4; i++) {
            GasParticle gp = new GasParticle();
            gp.x = gx + (random.nextFloat() - 0.5f) * 20f;
            gp.y = gy + 35f;
            gp.vx = (random.nextFloat() - 0.4f) * 3f + (float) Math.sin(animPhase) * 1.5f;
            gp.vy = -(4f + random.nextFloat() * 6f);
            gp.maxLife = 30f + random.nextFloat() * 20f;
            gp.life = gp.maxLife;
            gp.size = 20f + random.nextFloat() * 25f;

            // Toxic yellow/chartreuse gradient
            if (random.nextBoolean()) {
                gp.color = Color.parseColor("#FACC15"); // Toxic yellow
            } else {
                gp.color = Color.parseColor("#84CC16"); // Toxic sulfur green
            }
            gasParticles.add(gp);
        }

        // Draw and update gas particles
        Iterator<GasParticle> gIter = gasParticles.iterator();
        while (gIter.hasNext()) {
            GasParticle gp = gIter.next();
            gp.x += gp.vx;
            gp.y += gp.vy;
            gp.size += 0.8f;
            gp.life--;

            if (gp.life <= 0) {
                gIter.remove();
                continue;
            }

            float ratio = gp.life / gp.maxLife;
            int alpha = (int) (140 * ratio);
            gasPaint.setColor(gp.color);
            gasPaint.setAlpha(alpha);
            gasPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(gp.x, gp.y, gp.size, gasPaint);
        }

        // Label
        drawBadge(canvas, "TOXIC GAS LEAK SOURCE", gx, gy - 70f, Color.parseColor("#FF1744"));
    }

    private void drawDangerAndSafeZones(Canvas canvas, float gx, float gy, float cx, float cy) {
        // Red Danger Zone Boundary
        float hazardRadius = 130f + (float) Math.sin(animPhase * 2f) * 6f;
        canvas.drawCircle(gx, gy, hazardRadius, hazardZonePaint);
        dangerZoneRect.set(gx - hazardRadius, gy - hazardRadius, gx + hazardRadius, gy + hazardRadius);

        drawBadge(canvas, "RED DANGER ZONE (CH4 &gt; 2.0%)", gx, gy + hazardRadius + 24f, Color.parseColor("#FF1744"));

        // 10-meter Safe Upwind Zone
        float safeRadius = 260f;
        canvas.drawCircle(cx, cy, safeRadius, safeZonePaint);
        drawBadge(canvas, "10-METER SAFE UPWIND PERIMETER \u2713", cx, cy + safeRadius + 24f, Color.parseColor("#10B981"));
    }

    private void drawConfinedSpacePortal(Canvas canvas, float px, float py) {
        portalRect.set(px - 90f, py - 90f, px + 90f, py + 90f);

        // Outer hatch rim with amber hazard
        portalPaint.setStyle(Paint.Style.STROKE);
        portalPaint.setStrokeWidth(10f);
        portalPaint.setColor(Color.parseColor("#F59E0B"));
        canvas.drawCircle(px, py, 72f, portalPaint);

        // Dark interior void
        portalPaint.setStyle(Paint.Style.FILL);
        portalPaint.setColor(Color.parseColor("#0B0F19"));
        canvas.drawCircle(px, py, 66f, portalPaint);

        // Diagonal hazard stripes
        Paint stripe = new Paint(Paint.ANTI_ALIAS_FLAG);
        stripe.setColor(Color.parseColor("#D97706"));
        stripe.setStrokeWidth(3.5f);
        canvas.drawLine(px - 45f, py - 45f, px + 45f, py + 45f, stripe);
        canvas.drawLine(px - 45f, py + 45f, px + 45f, py - 45f, stripe);

        drawBadge(canvas, "CONFINED SPACE PORTAL", px, py - 90f, Color.parseColor("#F59E0B"));
    }

    private void drawPpeStation(Canvas canvas, float sx, float sy) {
        ppeRect.set(sx - 90f, sy - 55f, sx + 90f, sy + 55f);

        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(Color.parseColor("#E60B0F19"));
        canvas.drawRoundRect(ppeRect, 16f, 16f, bg);

        Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(2.5f);
        border.setColor(Color.parseColor("#00E5FF"));
        canvas.drawRoundRect(ppeRect, 16f, 16f, border);

        textPaint.setTextSize(20f);
        textPaint.setColor(Color.WHITE);
        canvas.drawText("SCBA PPE STATION", sx, sy - 14f, textPaint);

        textPaint.setTextSize(16f);
        textPaint.setColor(Color.parseColor("#38BDF8"));
        canvas.drawText("Equip Positive Pressure", sx, sy + 16f, textPaint);
        canvas.drawText("Apparatus &amp; Harness", sx, sy + 38f, textPaint);
    }

    private void drawDigital4GasTelemetry(Canvas canvas, float w, float h) {
        float hudW = 280f;
        float hudH = 150f;
        float hx = w - hudW - 20f;
        float hy = 90f;
        detectorRect.set(hx, hy, hx + hudW, hy + hudH);

        canvas.drawRoundRect(detectorRect, 16f, 16f, hudBgPaint);

        Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(2f);
        border.setColor(Color.parseColor("#00E5FF"));
        canvas.drawRoundRect(detectorRect, 16f, 16f, border);

        // Header
        textPaint.setTextSize(18f);
        textPaint.setColor(Color.parseColor("#00E5FF"));
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("4-GAS DETECTOR TELEMETRY", hx + 16f, hy + 28f, textPaint);

        // Readings
        textPaint.setTextSize(16f);
        textPaint.setColor(Color.parseColor("#FF1744"));
        canvas.drawText("CH4 (Methane):  2.4% [CRITICAL]", hx + 16f, hy + 58f, textPaint);

        textPaint.setColor(Color.parseColor("#F59E0B"));
        canvas.drawText("O2  (Oxygen):   18.2% [LOW DEFICIENT]", hx + 16f, hy + 86f, textPaint);

        textPaint.setColor(Color.parseColor("#38BDF8"));
        canvas.drawText("CO  (Carbon Monoxide): 38 ppm", hx + 16f, hy + 114f, textPaint);
        canvas.drawText("H2S (Hydrogen Sulfide): 14 ppm", hx + 16f, hy + 138f, textPaint);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void drawStandbyBuddyPost(Canvas canvas, float bx, float by) {
        buddyRect.set(bx - 80f, by - 50f, bx + 80f, by + 50f);

        canvas.drawRoundRect(buddyRect, 14f, 14f, hudBgPaint);

        Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(2f);
        border.setColor(Color.parseColor("#10B981"));
        canvas.drawRoundRect(buddyRect, 14f, 14f, border);

        drawBadge(canvas, "SAFETY BUDDY POST", bx, by - 65f, Color.parseColor("#10B981"));
        textPaint.setTextSize(18f);
        textPaint.setColor(Color.WHITE);
        canvas.drawText("Lifeline Standby Ready", bx, by + 8f, textPaint);
    }

    private void drawSupervisorPermitBadge(Canvas canvas, float px, float py) {
        permitRect.set(px - 140f, py - 36f, px + 140f, py + 36f);

        canvas.drawRoundRect(permitRect, 14f, 14f, hudBgPaint);

        Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(2f);
        border.setColor(Color.parseColor("#10B981"));
        canvas.drawRoundRect(permitRect, 14f, 14f, border);

        textPaint.setTextSize(18f);
        textPaint.setColor(Color.parseColor("#10B981"));
        canvas.drawText("PERMIT-TO-WORK: VALID \u2713", px, py + 6f, textPaint);
    }

    private void drawEvacuationRoute(Canvas canvas, float cx, float cy, float w) {
        float offset = (animPhase * 35f) % 50f;
        for (int i = 0; i < 4; i++) {
            float y = cy - (i * 55f) - offset;
            float x = cx + (i * 45f);
            canvas.drawLine(x - 24f, y + 16f, x, y, arrowPaint);
            canvas.drawLine(x, y, x - 24f, y - 16f, arrowPaint);
        }

        evacuationRect.set(cx - 100f, cy - 80f, cx + 180f, cy + 80f);
        drawBadge(canvas, "EVACUATE UPWIND \u2192", cx + 70f, cy + 90f, Color.parseColor("#10B981"));
    }

    private void drawTacticalReticle(Canvas canvas, float cx, float cy) {
        float size = 32f;
        float gap = 10f;

        canvas.drawLine(cx - size, cy - size, cx - size + gap, cy - size, reticlePaint);
        canvas.drawLine(cx - size, cy - size, cx - size, cy - size + gap, reticlePaint);

        canvas.drawLine(cx + size, cy - size, cx + size - gap, cy - size, reticlePaint);
        canvas.drawLine(cx + size, cy - size, cx + size, cy - size + gap, reticlePaint);

        canvas.drawLine(cx - size, cy + size, cx - size + gap, cy + size, reticlePaint);
        canvas.drawLine(cx - size, cy + size, cx - size, cy + size - gap, reticlePaint);

        canvas.drawLine(cx + size, cy + size, cx + size - gap, cy + size, reticlePaint);
        canvas.drawLine(cx + size, cy + size, cx + size, cy - size + gap, reticlePaint);

        reticlePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, 3f, reticlePaint);
        reticlePaint.setStyle(Paint.Style.STROKE);
    }

    private void drawBadge(Canvas canvas, String label, float x, float y, int strokeColor) {
        textPaint.setTextSize(20f);
        textPaint.setColor(Color.WHITE);
        float width = textPaint.measureText(label) + 24f;
        RectF rect = new RectF(x - (width / 2), y - 16f, x + (width / 2), y + 14f);

        canvas.drawRoundRect(rect, 8f, 8f, labelBgPaint);

        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(strokeColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);
        canvas.drawRoundRect(rect, 8f, 8f, borderPaint);

        canvas.drawText(label, x, y + 5f, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            if (listener == null) return super.onTouchEvent(event);

            if (gasLeakRect.contains(x, y)) {
                listener.onGasLeakTapped();
                return true;
            }
            if (dangerZoneRect.contains(x, y)) {
                listener.onDangerZoneTapped();
                return true;
            }
            if (portalRect.contains(x, y)) {
                listener.onConfinedSpaceTapped();
                return true;
            }
            if (ppeRect.contains(x, y)) {
                listener.onPpeStationTapped();
                return true;
            }
            if (detectorRect.contains(x, y)) {
                listener.onGasDetectorTapped();
                return true;
            }
            if (buddyRect.contains(x, y)) {
                listener.onBuddyTapped();
                return true;
            }
            if (permitRect.contains(x, y)) {
                listener.onPermitTapped();
                return true;
            }
            if (evacuationRect.contains(x, y)) {
                listener.onEvacuationRouteTapped();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
