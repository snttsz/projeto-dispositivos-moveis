package com.example.projeto_dispositivos_moveis.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.projeto_dispositivos_moveis.R;

final class WaterProgressAttributes {

    static final int DEFAULT_MAX_VALUE = 2000;

    private final int maxValue;
    private final int progressColor;
    private final int overColor;
    private final int textColor;
    private final int outlineColor;

    private WaterProgressAttributes(int maxValue, int progressColor, int overColor,
                                    int textColor, int outlineColor) {
        this.maxValue = maxValue > 0 ? maxValue : DEFAULT_MAX_VALUE;
        this.progressColor = progressColor;
        this.overColor = overColor;
        this.textColor = textColor;
        this.outlineColor = outlineColor;
    }

    static WaterProgressAttributes from(Context context, @Nullable AttributeSet attrs) {
        int progressColor = ContextCompat.getColor(context, R.color.water);
        int overColor = ContextCompat.getColor(context, R.color.water_over);
        int textColor = ContextCompat.getColor(context, R.color.primary);
        int outlineColor = ContextCompat.getColor(context, R.color.foreground);

        if (attrs == null) {
            return new WaterProgressAttributes(DEFAULT_MAX_VALUE, progressColor, overColor,
                    textColor, outlineColor);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WaterProgressView, 0, 0);
        try {
            return new WaterProgressAttributes(
                    array.getInt(R.styleable.WaterProgressView_maxValue, DEFAULT_MAX_VALUE),
                    array.getColor(R.styleable.WaterProgressView_progressColor, progressColor),
                    array.getColor(R.styleable.WaterProgressView_overColor, overColor),
                    array.getColor(R.styleable.WaterProgressView_textColor, textColor),
                    array.getColor(R.styleable.WaterProgressView_outlineColor, outlineColor));
        } finally {
            array.recycle();
        }
    }

    int getMaxValue() {
        return maxValue;
    }

    int getProgressColor() {
        return progressColor;
    }

    int getOverColor() {
        return overColor;
    }

    int getTextColor() {
        return textColor;
    }

    int getOutlineColor() {
        return outlineColor;
    }
}
