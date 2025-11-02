package config;

import java.util.*;
import GUI.Nota;
import javax.swing.SwingUtilities;

public class Mapeado {

    private final CancionBase cancionBase;
    private final ResolucionManager resolucion;
    private final String rutaAudio;
    private final String rutaJson;
    private Thread hiloCreador;

    public Mapeado(CancionBase cancionBase, ResolucionManager resolucion, String rutaAudio, String rutaJson) {
        this.cancionBase = cancionBase;
        this.resolucion = resolucion;
        this.rutaAudio = rutaAudio;
        this.rutaJson = rutaJson;
    }

    // ===== Precarga mínima, una sola vez =====
    private static volatile boolean primeraVezLista = false;

    public static boolean estaListaLaPrimeraVez() { return primeraVezLista; }

    public static void prepararPrimeraVezAsync(Runnable alTerminar) {
        if (primeraVezLista) {
            if (alTerminar != null) SwingUtilities.invokeLater(alTerminar);
            return;
        }
        Thread hilo = new Thread(() -> {
            try {
                precargarImagenes();
                prepararAudio();
                primeraVezLista = true;
            } finally {
                if (alTerminar != null) SwingUtilities.invokeLater(alTerminar);
            }
        }, "precarga-primera-vez");
        hilo.setDaemon(true);
        hilo.start();
    }

    public static void esperar(Runnable cuandoListo) {
        if (cuandoListo == null) return;
        prepararPrimeraVezAsync(cuandoListo);
    }

    private static void precargarImagenes() {
        String[] rutas = {
            "/img/notas/notasUsuario/izquierda.png",
            "/img/notas/notasUsuario/izquierdaPresion.png",
            "/img/notas/notasUsuario/abajo.png",
            "/img/notas/notasUsuario/abajoPresion.png",
            "/img/notas/notasUsuario/arriba.png",
            "/img/notas/notasUsuario/arribaPresion.png",
            "/img/notas/notasUsuario/derecha.png",
            "/img/notas/notasUsuario/derechaPresion.png",
            "/img/notas/izquierda.png",
            "/img/notas/abajo.png",
            "/img/notas/arriba.png",
            "/img/notas/derecha.png",
            "/img/Cargando.png"
        };
        for (String r : rutas) {
            try {
                java.net.URL u = Mapeado.class.getResource(r);
                if (u != null) new javax.swing.ImageIcon(u).getImage(); // fuerza decodificación PNG
            } catch (Exception ignorar) {}
        }
    }

