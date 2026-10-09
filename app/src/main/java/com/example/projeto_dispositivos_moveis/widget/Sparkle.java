package com.example.projeto_dispositivos_moveis.widget;

final class Sparkle {

    private final float x;
    private final float y;
    private final float radius;
    private final boolean star;

    Sparkle(float x, float y, float radius, boolean star) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.star = star;
    }

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    float getRadius() {
        return radius;
    }

    boolean isStar() {
        return star;
    }
}
