package com.example.projeto_dispositivos_moveis;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projeto_dispositivos_moveis.widget.WaterProgressView;

public class MainActivity extends AppCompatActivity {

    private static final int SAMPLE_STEP_ML = 500;
    private static final int SAMPLE_SMALL_GOAL_ML = 1000;
    private static final int SAMPLE_DEFAULT_GOAL_ML = 2000;
    private static final int PROVISIONAL_CONSUMPTION_ML = 2500;

    private View header;
    private int headerBasePaddingTop;
    private WaterProgressView waterView;
    private TextView statusView;
    private TextView metaView;
    private LinearLayout recordsList;
    private TextView emptyView;
    private int sampleConsumptionMl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bindViews();
        configureSystemBars();
        bindSampleControls();
        bindProvisionalPreview();
    }

    private void bindViews() {
        header = findViewById(R.id.cabecalho);
        headerBasePaddingTop = header.getPaddingTop();
        waterView = findViewById(R.id.copo);
        statusView = findViewById(R.id.status_consumo);
        metaView = findViewById(R.id.meta_texto);
        recordsList = findViewById(R.id.lista_registros);
        emptyView = findViewById(R.id.lista_vazia);
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

    private void bindProvisionalPreview() {
        sampleConsumptionMl = PROVISIONAL_CONSUMPTION_ML;
        waterView.setMaxValue(SAMPLE_DEFAULT_GOAL_ML);
        waterView.setConsumption(sampleConsumptionMl);
        updateMetaText();
        updateStatus();

        addRecordRow("08:10", 1000);
        addRecordRow("13:00", 800);
        addRecordRow("18:30", 700);
        emptyView.setVisibility(View.GONE);
    }

    private void addRecordRow(String time, int volumeMl) {
        View row = getLayoutInflater().inflate(R.layout.item_registro, recordsList, false);
        TextView timeView = row.findViewById(R.id.item_hora);
        TextView volumeView = row.findViewById(R.id.item_volume);
        timeView.setText(time);
        volumeView.setText(getString(R.string.registro_volume, volumeMl));
        recordsList.addView(row);
    }

    private void updateMetaText() {
        metaView.setText(getString(R.string.meta_label, waterView.getMaxValue()));
    }

    private void updateStatus() {
        int goal = waterView.getMaxValue();
        if (sampleConsumptionMl == 0) {
            statusView.setText(R.string.status_sem_consumo);
            statusView.setTextColor(ContextCompat.getColor(this, R.color.alert));
            statusView.setVisibility(View.VISIBLE);
        } else if (sampleConsumptionMl > goal) {
            statusView.setText(R.string.status_meta_ultrapassada);
            statusView.setTextColor(ContextCompat.getColor(this, R.color.water_over));
            statusView.setVisibility(View.VISIBLE);
        } else if (sampleConsumptionMl == goal) {
            statusView.setText(R.string.status_meta_batida);
            statusView.setTextColor(ContextCompat.getColor(this, R.color.success));
            statusView.setVisibility(View.VISIBLE);
        } else {
            statusView.setVisibility(View.GONE);
        }
    }

    private void changeConsumption(int deltaMl) {
        sampleConsumptionMl = Math.max(0, sampleConsumptionMl + deltaMl);
        waterView.setConsumption(sampleConsumptionMl);
        updateStatus();
    }

    private void changeGoal(int goalMl) {
        waterView.setMaxValue(goalMl);
        updateMetaText();
        updateStatus();
    }
}