    private static void prepararAudio() {
        javax.sound.sampled.AudioFormat f = new javax.sound.sampled.AudioFormat(44100f, 16, 2, true, false);
        try {
            javax.sound.sampled.DataLine.Info info =
                new javax.sound.sampled.DataLine.Info(javax.sound.sampled.SourceDataLine.class, f);
            javax.sound.sampled.SourceDataLine linea =
                (javax.sound.sampled.SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
            linea.open(f, 1024);
            linea.start();
            linea.stop();
            linea.close();
        } catch (Exception ignorar) {}
    }

    // ===== Iniciar canción / spawner sincronizado al reloj lógico =====
    public void iniciar() {
        // 1) Música de menú off
        Sonido.detenerMusica();

        // 2) Cargar mapa
        List<EventoNota> mapaDeNotas = cargarMapaDesdeJson();
        if (mapaDeNotas == null || mapaDeNotas.isEmpty()) {
            System.err.println("No se pudieron cargar notas del JSON.");
            cancionBase.iniciarMovimientoNotas();
            return;
        }

        // Orden por seguridad
        mapaDeNotas.sort(Comparator.comparingLong(n -> n.tiempo));
        cancionBase.tiempoUltimaNotaMs = mapaDeNotas.get(mapaDeNotas.size() - 1).tiempo;

        // 3) Parámetros de caída
        final int tickMs = 10;
        final int velocidadPxPorTick = resolucion.escalarY(5);
        final double velocidadPxPorMs = velocidadPxPorTick / (double) tickMs;

        final int yImpacto = cancionBase.notaD.getY();
        final int yAparicion = -resolucion.escalarY(200);
        final int distanciaPx = Math.max(1, yImpacto - yAparicion);
        final long anticipacionMs = Math.round(distanciaPx / velocidadPxPorMs);

        // 4) Arrancar caída y audio (una sola vez)

        // Offset por canción (ajustá las rutas a tus nombres reales)
        switch (rutaAudio) {
            case "GUI/canciones/Cancion4/audio.mp3" -> Sonido.setearOffset(0);
            case "GUI/canciones/Cancion5/audio.mp3" -> Sonido.setearOffset(0);
            default -> Sonido.setearOffset(0);
        }
        Sonido.reproducirCancion(rutaAudio); // respeta offset (+/-)

        // Esperar a que el reloj lógico comience (>0) o el hilo audio muera
        while (Sonido.cancionActiva() && Sonido.getClockMs() == 0L) {
            Thread.onSpinWait();
        }
        
        cancionBase.iniciarMovimientoNotas();


        // 5) Crear notas en hilo, sincronizado al reloj lógico (pausa-friendly)
        hiloCreador = new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> cancionBase.notasGeneradas = false);

                for (EventoNota ev : mapaDeNotas) {
                    if (Thread.currentThread().isInterrupted()) break;

                    int columna = Math.max(0, Math.min(3, ev.columna));
                    long momentoAparicionMs = Math.max(0, ev.tiempo - anticipacionMs);

                    // Esperar usando Sonido.getClockMs() (se congela en pausa)
                    while (true) {
                        if (Thread.currentThread().isInterrupted()) return;
                        long now = Sonido.getClockMs(); // reloj lógico desde start (ms)
                        long diff = momentoAparicionMs - now;
                        if (diff <= 0) break;
                        if (diff > 3) {
                            try { Thread.sleep(1); } catch (InterruptedException ie) { return; }
                        } else {
                            Thread.onSpinWait();
                        }
                    }

                    int ladoTemporal = resolucion.escalarUniformeMin(157 / 2, 80);
                    int xColumna = cancionBase.getColumnXForWidth(columna, ladoTemporal);

                    Nota n = ("hold".equalsIgnoreCase(ev.tipo))
                            ? new Nota(xColumna, yAparicion, columna, true, ev.duracion, velocidadPxPorMs, resolucion)
                            : new Nota(xColumna, yAparicion, columna, false, 0L,          velocidadPxPorMs, resolucion);

                    SwingUtilities.invokeLater(() -> cancionBase.agregarNota(n));
                }
            } catch (Exception ignored) {
                // salida limpia del hilo
            }
        }, "Mapeado-Hilo");

        hiloCreador.setDaemon(true);
        hiloCreador.start();
        
        System.out.println("[Cancion] Rutas: JSON=" + rutaJson + " | MP3=" + rutaAudio);

        System.out.println("[Cancion] #notas = " + mapaDeNotas.size());
        mapaDeNotas.stream().limit(12).forEach(ev ->
            System.out.printf("[Cancion] t=%d  tipo=%s  dur=%d  col=%d%n",
                ev.tiempo, ev.tipo, ev.duracion, ev.columna)
        );

        long maxT = mapaDeNotas.stream().mapToLong(n -> n.tiempo).max().orElse(-1);
        long minDelta = Long.MAX_VALUE;
        long prev = Long.MIN_VALUE;
        for (var ev : mapaDeNotas) {
            if (prev != Long.MIN_VALUE) {
                long d = ev.tiempo - prev;
                if (d > 0 && d < minDelta) minDelta = d;
            }
            prev = ev.tiempo;
        }
        System.out.printf("[Cancion] maxT=%d ms  |  minDelta=%s ms%n",
            maxT, (minDelta==Long.MAX_VALUE?"NA":String.valueOf(minDelta)));
        System.out.println("[Cancion] anticipacionMs=" + anticipacionMs);
        
        
    }

    public void detener() {
        if (hiloCreador != null && hiloCreador.isAlive()) {
            hiloCreador.interrupt();
            hiloCreador = null;
        }
        Sonido.detenerCancion();
    }

    // ===== Loader de JSON =====
    private List<EventoNota> cargarMapaDesdeJson() {
        try (var is = getClass().getResourceAsStream(rutaJson);
             var lector = new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)) {
            var raiz = com.google.gson.JsonParser.parseReader(lector).getAsJsonObject();
            var gson = new com.google.gson.Gson();
            var tipoLista = new com.google.gson.reflect.TypeToken<List<EventoNota>>() {};
            return gson.fromJson(raiz.getAsJsonArray("notas"), tipoLista.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static class EventoNota {
        String tipo;   // "tap" o "hold"
        int columna;   // 0..3
        long tiempo;   // ms desde inicio
        long duracion; // ms (solo hold)
    }

    // ===== Pantalla de carga (igual) =====
    public static void esperarCarga(int cancion, javax.swing.JFrame ventana, ResolucionManager resolucion) {
        if (ventana == null) return;

        GUI.Animaciones.mostrarCargando(ventana);
        ventana.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        final long inicioNs = System.nanoTime();
        final int minimoMs = 5000;

        prepararPrimeraVezAsync(() -> {
            long transcurridoMs = (System.nanoTime() - inicioNs) / 1_000_000L;
            int faltante = (int) Math.max(0, minimoMs - transcurridoMs);

            javax.swing.Timer t = new javax.swing.Timer(faltante, ev -> {
                javax.swing.JPanel panel = null;
                switch (cancion) {
                     case 4 -> panel = new GUI.canciones.Cancion4.Cancion4(resolucion);
                    case 5 -> panel = new GUI.canciones.Cancion5.Cancion5(resolucion);
                    default -> { /* nada */ }
                }

                if (panel != null) {
                    ventana.setContentPane(panel);
                    ventana.revalidate();
                    ventana.repaint();
                    javax.swing.SwingUtilities.invokeLater(panel::requestFocusInWindow);
                }

                GUI.Animaciones.ocultarCargando(ventana);
                ventana.setCursor(java.awt.Cursor.getDefaultCursor());
            });
            t.setRepeats(false);
            t.start();
        });
    }
}
