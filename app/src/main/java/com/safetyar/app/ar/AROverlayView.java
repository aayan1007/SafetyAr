package com.safetyar.app.ar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.safetyar.app.domain.model.HazardItem;

import java.util.ArrayList;
import java.util.List;

public class AROverlayView extends View {

    private final Paint reticlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pulsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hazardCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hazardTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cardBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cardBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float pulseRadius = 30f;
    private ValueAnimator pulseAnimator;

    private List<HazardItem> activeHazards = new ArrayList<>();
    private HazardItem selectedHazard = null;
    private OnHazardClickListener hazardClickListener;

    public interface OnHazardClickListener {
        void onHazardSelected(HazardItem hazard);
    }

    public AROverlayView(Context context) {
        super(context);
        init();
    }

    public AROverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        reticlePaint.setColor(Color.parseColor("#00E5FF"));
        reticlePaint.setStyle(Paint.Style.STROKE);
        reticlePaint.setStrokeWidth(4f);

        pulsePaint.setColor(Color.parseColor("#4000E5FF"));
        pulsePaint.setStyle(Paint.Style.FILL);

        hazardCirclePaint.setStyle(Paint.Style.FILL);
        hazardTextPaint.setColor(Color.WHITE);
        hazardTextPaint.setTextSize(32f);
        hazardTextPaint.setFakeBoldText(true);

        cardBgPaint.setColor(Color.parseColor("#E60F172A"));
        cardBgPaint.setStyle(Paint.Style.FILL);

        cardBorderPaint.setColor(Color.parseColor("#FF6F00"));
        cardBorderPaint.setStyle(Paint.Style.STROKE);
        cardBorderPaint.setStrokeWidth(3f);

        pulseAnimator = ValueAnimator.ofFloat(25f, 65f);
        pulseAnimator.setDuration(1200);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ValueAnimator.RESTART);
        pulseAnimator.addUpdateListener(anim -> {
            pulseRadius = (float) anim.getAnimatedValue();
            invalidate();
        });
        pulseAnimator.start();
    }

    public void setHazards(List<HazardItem> hazards) {
        this.activeHazards = hazards != null ? hazards : new ArrayList<>();
        invalidate();
    }

    public void setSelectedHazard(HazardItem hazard) {
        this.selectedHazard = hazard;
        invalidate();
    }

    public void setOnHazardClickListener(OnHazardClickListener listener) {
        this.hazardClickListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // Draw animated center HUD reticle
        canvas.drawCircle(cx, cy, pulseRadius, pulsePaint);
        canvas.drawCircle(cx, cy, 25f, reticlePaint);
        canvas.drawLine(cx - 35f, cy, cx - 15f, cy, reticlePaint);
        canvas.drawLine(cx + 15f, cy, cx + 35f, cy, reticlePaint);
        canvas.drawLine(cx, cy - 35f, cx, cy - 15f, reticlePaint);
        canvas.drawLine(cx, cy + 15f, cx, cy + 35f, reticlePaint);

        // Draw Simulated AR Hazard 3D/2D spatial projection pins
        if (activeHazards != null && !activeHazards.isEmpty()) {
            for (int i = 0; i < activeHazards.size(); i++) {
                HazardItem item = activeHazards.get(i);

                // Project normalized offsets into view coordinates
                float x = cx + (item.getPosX() * (getWidth() * 0.4f));
                float y = cy + (item.getPosY() * (getHeight() * 0.35f));

                int markerColor;
                if (item.isMitigated()) {
                    markerColor = Color.parseColor("#388E3C"); // Green
                } else if (item.isIdentified()) {
                    markerColor = Color.parseColor(item.getSeverity().getColorHex());
                } else {
                    markerColor = Color.parseColor("#F57C00"); // Unidentified warning orange
                }

                hazardCirclePaint.setColor(markerColor);

                // Outer pulsing target halo
                hazardCirclePaint.setAlpha(60);
                canvas.drawCircle(x, y, 48f, hazardCirclePaint);

                // Inner core pin
                hazardCirclePaint.setAlpha(255);
                canvas.drawCircle(x, y, 24f, hazardCirclePaint);

                // Distance estimation label
                float dist = (float) Math.abs(item.getPosZ());
                String badgeText = String.format("%.1fm", dist);
                if (item.isMitigated()) {
                    badgeText = "SAFE \u2713";
                }

                RectF tagRect = new RectF(x - 60f, y + 30f, x + 60f, y + 75f);
                canvas.drawRoundRect(tagRect, 10f, 10f, cardBgPaint);
                canvas.drawRoundRect(tagRect, 10f, 10f, cardBorderPaint);

                hazardTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(badgeText, x, y + 62f, hazardTextPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float tx = event.getX();
            float ty = event.getY();

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            for (HazardItem item : activeHazards) {
                float x = cx + (item.getPosX() * (getWidth() * 0.4f));
                float y = cy + (item.getPosY() * (getHeight() * 0.35f));

                // Hit test radius
                float dx = tx - x;
                float dy = ty - y;
                if ((dx * dx + dy * dy) <= (80 * 80)) {
                    selectedHazard = item;
                    item.setIdentified(true);
                    invalidate();
                    if (hazardClickListener != null) {
                        hazardClickListener.onHazardSelected(item);
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
        }
    }
}
