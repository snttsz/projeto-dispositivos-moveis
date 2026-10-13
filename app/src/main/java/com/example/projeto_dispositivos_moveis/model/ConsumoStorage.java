package com.example.projeto_dispositivos_moveis.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsumoStorage { //gerencia leitura e escrita com o sharedPreferences

    private static final String FILE_NAME = "ConsumoAgua";
    private static final String KEY_GOAL = "meta_ml";
    private static final String KEY_DAY = "data_dia";
    private static final String KEY_RECORDS = "registros";
    private static final String KEY_ACCUMULATED = "acumulado_ml";

    private static final int DEFAULT_GOAL_ML = 2000;
    private static final String RECORD_SEPARATOR = ";";
    private static final String FIELD_SEPARATOR = ":";

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    public ConsumoStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public int getGoal() {
        return sharedPreferences.getInt(KEY_GOAL, DEFAULT_GOAL_ML);
    }

    public void setGoal(int goalMl) {
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(KEY_GOAL, goalMl);
        sharedPreferencesEditor.apply();
    }

    public List<RegistroConsumo> getRecords() {
        return deserialize(sharedPreferences.getString(KEY_RECORDS, ""));
    }

    public void addRecord(int volumeMl, long instantMillis) {
        List<RegistroConsumo> records = new ArrayList<>(getRecords());
        records.add(new RegistroConsumo(instantMillis, volumeMl));

        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(KEY_RECORDS, serialize(records));
        sharedPreferencesEditor.putInt(KEY_ACCUMULATED, sum(records));
        sharedPreferencesEditor.apply();
    }

    public int getAccumulated() {
        int accumulated = sum(getRecords());
        if (accumulated != sharedPreferences.getInt(KEY_ACCUMULATED, 0)) {
            sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putInt(KEY_ACCUMULATED, accumulated);
            sharedPreferencesEditor.apply();
        }
        return accumulated;
    }

    public void checkNewDay() {
        String today = LocalDate.now().toString();
        if (today.equals(sharedPreferences.getString(KEY_DAY, ""))) {
            return;
        }

        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.remove(KEY_RECORDS);
        sharedPreferencesEditor.putInt(KEY_ACCUMULATED, 0);
        sharedPreferencesEditor.putString(KEY_DAY, today);
        sharedPreferencesEditor.apply();
    }

    private int sum(List<RegistroConsumo> records) {
        int total = 0;
        for (RegistroConsumo record : records) {
            total += record.getVolumeMl();
        }
        return total;
    }

    private String serialize(List<RegistroConsumo> records) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < records.size(); i++) {
            RegistroConsumo record = records.get(i);
            if (i > 0) {
                builder.append(RECORD_SEPARATOR);
            }
            builder.append(record.getInstantMillis())
                    .append(FIELD_SEPARATOR)
                    .append(record.getVolumeMl());
        }
        return builder.toString();
    }

    private List<RegistroConsumo> deserialize(String serialized) {
        List<RegistroConsumo> records = new ArrayList<>();
        if (serialized == null || serialized.trim().isEmpty()) {
            return records;
        }

        String[] tokens = serialized.split(RECORD_SEPARATOR);
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            String[] fields = token.split(FIELD_SEPARATOR);
            if (fields.length != 2) {
                continue;
            }
            try {
                long instantMillis = Long.parseLong(fields[0]);
                int volumeMl = Integer.parseInt(fields[1]);
                if (volumeMl > 0) {
                    records.add(new RegistroConsumo(instantMillis, volumeMl));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return records;
    }
}
