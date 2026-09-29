package com.safetyar.app.ar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
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

public class FireExplosionOverlayView extends View {

    // Particle Classes
    private static class FlameParticle {
        float x, y;
        float vx, vy;
        float size;
        float maxLife;
        float life;
        int color;
    }

    private static class SmokeParticle {
        float x, y;
        float vx, vy;
        float size;
        float maxLife;
        float life;
        int alpha;
    }

    private static class SprayParticle {
        float x, y;
        float vx, vy;
        float size;
        float maxLife;
        float life;
    }

    private final List<FlameParticle> flameParticles = new ArrayList<>();
    private final List<SmokeParticle> smokeParticles = new ArrayList<>();
    private final List<SprayParticle> sprayParticles = new ArrayList<>();

    // Paints
    private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint reticlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint floorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint alarmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint exitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Random random = new Random();
    private FireExplosionStateManager stateManager;
    private OnArObjectClickListener objectClickListener;

    // Simulation states
    private float fireHealth = 1.0f; // 1.0 = fully burning, 0.0 = extinguished
    private boolean isSpraying = false;
    private float sprayTargetX = 0f;
    private float sprayTargetY = 0f;
    private float sprayProgress = 0f; // 0 to 100%
    private float animTicker = 0f;

    // Touch hit targets
    private final RectF alarmBoxRect = new RectF();
    private final RectF exitPortalRect = new RectF();
    private final RectF musterPointRect = new RectF();

    public interface OnArObjectClickListener {
        void onFireTapped();
        void onAlarmTapped();
        void onExitRouteTapped();
        void onAssemblyPointTapped();
    }

    public FireExplosionOverlayView(Context context) {
        super(context);
        init();
    }

    public FireExplosionOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        reticlePaint.setColor(Color.parseColor("#00E5FF"));
        reticlePaint.setStyle(Paint.Style.STROKE);
        reticlePaint.setStrokeWidth(3f);

        floorPaint.setStyle(Paint.Style.STROKE);
        floorPaint.setStrokeWidth(3f);
        floorPaint.setColor(Color.parseColor("#FF9800"));
        floorPaint.setPathEffect(new DashPathEffect(new float[]{16f, 12f}, 0f));

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(26f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        badgePaint.setStyle(Paint.Style.FILL);
        badgePaint.setColor(Color.parseColor("#E60B0F19"));
    }

    public void setStateManager(FireExplosionStateManager stateManager) {
        this.stateManager = stateManager;
        if (stateManager != null && stateManager.isFireExtinguished()) {
            fireHealth = 0f;
        }
        invalidate();
    }

    public void setOnArObjectClickListener(OnArObjectClickListener listener) {
        this.objectClickListener = listener;
    }

    public void setSpraying(boolean spraying) {
        this.isSpraying = spraying;
    }

    public float getSprayProgress() {
        return sprayProgress;
    }

    public float getFireHealth() {
        return fireHealth;
    }

    public void resetFire() {
        fireHealth = 1.0f;
        sprayProgress = 0f;
        flameParticles.clear();
        smokeParticles.clear();
        sprayParticles.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        animTicker += 0.06f;

        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        float cx = w / 2f;
        float cy = h / 2f;

        float fireBaseX = cx;
        float fireBaseY = cy + 120f;

        boolean isExtinguished = (stateManager != null && stateManager.isFireExtinguished()) || fireHealth <= 0.02f;
        boolean isAlarmSounded = stateManager != null && stateManager.isAlarmSounded();

        // 1. Draw 3D Ground Standoff Perimeter & Light Illumination
        drawGroundPlaneHazard(canvas, fireBaseX, fireBaseY, isExtinguished);

        // 2. Draw Dynamic Flame & Smoke Particles
        if (!isExtinguished) {
            updateAndDrawFireParticles(canvas, fireBaseX, fireBaseY);
        } else {
            // Draw residue and subtle cooling smoke
            drawExtinguishedResidual(canvas, fireBaseX, fireBaseY);
        }

        // 3. Draw Pressurized Chemical Powder Spray (when active)
        if (isSpraying) {
            updateAndDrawSprayParticles(canvas, cx, h - 120f, fireBaseX, fireBaseY);
        }

        // 4. Draw Industrial Wall Emergency Alarm Box (Upper Left)
        drawWallAlarmStation(canvas, w * 0.18f, h * 0.22f, isAlarmSounded);

        // 5. Draw Evacuation Route & Muster Beacon (Active when fire is out or during evacuation steps)
        if (stateManager != null && (stateManager.getCurrentStep() == FireExplosionStateManager.Step.STEP_9_FOLLOW_EXIT_ROUTE ||
                stateManager.getCurrentStep() == FireExplosionStateManager.Step.STEP_10_REACH_ASSEMBLY ||
                isExtinguished)) {
            drawEvacuationChevronsAndMuster(canvas, cx, cy, w, h);
        }

        // 6. Draw Tactical AR Crosshair Reticle (Center screen)
        drawTacticalReticle(canvas, cx, cy, fireBaseX, fireBaseY);

        // Continuous animation loop (~30 FPS)
        postInvalidateDelayed(33);
    }

