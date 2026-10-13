package com.example.projeto_dispositivos_moveis;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projeto_dispositivos_moveis.model.ConsumoStorage;
import com.example.projeto_dispositivos_moveis.model.RegistroConsumo;
import com.example.projeto_dispositivos_moveis.widget.WaterProgressView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_VOLUME_ML = 5000;
    private static final int MAX_GOAL_ML = 20000;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private View header;
    private int headerBasePaddingTop;
    private WaterProgressView waterView;
    private TextView statusView;
    private TextView metaView;
    private LinearLayout recordsList;
    private TextView emptyView;
    private LinearLayout metaReadGroup;
    private LinearLayout metaEditGroup;
    private TextView alterGoalButton;
    private EditText goalField;
    private Button saveGoalButton;
    private EditText volumeField;
    private Button registerButton;

    private ConsumoStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bindViews();
        configureSystemBars();
        storage = new ConsumoStorage(this);
        bindActions();
        storage.checkNewDay();
        atualizarTela();
    }

    @Override
    protected void onResume() {
        super.onResume();
        storage.checkNewDay();
        atualizarTela();
    }

    private void bindViews() {
        header = findViewById(R.id.cabecalho);
        headerBasePaddingTop = header.getPaddingTop();
        waterView = findViewById(R.id.copo);
        statusView = findViewById(R.id.status_consumo);
        metaView = findViewById(R.id.meta_texto);
        recordsList = findViewById(R.id.lista_registros);
        emptyView = findViewById(R.id.lista_vazia);
        metaReadGroup = findViewById(R.id.meta_leitura);
        metaEditGroup = findViewById(R.id.meta_edicao);
        alterGoalButton = findViewById(R.id.botao_alterar_meta);
        goalField = findViewById(R.id.campo_meta);
        saveGoalButton = findViewById(R.id.botao_salvar_meta);
        volumeField = findViewById(R.id.campo_quantidade);
        registerButton = findViewById(R.id.botao_registrar);
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

    private void bindActions() {
        alterGoalButton.setOnClickListener(view -> entrarModoEdicaoMeta());
        saveGoalButton.setOnClickListener(view -> salvarMeta());
        registerButton.setOnClickListener(view -> registrarConsumo());
    }

    private void atualizarTela() {
        int goal = storage.getGoal();
        int accumulated = storage.getAccumulated();
        List<RegistroConsumo> records = storage.getRecords();

        waterView.setMaxValue(goal);
        waterView.setConsumption(accumulated);
        metaView.setText(getString(R.string.meta_label, goal));
        sairModoEdicaoMeta();
        atualizarStatus(accumulated, goal);
        renderRecords(records);
    }

    private void atualizarStatus(int accumulated, int goal) {
        if (accumulated == 0) {
            statusView.setText(R.string.status_sem_consumo);
            statusView.setTextColor(ContextCompat.getColor(this, R.color.alert));
            statusView.setVisibility(View.VISIBLE);
        } else if (accumulated > goal) {
            statusView.setText(R.string.status_meta_ultrapassada);
            statusView.setTextColor(ContextCompat.getColor(this, R.color.water_over));
            statusView.setVisibility(View.VISIBLE);
        } else if (accumulated == goal) {
            statusView.setText(R.string.status_meta_batida);
            statusView.setTextColor(ContextCompat.getColor(this, R.color.success));
            statusView.setVisibility(View.VISIBLE);
        } else {
            statusView.setVisibility(View.GONE);
        }
    }

    private void renderRecords(List<RegistroConsumo> records) {
        recordsList.removeAllViews();
        if (records.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);
        for (RegistroConsumo record : records) {
            addRecordRow(record);
        }
    }

    private void addRecordRow(RegistroConsumo record) {
        View row = getLayoutInflater().inflate(R.layout.item_registro, recordsList, false);
        TextView timeView = row.findViewById(R.id.item_hora);
        TextView volumeView = row.findViewById(R.id.item_volume);
        timeView.setText(formatTime(record.getInstantMillis()));
        volumeView.setText(getString(R.string.registro_volume, record.getVolumeMl()));
        recordsList.addView(row);
    }

    private String formatTime(long instantMillis) {
        return Instant.ofEpochMilli(instantMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .format(TIME_FORMAT);
    }

    private void registrarConsumo() {
        Integer volume = parsePositiveValue(volumeField);
        if (volume == null) {
            showToast(getString(R.string.erro_volume_invalido));
            return;
        }
        if (volume > MAX_VOLUME_ML) {
            showToast(getString(R.string.erro_volume_maximo, MAX_VOLUME_ML));
            return;
        }

        storage.addRecord(volume, System.currentTimeMillis());
        volumeField.setText("");
        atualizarTela();
    }

    private void salvarMeta() {
        Integer goal = parsePositiveValue(goalField);
        if (goal == null) {
            showToast(getString(R.string.erro_meta_invalida));
            return;
        }
        if (goal > MAX_GOAL_ML) {
            showToast(getString(R.string.erro_meta_maxima, MAX_GOAL_ML));
            return;
        }

        storage.setGoal(goal);
        sairModoEdicaoMeta();
        atualizarTela();
    }

    private Integer parsePositiveValue(EditText field) {
        String text = field.getText().toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            int value = Integer.parseInt(text);
            return value > 0 ? value : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void entrarModoEdicaoMeta() {
        goalField.setText(String.valueOf(storage.getGoal()));
        metaReadGroup.setVisibility(View.GONE);
        metaEditGroup.setVisibility(View.VISIBLE);
        goalField.requestFocus();
    }

    private void sairModoEdicaoMeta() {
        metaEditGroup.setVisibility(View.GONE);
        metaReadGroup.setVisibility(View.VISIBLE);
    }
}
