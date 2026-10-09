package com.example.projeto_dispositivos_moveis.widget;

final class WaterProgressState {

    private static final float PERCENT_FACTOR = 100f;

    private int maxValue;
    private int consumption;

    WaterProgressState(int maxValue) {
        this.maxValue = maxValue;
    }

    void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    void setConsumption(int consumption) {
        this.consumption = Math.max(0, consumption);
    }

    void setPercent(int percent) {
        setConsumption(consumptionAt(Math.max(0, percent)));
    }

    int getMaxValue() {
        return maxValue;
    }

    float getPercent() {
        return consumption * PERCENT_FACTOR / maxValue;
    }

    int consumptionAt(float percent) {
        return Math.round(percent * maxValue / PERCENT_FACTOR);
    }

    boolean isOverGoal() {
        return consumption > maxValue;
    }

    boolean isGoalReached() {
        return consumption >= maxValue;
    }
}