    private void drawGroundPlaneHazard(Canvas canvas, float bx, float by, boolean isExtinguished) {
        float scale = isExtinguished ? 0.4f : fireHealth;

        // Ground illumination glow
        if (!isExtinguished && scale > 0.1f) {
            float glowRadius = 260f * scale;
            RadialGradient glow = new RadialGradient(
                    bx, by, glowRadius,
                    new int[]{Color.parseColor("#4DFF9800"), Color.parseColor("#1AFF5722"), Color.TRANSPARENT},
                    new float[]{0f, 0.5f, 1f},
                    Shader.TileMode.CLAMP
            );
            Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            glowPaint.setShader(glow);
            canvas.drawOval(new RectF(bx - glowRadius, by - 60f * scale, bx + glowRadius, by + 60f * scale), glowPaint);
        }

        // 3D Perspective Elliptical Floor Ring (3-meter Standoff Zone)
        float ringRadiusX = 220f;
        float ringRadiusY = 70f;
        RectF floorEllipse = new RectF(bx - ringRadiusX, by - ringRadiusY, bx + ringRadiusX, by + ringRadiusY);

        if (!isExtinguished) {
            floorPaint.setColor(Color.parseColor("#FF9800"));
            canvas.drawOval(floorEllipse, floorPaint);
            drawHolographicLabel(canvas, "3M STANDOFF BOUNDARY", bx, by + ringRadiusY + 28f, Color.parseColor("#FF9800"));
        } else {
            floorPaint.setColor(Color.parseColor("#10B981"));
            canvas.drawOval(floorEllipse, floorPaint);
            drawHolographicLabel(canvas, "ZONE DECONTAMINATED \u2713", bx, by + ringRadiusY + 28f, Color.parseColor("#10B981"));
        }
    }

    private void updateAndDrawFireParticles(Canvas canvas, float bx, float by) {
        // Spawn new flame particles based on current fire health
        int spawnCount = (int) (6 * fireHealth);
        for (int i = 0; i < spawnCount; i++) {
            FlameParticle p = new FlameParticle();
            p.x = bx + (random.nextFloat() - 0.5f) * (90f * fireHealth);
            p.y = by + (random.nextFloat() - 0.5f) * 20f;
            p.vx = (random.nextFloat() - 0.5f) * 3f;
            p.vy = -(6f + random.nextFloat() * 12f) * fireHealth;
            p.maxLife = 20f + random.nextFloat() * 18f;
            p.life = p.maxLife;
            p.size = (28f + random.nextFloat() * 32f) * fireHealth;

            // Temperature gradient
            float heat = random.nextFloat();
            if (heat > 0.7f) {
                p.color = Color.parseColor("#FFFDE7"); // White-yellow core
            } else if (heat > 0.35f) {
                p.color = Color.parseColor("#FF9800"); // Vivid amber flame
            } else {
                p.color = Color.parseColor("#FF1744"); // Red-orange tip
            }
            flameParticles.add(p);
        }

        // Spawn smoke particles at the top of flames
        if (random.nextFloat() < (0.6f * fireHealth)) {
            SmokeParticle s = new SmokeParticle();
            s.x = bx + (random.nextFloat() - 0.5f) * 60f;
            s.y = by - (110f * fireHealth) - random.nextFloat() * 40f;
            s.vx = (random.nextFloat() - 0.5f) * 2.5f + (float) Math.sin(animTicker) * 1.5f;
            s.vy = -(3f + random.nextFloat() * 5f);
            s.maxLife = 40f + random.nextFloat() * 25f;
            s.life = s.maxLife;
            s.size = 35f + random.nextFloat() * 30f;
            s.alpha = 90;
            smokeParticles.add(s);
        }

        // Update and draw smoke particles
        Iterator<SmokeParticle> sIter = smokeParticles.iterator();
        while (sIter.hasNext()) {
            SmokeParticle s = sIter.next();
            s.x += s.vx;
            s.y += s.vy;
            s.size += 0.8f;
            s.life--;

            float progress = s.life / s.maxLife;
            int alpha = (int) (s.alpha * progress);
            if (s.life <= 0 || alpha <= 0) {
                sIter.remove();
                continue;
            }

            particlePaint.setColor(Color.argb(alpha, 60, 75, 95));
            particlePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(s.x, s.y, s.size, particlePaint);
        }

        // Update and draw flame particles
        Iterator<FlameParticle> fIter = flameParticles.iterator();
        while (fIter.hasNext()) {
            FlameParticle p = fIter.next();
            p.x += p.vx + (float) Math.sin(animTicker * 2f + p.y * 0.05f) * 1.5f;
            p.y += p.vy;
            p.size = Math.max(4f, p.size * 0.94f);
            p.life--;

            if (p.life <= 0 || p.size <= 4f) {
                fIter.remove();
                continue;
            }

            float ratio = p.life / p.maxLife;
            int alpha = (int) (240 * ratio);
            particlePaint.setColor(p.color);
            particlePaint.setAlpha(alpha);
            particlePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(p.x, p.y, p.size, particlePaint);
        }

        // Flame base tag
        drawHolographicLabel(canvas, "METHANE GAS FLARE [CLASS B/C]", bx, by - (170f * fireHealth), Color.parseColor("#FF1744"));
    }

