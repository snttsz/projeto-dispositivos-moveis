package com.example.projeto_dispositivos_moveis.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.projeto_dispositivos_moveis.R;

public class WaterProgressView extends View {

    private static final int DEFAULT_MAX_VALUE = 2000;
    private static final long ANIMATION_DURATION_MS = 600;

    private int maxValue = DEFAULT_MAX_VALUE;
    private int consumption;
    private float displayedConsumption;
    private int progressColor;
    private int overColor;
    private int textColor;
    private int outlineColor;
    private String title;
    private boolean absoluteMode;
    private ValueAnimator progressAnimator;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WaterProgressView(Context context) {
        this(context, null);
    }

    public WaterProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                absoluteMode = !absoluteMode;
                invalidate();
            }
        });
    }

    private void readAttributes(Context context, @Nullable AttributeSet attrs) {
        progressColor = ContextCompat.getColor(context, R.color.water);
        overColor = ContextCompat.getColor(context, R.color.water_over);
        textColor = ContextCompat.getColor(context, R.color.primary);
        outlineColor = ContextCompat.getColor(context, R.color.foreground);
        title = context.getString(R.string.titulo_consumo);

        if (attrs == null) {
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WaterProgressView);
        int configuredMaxValue = array.getInt(
                R.styleable.WaterProgressView_maxValue, DEFAULT_MAX_VALUE);
        if (configuredMaxValue > 0) {
            maxValue = configuredMaxValue;
        }
        progressColor = array.getColor(R.styleable.WaterProgressView_progressColor, progressColor);
        overColor = array.getColor(R.styleable.WaterProgressView_overColor, overColor);
        textColor = array.getColor(R.styleable.WaterProgressView_textColor, textColor);
        outlineColor = array.getColor(R.styleable.WaterProgressView_outlineColor, outlineColor);
        String configuredTitle = array.getString(R.styleable.WaterProgressView_title);
        if (configuredTitle != null) {
            title = configuredTitle;
        }
        array.recycle();
    }

    public void setMaxValue(int value) {
        if (value > 0) {
            maxValue = value;
            invalidate();
        }
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setConsumption(int value) {
        consumption = Math.max(0, value);
        animateProgress();
    }

    public void setProgress(int percent) {
        setConsumption(Math.max(0, percent) * maxValue / 100);
    }

    public boolean isAbsoluteMode() {
        return absoluteMode;
    }

    public void setModoAbsoluto(boolean absolute) {
        absoluteMode = absolute;
        invalidate();
    }

    public boolean isAcimaDaMeta() {
        return consumption > maxValue;
    }

    private void animateProgress() {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }

        progressAnimator = ValueAnimator.ofFloat(displayedConsumption, consumption);
        progressAnimator.setDuration(ANIMATION_DURATION_MS);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                displayedConsumption = (float) animator.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float left = width * 0.25f;
        float right = width * 0.75f;
        float top = height * 0.22f;
        float bottom = height * 0.78f;

        drawTitle(canvas, width, height);
        drawWater(canvas, left, right, top, bottom, width);
        drawGlass(canvas, left, right, top, bottom, width);
        drawStars(canvas, width, height);
        drawValue(canvas, width, height);
    }

    private void drawTitle(Canvas canvas, float width, float height) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(height * 0.07f);
        paint.setFakeBoldText(true);
        canvas.drawText(title, width / 2f, height * 0.11f, paint);
    }

    private void drawWater(Canvas canvas, float left, float right, float top,
                           float bottom, float width) {
        float percent = Math.min(100f, displayedConsumption * 100f / maxValue);
        float waterTop = bottom - (bottom - top) * percent / 100f;
        float bottomMargin = width * 0.05f;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(isAcimaDaMeta() ? overColor : progressColor);

        for (float y = waterTop; y <= bottom; y += 2f) {
            float position = (y - top) / (bottom - top);
            float sideMargin = bottomMargin * position;
            canvas.drawLine(left + sideMargin, y, right - sideMargin, y, paint);
        }

        if (percent > 0f && percent < 100f) {
            float surfaceMargin = bottomMargin * ((waterTop - top) / (bottom - top));
            paint.setStrokeWidth(4f);
            canvas.drawLine(left + surfaceMargin, waterTop,
                    right - surfaceMargin, waterTop, paint);
        }
    }

    private void drawGlass(Canvas canvas, float left, float right, float top,
                           float bottom, float width) {
        float bottomMargin = width * 0.05f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6f);
        paint.setColor(outlineColor);
        canvas.drawLine(left, top, right, top, paint);
        canvas.drawLine(left, top, left + bottomMargin, bottom, paint);
        canvas.drawLine(right, top, right - bottomMargin, bottom, paint);
        canvas.drawLine(left + bottomMargin, bottom, right - bottomMargin, bottom, paint);
    }

    private void drawStars(Canvas canvas, float width, float height) {
        if (displayedConsumption < maxValue) {
            return;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(isAcimaDaMeta() ? overColor : progressColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(height * 0.07f);
        paint.setFakeBoldText(false);
        canvas.drawText("★", width * 0.16f, height * 0.36f, paint);
        canvas.drawText("★", width * 0.84f, height * 0.42f, paint);
        canvas.drawText("★", width * 0.18f, height * 0.65f, paint);
        canvas.drawText("★", width * 0.82f, height * 0.69f, paint);
    }

    private void drawValue(Canvas canvas, float width, float height) {
        int visibleConsumption = Math.round(displayedConsumption);
        int percent = Math.round(displayedConsumption * 100f / maxValue);
        String value;
        if (absoluteMode) {
            value = getContext().getString(
                    R.string.absoluto_meta, visibleConsumption, maxValue);
        } else {
            value = getContext().getString(R.string.percentual_meta, percent);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(isAcimaDaMeta() ? overColor : textColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(height * 0.08f);
        paint.setFakeBoldText(true);
        canvas.drawText(value, width / 2f, height * 0.91f, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }
}
