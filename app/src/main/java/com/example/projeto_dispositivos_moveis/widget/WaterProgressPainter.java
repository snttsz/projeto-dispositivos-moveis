package com.example.projeto_dispositivos_moveis.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

final class WaterProgressPainter {

    private static final float FULL_PERCENT = 100f;
    private static final float MAX_TEXT_WIDTH_RATIO = 0.95f;
    private static final int STAR_POINTS = 8;
    private static final float STAR_INNER_RADIUS_RATIO = 0.35f;

    private final WaterProgressAttributes attributes;
    private final Path starPath = new Path();
    private final Paint waterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint surfacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sparklePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint alertPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    WaterProgressPainter(WaterProgressAttributes attributes) {
        this.attributes = attributes;
        configureFillPaint(waterPaint);
        configureFillPaint(sparklePaint);
        configureSurfacePaint();
        configureOutlinePaint();
        configureTextPaint(titlePaint);
        configureTextPaint(labelPaint);
        configureTextPaint(alertPaint);
    }

    private void configureFillPaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
    }

    private void configureSurfacePaint() {
        surfacePaint.setStyle(Paint.Style.STROKE);
        surfacePaint.setColor(attributes.getTextColor());
    }

    private void configureOutlinePaint() {
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeJoin(Paint.Join.ROUND);
        outlinePaint.setColor(attributes.getTextColor());
    }

    private void configureTextPaint(Paint paint) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    void draw(Canvas canvas, GlassGeometry geometry, WaterProgressFrame frame) {
        int waterColor = waterColor(frame);
        drawTitle(canvas, geometry);
        drawWater(canvas, geometry, frame, waterColor);
        drawOutline(canvas, geometry);
        drawSparkles(canvas, geometry, frame, waterColor);
        drawLabel(canvas, geometry, frame);
        drawAlert(canvas, geometry, frame);
    }

    private int waterColor(WaterProgressFrame frame) {
        return frame.isOverGoal() ? attributes.getOverColor() : attributes.getProgressColor();
    }

    private void drawTitle(Canvas canvas, GlassGeometry geometry) {
        titlePaint.setColor(attributes.getTextColor());
        drawCenteredText(canvas, geometry, titlePaint, attributes.getTitle(),
                geometry.getTitleSize(), geometry.getTitleBaseline());
    }

    private void drawWater(Canvas canvas, GlassGeometry geometry, WaterProgressFrame frame, int color) {
        float fraction = Math.min(frame.getDisplayedPercent(), FULL_PERCENT) / FULL_PERCENT;
        if (fraction <= 0f) {
            return;
        }
        float levelY = geometry.waterLevelY(fraction);
        waterPaint.setColor(color);
        surfacePaint.setStrokeWidth(geometry.getSurfaceWidth());

        canvas.save();
        canvas.clipPath(geometry.getGlassPath());
        canvas.drawRect(0, levelY, geometry.getWidth(), geometry.getHeight(), waterPaint);
        if (fraction < 1f) {
            canvas.drawLine(0, levelY, geometry.getWidth(), levelY, surfacePaint);
        }
        canvas.restore();
    }

    private void drawOutline(Canvas canvas, GlassGeometry geometry) {
        outlinePaint.setStrokeWidth(geometry.getOutlineWidth());
        canvas.drawPath(geometry.getGlassPath(), outlinePaint);
    }

    private void drawSparkles(Canvas canvas, GlassGeometry geometry, WaterProgressFrame frame, int color) {
        if (!frame.isGoalReached()) {
            return;
        }
        sparklePaint.setColor(color);
        for (Sparkle sparkle : geometry.getSparkles()) {
            drawSparkle(canvas, sparkle);
        }
    }

    private void drawSparkle(Canvas canvas, Sparkle sparkle) {
        if (sparkle.isStar()) {
            drawStar(canvas, sparkle);
        } else {
            canvas.drawCircle(sparkle.getX(), sparkle.getY(), sparkle.getRadius() * 0.6f, sparklePaint);
        }
    }

    private void drawStar(Canvas canvas, Sparkle sparkle) {
        starPath.reset();
        for (int i = 0; i < STAR_POINTS; i++) {
            float radius = i % 2 == 0 ? sparkle.getRadius() : sparkle.getRadius() * STAR_INNER_RADIUS_RATIO;
            double angle = Math.PI / 4 * i - Math.PI / 2;
            float x = sparkle.getX() + (float) Math.cos(angle) * radius;
            float y = sparkle.getY() + (float) Math.sin(angle) * radius;
            if (i == 0) {
                starPath.moveTo(x, y);
            } else {
                starPath.lineTo(x, y);
            }
        }
        starPath.close();
        canvas.drawPath(starPath, sparklePaint);
    }

    private void drawLabel(Canvas canvas, GlassGeometry geometry, WaterProgressFrame frame) {
        labelPaint.setColor(frame.isOverGoal() ? attributes.getOverColor() : attributes.getTextColor());
        drawCenteredText(canvas, geometry, labelPaint, frame.getLabel(),
                geometry.getLabelSize(), geometry.getLabelBaseline());
    }

    private void drawAlert(Canvas canvas, GlassGeometry geometry, WaterProgressFrame frame) {
        if (!frame.isOverGoal()) {
            return;
        }
        alertPaint.setColor(attributes.getOverColor());
        drawCenteredText(canvas, geometry, alertPaint, frame.getAlert(),
                geometry.getTitleSize(), geometry.getCaptionBaseline());
    }

    private void drawCenteredText(Canvas canvas, GlassGeometry geometry, Paint paint,
                                  String text, float maxSize, float baseline) {
        fitText(paint, text, geometry.getWidth() * MAX_TEXT_WIDTH_RATIO, maxSize);
        canvas.drawText(text, geometry.getCenterX(), baseline, paint);
    }

    private void fitText(Paint paint, String text, float maxWidth, float maxSize) {
        paint.setTextSize(maxSize);
        float textWidth = paint.measureText(text);
        if (textWidth > maxWidth) {
            paint.setTextSize(maxSize * maxWidth / textWidth);
        }
    }
}