    private void drawExtinguishedResidual(Canvas canvas, float bx, float by) {
        // Cold chemical powder pile
        Paint powderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        powderPaint.setColor(Color.parseColor("#D0E2E8F0"));
        powderPaint.setStyle(Paint.Style.FILL);
        canvas.drawOval(new RectF(bx - 90f, by - 20f, bx + 90f, by + 20f), powderPaint);

        // Thin wisps of white smoke
        if (random.nextFloat() < 0.25f) {
            SmokeParticle s = new SmokeParticle();
            s.x = bx + (random.nextFloat() - 0.5f) * 50f;
            s.y = by - 10f;
            s.vx = (random.nextFloat() - 0.5f) * 1.5f;
            s.vy = -(2f + random.nextFloat() * 3f);
            s.maxLife = 35f;
            s.life = s.maxLife;
            s.size = 18f;
            s.alpha = 70;
            smokeParticles.add(s);
        }

        Iterator<SmokeParticle> sIter = smokeParticles.iterator();
        while (sIter.hasNext()) {
            SmokeParticle s = sIter.next();
            s.x += s.vx;
            s.y += s.vy;
            s.size += 0.5f;
            s.life--;
            if (s.life <= 0) {
                sIter.remove();
                continue;
            }
            float progress = s.life / s.maxLife;
            particlePaint.setColor(Color.argb((int) (s.alpha * progress), 200, 215, 230));
            particlePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(s.x, s.y, s.size, particlePaint);
        }

        drawHolographicLabel(canvas, "FIRE SUPPRESSED \u2022 PASS SECURED", bx, by - 60f, Color.parseColor("#10B981"));
    }

