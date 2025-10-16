package GUI.canciones.Cancion1;

import GUI.Nota;
import config.CancionBase;
import config.ResolucionManager;

public class Cancion1 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private Thread hiloCreador;

    public Cancion1(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
        // 1) Audio
        config.Sonido.detenerMusica();
        config.Sonido.reproducirCancion("GUI/canciones/Cancion1/my-8-bit-hero-301280.mp3");

        // 2) Mapa
        java.util.List<EventoNota> mapaDeNotas = cargarMapaDesdeJson(
            "/GUI/canciones/Cancion1/my-8-bit-hero-301280.json"
        );
        if (mapaDeNotas == null || mapaDeNotas.isEmpty()) {
            System.err.println("No se pudieron cargar notas del JSON.");
            iniciarMovimientoNotas();
            return;
        }
        mapaDeNotas.sort(java.util.Comparator.comparingLong(n -> n.tiempo));

        // 3) Parámetros de caída
        final int tickMs = 10; // igual al Timer de CancionBase
        final int velocidadPxPorTick = resolucion.escalarY(5);
        final double velocidadPxPorMs = velocidadPxPorTick / (double) tickMs;

        // 4) Geometría vertical
        final int yImpacto = notaD.getY();                // línea de hit
        final int yAparicion = -resolucion.escalarY(200); // nace fuera de pantalla
        final int distanciaPx = Math.max(1, yImpacto - yAparicion);
        final long anticipacionMs = Math.round(distanciaPx / velocidadPxPorMs);

        // Ajuste fino (si lo necesitás, calibrable)
        final long offsetMs = -100;

        // 5) Arrancar movimiento
        iniciarMovimientoNotas();

        // 6) Spawner
        final long tiempoInicioNs = System.nanoTime();
        hiloCreador = new Thread(() -> {
            try {
                for (EventoNota ev : mapaDeNotas) {
                    int columna = Math.max(0, Math.min(3, ev.columna));
                    long momentoAparicionMs = Math.max(0, ev.tiempo - anticipacionMs + offsetMs);

                    long ahoraMs = (System.nanoTime() - tiempoInicioNs) / 1_000_000L;
                    long esperarMs = momentoAparicionMs - ahoraMs;
                    if (esperarMs > 0) Thread.sleep(esperarMs);

                    // ancho provisional para calcular X centrada
                    int ladoTemporal = resolucion.escalarUniformeMin(157/2, 80);
                    int xColumna = getColumnXForWidth(columna, ladoTemporal);

                    final Nota nota;
                    if ("hold".equalsIgnoreCase(ev.tipo)) {
                        nota = new Nota(
                            xColumna,
                            yAparicion,
                            columna,
                            true,               // esHold
                            ev.duracion,        // ms
                            velocidadPxPorMs,
                            resolucion
                        );
                    } else {
                        nota = new Nota(
                            xColumna,
                            yAparicion,
                            columna,
                            false,              // esHold
                            0L,
                            velocidadPxPorMs,
                            resolucion
                        );
                    }

                    javax.swing.SwingUtilities.invokeLater(() -> agregarNota(nota));
                }
            } catch (InterruptedException ignored) {
            }
        }, "Creador-Notas-Cancion1");
        hiloCreador.setDaemon(true);
        hiloCreador.start();
    }

    @Override
    protected void finalizarCancion() {
        if (hiloCreador != null && hiloCreador.isAlive()) {
            hiloCreador.interrupt();
            hiloCreador = null;
        }
        config.Sonido.detenerCancion();
        super.finalizarCancion();
    }

    /** Carga y parsea el JSON del mapa de notas. */
    private java.util.List<EventoNota> cargarMapaDesdeJson(String rutaEnClasspath) {
        try (java.io.InputStream is = getClass().getResourceAsStream(rutaEnClasspath);
             java.io.InputStreamReader lector = new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)) {

            com.google.gson.JsonObject raiz = com.google.gson.JsonParser.parseReader(lector).getAsJsonObject();
            com.google.gson.Gson gson = new com.google.gson.Gson();
            com.google.gson.reflect.TypeToken<java.util.List<EventoNota>> tipoLista =
                    new com.google.gson.reflect.TypeToken<java.util.List<EventoNota>>() {};
            return gson.fromJson(raiz.getAsJsonArray("notas"), tipoLista.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    /** DTO del JSON. */
    private static class EventoNota {
        String tipo;     // "tap" o "hold"
        int columna;     // 0..3
        long tiempo;     // ms desde inicio
        long duracion;   // ms (solo hold)
    }
}
