package com.example.projeto_dispositivos_moveis;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projeto_dispositivos_moveis.widget.WaterProgressView;

public class MainActivity extends AppCompatActivity {

    private static final int SAMPLE_STEP_ML = 500;
    private static final int SAMPLE_SMALL_GOAL_ML = 1000;
    private static final int SAMPLE_DEFAULT_GOAL_ML = 2000;

    private View header;
    private int headerBasePaddingTop;
    private WaterProgressView waterView;
    private int sampleConsumptionMl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bindViews();
        configureSystemBars();
        bindSampleControls();
    }

    private void bindViews() {
        header = findViewById(R.id.cabecalho);
        headerBasePaddingTop = header.getPaddingTop();
        waterView = findViewById(R.id.copo);
    }

    private void configureSystemBars() {
        View root = findViewById(R.id.main);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(false);
        ViewCompat.setOnApplyWindowInsetsListener(root, this::applySystemBarInsets);
    }

    private WindowInsetsCompat applySystemBarInsets(View root, WindowInsetsCompat windowInsets) {
        Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        header.setPadding(header.getPaddingLeft(), headerBasePaddingTop + bars.top,
                header.getPaddingRight(), header.getPaddingBottom());
        root.setPadding(bars.left, 0, bars.right, bars.bottom);
        return WindowInsetsCompat.CONSUMED;
    }

    private void bindSampleControls() {
        findViewById(R.id.botao_menos).setOnClickListener(view -> changeConsumption(-SAMPLE_STEP_ML));
        findViewById(R.id.botao_mais).setOnClickListener(view -> changeConsumption(SAMPLE_STEP_ML));
        findViewById(R.id.botao_meta_pequena).setOnClickListener(view -> changeGoal(SAMPLE_SMALL_GOAL_ML));
        findViewById(R.id.botao_meta_padrao).setOnClickListener(view -> changeGoal(SAMPLE_DEFAULT_GOAL_ML));
    }

    private void changeConsumption(int deltaMl) {
        sampleConsumptionMl = Math.max(0, sampleConsumptionMl + deltaMl);
        waterView.setConsumption(sampleConsumptionMl);
    }

    private void changeGoal(int goalMl) {
        waterView.setMaxValue(goalMl);
    }
}
