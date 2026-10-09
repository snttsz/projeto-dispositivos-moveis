package com.example.projeto_dispositivos_moveis.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WaterProgressView extends View {

    private static final float DEFAULT_WIDTH_DP = 200f;
    private static final float DEFAULT_HEIGHT_DP = 240f;

    private final GlassGeometry geometry;
    private final WaterProgressPainter painter;
    private final WaterProgressState state;
    private final WaterProgressLabels labels;
    private final PercentAnimator animator;

    private boolean absoluteMode;

    public WaterProgressView(Context context) {
        this(context, null);
    }

    public WaterProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        WaterProgressAttributes attributes = WaterProgressAttributes.from(context, attrs);
        geometry = new GlassGeometry(getResources().getDisplayMetrics().density);
        painter = new WaterProgressPainter(attributes);
        state = new WaterProgressState(attributes.getMaxValue());
        labels = new WaterProgressLabels(context);
        animator = new PercentAnimator(this::invalidate);
        setOnClickListener(view -> toggleDisplayMode());
    }

    public void setMaxValue(int maxValue) {
        if (maxValue <= 0) {
            return;
        }
        state.setMaxValue(maxValue);
        refresh();
    }

    public int getMaxValue() {
        return state.getMaxValue();
    }

    public void setConsumption(int consumption) {
        state.setConsumption(consumption);
        refresh();
    }

    public void setProgress(int percent) {
        state.setPercent(percent);
        refresh();
    }

    public boolean isAbsoluteMode() {
        return absoluteMode;
    }

    private void toggleDisplayMode() {
        absoluteMode = !absoluteMode;
        invalidate();
    }

    private void refresh() {
        animator.animateTo(state.getPercent());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                resolveSize(dpToPx(DEFAULT_WIDTH_DP), widthMeasureSpec),
                resolveSize(dpToPx(DEFAULT_HEIGHT_DP), heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        geometry.update(width, height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        painter.draw(canvas, geometry, buildFrame());
    }

    @Override
    protected void onDetachedFromWindow() {
        animator.cancel();
        super.onDetachedFromWindow();
    }

    private WaterProgressFrame buildFrame() {
        float displayedPercent = animator.getCurrent();
        return new WaterProgressFrame(
                displayedPercent,
                buildLabel(displayedPercent),
                labels.overGoalAlert(),
                state.isOverGoal(),
                state.isGoalReached());
    }

    private String buildLabel(float displayedPercent) {
        if (absoluteMode) {
            return labels.absolute(state.consumptionAt(displayedPercent), state.getMaxValue());
        }
        return labels.percent(displayedPercent);
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
