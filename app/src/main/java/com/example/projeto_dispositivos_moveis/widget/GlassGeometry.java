package com.example.projeto_dispositivos_moveis.widget;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class GlassGeometry {

    private static final float MARGIN_DP = 8f;
    private static final float TEXT_DESCENT_RATIO = 0.25f;
    private static final float CAPTION_SIZE_RATIO = 0.075f;
    private static final float LABEL_SIZE_RATIO = 0.11f;
    private static final float CAPTION_GAP_RATIO = 1.3f;
    private static final float GLASS_WIDTH_PER_HEIGHT = 0.667f;
    private static final float GLASS_MAX_WIDTH_RATIO = 0.7f;
    private static final float BOTTOM_TO_TOP_WIDTH_RATIO = 0.75f;
    private static final float OUTLINE_WIDTH_RATIO = 0.035f;
    private static final float MIN_OUTLINE_DP = 2f;
    private static final float MIN_SURFACE_DP = 1.5f;
    private static final float SURFACE_TO_OUTLINE_RATIO = 0.5f;
    private static final float SPARKLE_RADIUS_RATIO = 0.045f;
    private static final float SPARKLE_MAX_OFFSET_RATIO = 0.44f;

    private static final float[] SPARKLE_X_RATIOS = {-0.69f, 0.59f, -0.72f, 0.625f, -0.66f};
    private static final float[] SPARKLE_Y_RATIOS = {0.083f, 0.208f, 0.54f, 0.625f, 0.917f};
    private static final boolean[] SPARKLE_IS_STAR = {true, true, false, true, true};

    private final float density;
    private final Path glassPath = new Path();

    private int width;
    private int height;
    private float centerX;
    private float captionSize;
    private float labelSize;
    private float labelBaseline;
    private float captionBaseline;
    private float glassTop;
    private float glassBottom;
    private float glassTopWidth;
    private float outlineWidth;
    private List<Sparkle> sparkles = Collections.emptyList();

    GlassGeometry(float density) {
        this.density = density;
    }

    void update(int width, int height) {
        this.width = width;
        this.height = height;
        this.centerX = width / 2f;
        computeTextBands();
        computeGlass();
        computeSparkles();
    }

    private void computeTextBands() {
        float margin = margin();
        captionSize = Math.min(height, width) * CAPTION_SIZE_RATIO;
        labelSize = Math.min(height, width) * LABEL_SIZE_RATIO;
        captionBaseline = height - margin - captionSize * TEXT_DESCENT_RATIO;
        labelBaseline = captionBaseline - captionSize * CAPTION_GAP_RATIO - labelSize * TEXT_DESCENT_RATIO;
    }

    private void computeGlass() {
        float margin = margin();
        float availableTop = margin * 2f;
        float availableBottom = labelBaseline - labelSize - margin;
        float availableHeight = Math.max(availableBottom - availableTop, 1f);

        glassTopWidth = Math.min(availableHeight * GLASS_WIDTH_PER_HEIGHT, width * GLASS_MAX_WIDTH_RATIO);
        outlineWidth = Math.max(MIN_OUTLINE_DP * density, glassTopWidth * OUTLINE_WIDTH_RATIO);
        glassTop = availableTop + outlineWidth / 2f;
        glassBottom = availableBottom - outlineWidth / 2f;

        buildPath(glassTopWidth, glassTopWidth * BOTTOM_TO_TOP_WIDTH_RATIO);
    }

    private void buildPath(float topWidth, float bottomWidth) {
        glassPath.reset();
        glassPath.moveTo(centerX - topWidth / 2f, glassTop);
        glassPath.lineTo(centerX + topWidth / 2f, glassTop);
        glassPath.lineTo(centerX + bottomWidth / 2f, glassBottom);
        glassPath.lineTo(centerX - bottomWidth / 2f, glassBottom);
        glassPath.close();
    }

    private void computeSparkles() {
        float radius = Math.min(width, height) * SPARKLE_RADIUS_RATIO;
        float maxOffset = width * SPARKLE_MAX_OFFSET_RATIO;
        List<Sparkle> result = new ArrayList<>();
        for (int i = 0; i < SPARKLE_X_RATIOS.length; i++) {
            float offset = clamp(SPARKLE_X_RATIOS[i] * glassTopWidth, -maxOffset, maxOffset);
            float y = glassTop + SPARKLE_Y_RATIOS[i] * (glassBottom - glassTop);
            result.add(new Sparkle(centerX + offset, y, radius, SPARKLE_IS_STAR[i]));
        }
        sparkles = Collections.unmodifiableList(result);
    }

    private float margin() {
        return MARGIN_DP * density;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    float waterLevelY(float fraction) {
        return glassBottom - (glassBottom - glassTop) * fraction;
    }

    Path getGlassPath() {
        return glassPath;
    }

    List<Sparkle> getSparkles() {
        return sparkles;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    float getCenterX() {
        return centerX;
    }

    float getCaptionSize() {
        return captionSize;
    }

    float getLabelSize() {
        return labelSize;
    }

    float getLabelBaseline() {
        return labelBaseline;
    }

    float getCaptionBaseline() {
        return captionBaseline;
    }

    float getOutlineWidth() {
        return outlineWidth;
    }

    float getSurfaceWidth() {
        return Math.max(MIN_SURFACE_DP * density, outlineWidth * SURFACE_TO_OUTLINE_RATIO);
    }
}
