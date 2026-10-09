package com.example.projeto_dispositivos_moveis.widget;

final class WaterProgressFrame {

    private final float displayedPercent;
    private final String label;
    private final String alert;
    private final boolean overGoal;
    private final boolean goalReached;

    WaterProgressFrame(float displayedPercent, String label, String alert,
                       boolean overGoal, boolean goalReached) {
        this.displayedPercent = displayedPercent;
        this.label = label;
        this.alert = alert;
        this.overGoal = overGoal;
        this.goalReached = goalReached;
    }

    float getDisplayedPercent() {
        return displayedPercent;
    }

    String getLabel() {
        return label;
    }

    String getAlert() {
        return alert;
    }

    boolean isOverGoal() {
        return overGoal;
    }

    boolean isGoalReached() {
        return goalReached;
    }
}
