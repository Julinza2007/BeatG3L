package GUI.canciones.Cancion1;

import GUI.Nota;
import config.CancionBase;
import config.ResolucionManager;

public class Cancion1 extends CancionBase {

    private static final long serialVersionUID = 1L;
    public Thread hiloCreador;
    
    private volatile boolean enPausa = false;
    private long tiempoPausaAcumuladoMs = 0; 
    private long tiempoInicioNs;
    private long tiempoInicioPausaNs = 0;

    public Cancion1(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
        // 1) Audio
        config.Sonido.detenerMusica();
        config.Sonido.reproducirCancion("GUI/canciones/Cancion1/my-8-bit-hero-301280.mp3", 0);

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
        this.tiempoUltimaNotaMs = mapaDeNotas.get(mapaDeNotas.size() - 1).tiempo;

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
        final long offsetMs = 10;

        // 5) Arrancar movimiento
        iniciarMovimientoNotas();

        // 6) Spawner
        final long tiempoInicioNs = System.nanoTime();
        hiloCreador = new Thread(() -> {
            try {
            	
            	javax.swing.SwingUtilities.invokeLater(() -> notasGeneradas = false);

            	
                for (EventoNota ev : mapaDeNotas) {
                    int columna = Math.max(0, Math.min(3, ev.columna));
                    long momentoAparicionMs = Math.max(0, ev.tiempo - anticipacionMs + offsetMs);
                    
                    long ahoraMs;
                    long esperarMs;
                    
                    
                    while (true) {
                        ahoraMs = (System.nanoTime() - tiempoInicioNs - tiempoPausaAcumuladoMs * 1_000_000L) / 1_000_000L;
                        esperarMs = momentoAparicionMs - ahoraMs;

                        if (enPausa) {
                            tiempoInicioPausaNs = System.nanoTime();
                            while (enPausa) Thread.sleep(10);
                            tiempoPausaAcumuladoMs += (System.nanoTime() - tiempoInicioPausaNs) / 1_000_000L;
                        }

                        if (esperarMs <= 0) break;
                        Thread.sleep(1);
                    }
                    /*
                    while ((System.nanoTime() - tiempoInicioNs) / 1_000_000L < momentoAparicionMs) {
                        Thread.sleep(1); // espera activa mínima
                    }
                    */
                    // ancho provisional para calcular X centrada
                    int ladoTemporal = resolucion.escalarUniformeMin(157/2, 80);
                    int xColumna = getColumnXForWidth(columna, ladoTemporal);


                 // 2. Filtro visual antes de crear la nota
                 boolean conflictoVisual = false;
                 if ("tap".equalsIgnoreCase(ev.tipo)) {
                     ahoraMs = (System.nanoTime() - tiempoInicioNs) / 1_000_000L;
                     long tiempoRestanteMs = ev.tiempo - ahoraMs;
                     int yTap = yImpacto - (int)(tiempoRestanteMs * velocidadPxPorMs);

                     for (EventoNota otra : mapaDeNotas) {
                         if ("hold".equalsIgnoreCase(otra.tipo) && otra.columna == ev.columna) {
                             long tiempoRestanteHoldMs = otra.tiempo - ahoraMs;
                             int yHoldInicio = yImpacto - (int)(tiempoRestanteHoldMs * velocidadPxPorMs);
                             int alturaHoldPx = (int)(otra.duracion * velocidadPxPorMs);
                             int yHoldFin = yHoldInicio + alturaHoldPx;

                             if (yTap >= yHoldInicio && yTap <= yHoldFin) {
                                 conflictoVisual = true;
                                 System.out.printf("⚠️ TAP solapada visualmente: columna=%d, tiempo=%d, y=%d%n",
                                     ev.columna, ev.tiempo, yTap);
                                 break;
                             }
                         }
                     }
                     if (conflictoVisual) {
                    	    System.out.printf("⛔ Nota tap filtrada por conflicto visual: columna=%d, tiempo=%d%n", ev.columna, ev.tiempo);
                    	    continue;
                    	}
                 }

                 // 4. Crear la nota solo si no hubo conflicto
                 if ("hold".equalsIgnoreCase(ev.tipo)) {
                	 Nota nota = new Nota(
                         xColumna,
                         yAparicion,
                         columna,
                         true,
                         ev.duracion,
                         velocidadPxPorMs,
                         resolucion
                     );
                     /*
                       	System.out.printf("🟢 Nota creada: tipo=%s, columna=%d, tiempo=%d, duracion=%d%n",
                    		 ev.tipo, ev.columna, ev.tiempo, ev.duracion);
                    		 */
                     javax.swing.SwingUtilities.invokeLater(() -> agregarNota(nota));
                 } else {
                	 Nota nota = new Nota(
                         xColumna,
                         yAparicion,
                         columna,
                         false,
                         0L,
                         velocidadPxPorMs,
                         resolucion
                     );
                     /*
                      	System.out.printf("🟢 Nota creada: tipo=%s, columna=%d, tiempo=%d, duracion=%d%n",
                    		 ev.tipo, ev.columna, ev.tiempo, ev.duracion);
                    		 */
                     javax.swing.SwingUtilities.invokeLater(() -> agregarNota(nota));
                 }

                 
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
    
    public void pausarGeneracion() {
        enPausa = true;
    }

    public void reanudarGeneracion() {
        enPausa = false;
    }

    /** DTO del JSON. */
    private static class EventoNota {
        String tipo;     // "tap" o "hold"
        int columna;     // 0..3
        long tiempo;     // ms desde inicio
        long duracion;   // ms (solo hold)
    }
}