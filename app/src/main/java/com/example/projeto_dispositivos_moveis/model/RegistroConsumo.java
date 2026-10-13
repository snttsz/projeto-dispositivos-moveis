package com.example.projeto_dispositivos_moveis.model;

public class RegistroConsumo { //modelo de registro de consumo de agua (volume bebido)

    private final long instantMillis; //horario que o consumo foi registrado
    private final int volumeMl;

    public RegistroConsumo(long instantMillis, int volumeMl) {
        this.instantMillis = instantMillis;
        this.volumeMl = volumeMl;
    }

    public long getInstantMillis() {
        return instantMillis;
    }

    public int getVolumeMl() {
        return volumeMl;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RegistroConsumo)) {
            return false;
        }
        RegistroConsumo that = (RegistroConsumo) other;
        return instantMillis == that.instantMillis && volumeMl == that.volumeMl;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(instantMillis) + Integer.hashCode(volumeMl);
    }
}
