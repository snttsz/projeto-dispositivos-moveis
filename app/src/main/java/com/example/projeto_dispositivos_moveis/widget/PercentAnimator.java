package com.example.projeto_dispositivos_moveis.widget;

import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

final class PercentAnimator {

    private static final long DURATION_MS = 600L;

    private final Runnable onFrame;
    private ValueAnimator running;
    private float current;

    PercentAnimator(Runnable onFrame) {
        this.onFrame = onFrame;
    }

    float getCurrent() {
        return current;
    }

    void animateTo(float target) {
        cancel();
        running = ValueAnimator.ofFloat(current, target);
        running.setDuration(DURATION_MS);
        running.setInterpolator(new DecelerateInterpolator());
        running.addUpdateListener(animator -> updateCurrent((float) animator.getAnimatedValue()));
        running.start();
    }

    void cancel() {
        if (running != null) {
            running.cancel();
            running = null;
        }
    }

    private void updateCurrent(float value) {
        current = value;
        onFrame.run();
    }
}
