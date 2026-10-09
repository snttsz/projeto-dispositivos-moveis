package com.example.projeto_dispositivos_moveis.widget;

import android.content.Context;

import com.example.projeto_dispositivos_moveis.R;

final class WaterProgressLabels {

    private final Context context;

    WaterProgressLabels(Context context) {
        this.context = context;
    }

    String percent(float percent) {
        return context.getString(R.string.percentual_meta, Math.round(percent));
    }

    String absolute(int consumption, int maxValue) {
        return context.getString(R.string.absoluto_meta, consumption, maxValue);
    }

    String overGoalAlert() {
        return context.getString(R.string.status_meta_ultrapassada);
    }
}