    private void updateAndDrawSprayParticles(Canvas canvas, float originX, float originY, float targetX, float targetY) {
        // Spawn white chemical powder particles streaming from bottom toward target
        for (int i = 0; i < 14; i++) {
            SprayParticle sp = new SprayParticle();
            sp.x = originX + (random.nextFloat() - 0.5f) * 40f;
            sp.y = originY;

            float dx = (targetX - originX) + (random.nextFloat() - 0.5f) * 120f;
            float dy = (targetY - originY) + (random.nextFloat() - 0.5f) * 40f;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            float speed = 26f + random.nextFloat() * 10f;
            sp.vx = (dx / dist) * speed;
            sp.vy = (dy / dist) * speed;
            sp.maxLife = dist / speed;
            sp.life = sp.maxLife;
            sp.size = 14f + random.nextFloat() * 20f;
            sprayParticles.add(sp);
        }

        // Draw and update spray particles
        Iterator<SprayParticle> spIter = sprayParticles.iterator();
        while (spIter.hasNext()) {
            SprayParticle sp = spIter.next();
            sp.x += sp.vx;
            sp.y += sp.vy;
            sp.size += 1.4f;
            sp.life--;

            if (sp.life <= 0) {
                spIter.remove();
                continue;
            }

            float ratio = sp.life / sp.maxLife;
            int alpha = (int) (220 * ratio);
            particlePaint.setColor(Color.argb(alpha, 240, 250, 255));
            particlePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(sp.x, sp.y, sp.size, particlePaint);
        }

        // If spraying near fire base, reduce fire health
        float distToTarget = (float) Math.sqrt(Math.pow(sprayTargetX - targetX, 2) + Math.pow(sprayTargetY - targetY, 2));
        boolean onTarget = distToTarget < 260f || true; // Sweeping interaction

        if (onTarget && fireHealth > 0f) {
            fireHealth = Math.max(0f, fireHealth - 0.012f);
            sprayProgress = Math.min(100f, (1.0f - fireHealth) * 100f);

            if (fireHealth <= 0.02f && stateManager != null && !stateManager.isFireExtinguished()) {
                stateManager.processAction(FireExplosionStateManager.UserAction.ACTION_PASS_SWEEP);
            }
        }
    }

    private void drawWallAlarmStation(Canvas canvas, float ax, float ay, boolean isAlarmSounded) {
        float size = 48f;
        alarmBoxRect.set(ax - size, ay - size, ax + size, ay + size);

        // Box shadow & body
        alarmPaint.setStyle(Paint.Style.FILL);
        alarmPaint.setColor(Color.parseColor("#B91C1C")); // Industrial safety red
        canvas.drawRoundRect(alarmBoxRect, 14f, 14f, alarmPaint);

        // Border
        alarmPaint.setStyle(Paint.Style.STROKE);
        alarmPaint.setStrokeWidth(3f);
        alarmPaint.setColor(isAlarmSounded ? Color.parseColor("#00E5FF") : Color.parseColor("#EF4444"));
        canvas.drawRoundRect(alarmBoxRect, 14f, 14f, alarmPaint);

        // Center glass / pull lever
        Paint leverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        leverPaint.setColor(Color.parseColor("#F59E0B"));
        canvas.drawRoundRect(new RectF(ax - 20f, ay - 10f, ax + 20f, ay + 24f), 6f, 6f, leverPaint);

        if (isAlarmSounded) {
            // Animated siren wave rings
            Paint sirenRing = new Paint(Paint.ANTI_ALIAS_FLAG);
            sirenRing.setStyle(Paint.Style.STROKE);
            sirenRing.setColor(Color.parseColor("#8000E5FF"));
            sirenRing.setStrokeWidth(3f);

            float pulse = (animTicker * 25f) % 60f;
            canvas.drawCircle(ax, ay, 40f + pulse, sirenRing);
            canvas.drawCircle(ax, ay, 55f + pulse, sirenRing);

            drawHolographicLabel(canvas, "SIREN ACTIVE \u26A0 RESCUE ALERTED", ax, ay + size + 28f, Color.parseColor("#00E5FF"));
        } else {
            drawHolographicLabel(canvas, "PULL EMERGENCY ALARM", ax, ay + size + 28f, Color.parseColor("#EF4444"));
        }
    }

    private void drawEvacuationChevronsAndMuster(Canvas canvas, float cx, float cy, int w, int h) {
        // Animated glowing floor chevrons pointing toward evacuation route
        exitPaint.setColor(Color.parseColor("#10B981"));
        exitPaint.setStyle(Paint.Style.STROKE);
        exitPaint.setStrokeWidth(6f);
        exitPaint.setStrokeCap(Paint.Cap.ROUND);

        float startX = cx + 40f;
        float startY = cy + 100f;
        float offset = (animTicker * 40f) % 60f;

        for (int i = 0; i < 4; i++) {
            float y = startY - (i * 60f) - offset;
            float x = startX + (i * 50f);
            if (y > cy - 140f && y < h - 160f) {
                canvas.drawLine(x - 24f, y + 16f, x, y, exitPaint);
                canvas.drawLine(x, y, x - 24f, y - 16f, exitPaint);
            }
        }

        // Muster Assembly Point Beacon (Upper Right)
        float mx = w * 0.78f;
        float my = h * 0.25f;
        musterPointRect.set(mx - 60f, my - 60f, mx + 60f, my + 60f);

        Paint musterBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        musterBg.setColor(Color.parseColor("#065F46")); // Dark green
        canvas.drawCircle(mx, my, 40f, musterBg);

        Paint musterBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        musterBorder.setStyle(Paint.Style.STROKE);
        musterBorder.setStrokeWidth(3f);
        musterBorder.setColor(Color.parseColor("#10B981"));
        canvas.drawCircle(mx, my, 40f, musterBorder);

        // Star inside
        textPaint.setTextSize(26f);
        textPaint.setColor(Color.WHITE);
        canvas.drawText("\u2605", mx, my + 10f, textPaint);

        drawHolographicLabel(canvas, "MUSTER ASSEMBLY POINT", mx, my + 65f, Color.parseColor("#10B981"));
    }

