package config;

public class TemporizadorCancion {
    private long tiempoInicioNs = 0;
    private long tiempoReproducidoMs = 0;
    private boolean enPausa = false;

    public void iniciar() {
        tiempoInicioNs = System.nanoTime();
        tiempoReproducidoMs = 0;
        enPausa = false;
    }

    public void pausar() {
        if (!enPausa) {
            tiempoReproducidoMs += (System.nanoTime() - tiempoInicioNs) / 1_000_000L;
            enPausa = true;
        }
    }

    public void reanudar() {
        if (enPausa) {
            tiempoInicioNs = System.nanoTime();
            enPausa = false;
        }
    }

    public long getTiempoActualMs() {
        if (enPausa) return tiempoReproducidoMs;
        return tiempoReproducidoMs + (System.nanoTime() - tiempoInicioNs) / 1_000_000L;
    }

    public long getOffsetMs() {
        return getTiempoActualMs();
    }
}