    private void drawTacticalReticle(Canvas canvas, float cx, float cy, float fireBaseX, float fireBaseY) {
        // Holographic crosshair reticle
        float size = 38f;
        float gap = 12f;

        // Check if aimed near flame base
        float distToBase = (float) Math.sqrt(Math.pow(cx - fireBaseX, 2) + Math.pow(cy - fireBaseY, 2));
        boolean isAimedAtBase = distToBase < 180f;

        if (isAimedAtBase && stateManager != null && stateManager.getCurrentStep() == FireExplosionStateManager.Step.STEP_6_PASS_AIM) {
            reticlePaint.setColor(Color.parseColor("#10B981"));
            reticlePaint.setStrokeWidth(4.5f);
        } else {
            reticlePaint.setColor(Color.parseColor("#00E5FF"));
            reticlePaint.setStrokeWidth(3f);
        }

        // Corner reticle brackets
        canvas.drawLine(cx - size, cy - size, cx - size + gap, cy - size, reticlePaint);
        canvas.drawLine(cx - size, cy - size, cx - size, cy - size + gap, reticlePaint);

        canvas.drawLine(cx + size, cy - size, cx + size - gap, cy - size, reticlePaint);
        canvas.drawLine(cx + size, cy - size, cx + size, cy - size + gap, reticlePaint);

        canvas.drawLine(cx - size, cy + size, cx - size + gap, cy + size, reticlePaint);
        canvas.drawLine(cx - size, cy + size, cx - size, cy + size - gap, reticlePaint);

        canvas.drawLine(cx + size, cy + size, cx + size - gap, cy + size, reticlePaint);
        canvas.drawLine(cx + size, cy + size, cx + size, cy + size - gap, reticlePaint);

        // Center dot
        reticlePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, 3f, reticlePaint);
        reticlePaint.setStyle(Paint.Style.STROKE);

        // Reticle status prompt
        if (isAimedAtBase && stateManager != null && stateManager.getCurrentStep() == FireExplosionStateManager.Step.STEP_6_PASS_AIM) {
            drawHolographicLabel(canvas, "[AIM: FLAME BASE LOCKED \u2713]", cx, cy + size + 24f, Color.parseColor("#10B981"));
        }
    }

    private void drawHolographicLabel(Canvas canvas, String text, float x, float y, int accentColor) {
        textPaint.setTextSize(20f);
        textPaint.setColor(Color.WHITE);
        float tw = textPaint.measureText(text) + 24f;
        RectF r = new RectF(x - tw / 2f, y - 18f, x + tw / 2f, y + 14f);

        canvas.drawRoundRect(r, 8f, 8f, badgePaint);

        Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(2f);
        stroke.setColor(accentColor);
        canvas.drawRoundRect(r, 8f, 8f, stroke);

        canvas.drawText(text, x, y + 5f, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tx = event.getX();
        float ty = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                sprayTargetX = tx;
                sprayTargetY = ty;

                // Hit test: Wall Alarm
                if (alarmBoxRect.contains(tx, ty)) {
                    if (objectClickListener != null) {
                        objectClickListener.onAlarmTapped();
                    }
                    return true;
                }

                // Hit test: Muster Point
                if (musterPointRect.contains(tx, ty)) {
                    if (objectClickListener != null) {
                        objectClickListener.onAssemblyPointTapped();
                    }
                    return true;
                }

                // Hit test: Fire Base
                float cx = getWidth() / 2f;
                float cy = getHeight() / 2f + 120f;
                float d = (float) Math.sqrt(Math.pow(tx - cx, 2) + Math.pow(ty - cy, 2));
                if (d < 180f) {
                    if (objectClickListener != null) {
                        objectClickListener.onFireTapped();
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
