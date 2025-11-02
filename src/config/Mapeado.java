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
        if (primeraVezLista) { // ya está listo
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
    

    // — Separado por métodos (modular y legible) —
    private static void precargarImagenes() {
        // Notas jugador (las mismas rutas que ya usás)
        String[] rutas = {
            "/img/notas/notasUsuario/izquierda.png",
            "/img/notas/notasUsuario/izquierdaPresion.png",
            "/img/notas/notasUsuario/abajo.png",
            "/img/notas/notasUsuario/abajoPresion.png",
            "/img/notas/notasUsuario/arriba.png",
            "/img/notas/notasUsuario/arribaPresion.png",
            "/img/notas/notasUsuario/derecha.png",
            "/img/notas/notasUsuario/derechaPresion.png",
            // Una cabeza por columna (con esto ya “despierta” Java2D)
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
        // No reproduce nada: sólo abre/cierra una línea PCM pequeña
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

        // Orden por seguridad (si el JSON vino desordenado)
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

        // 4) Arrancar movimiento y AUDIO antes del spawner
        cancionBase.iniciarMovimientoNotas();
        
        switch (rutaAudio) {
        case "GUI/canciones/Cancion1/audio.mp3" -> Sonido.setearOffset(0);
        case "GUI/canciones/Cancion5/audio.mp3" -> Sonido.setearOffset(-2300);
//        case "src/GUI/canciones/Cancion6/audio.mp3" -> Sonido.setearOffset(150);
        default -> Sonido.setearOffset(0);
    }

    Sonido.reproducirCancion(rutaAudio);


        
        
        Sonido.reproducirCancion(rutaAudio);

        // Esperar a que el audio realmente arranque
        while (!Sonido.cancionActiva() || Sonido.getPosMs() == 0) {
            Thread.onSpinWait();
        }

        // Tomar tiempo base (cuando el audio empezó)
        final long t0AudioMs = Sonido.getPosMs();

        // 5) Crear notas en hilo, sincronizado al reloj del audio
        hiloCreador = new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> cancionBase.notasGeneradas = false);

                for (EventoNota ev : mapaDeNotas) {
                    int columna = Math.max(0, Math.min(3, ev.columna));
                    long momentoAparicionMs = Math.max(0, ev.tiempo - anticipacionMs);

                    // Esperar usando el reloj real del audio
                    while (true) {
                        long now = Sonido.getPosMs() - t0AudioMs;
                        long diff = momentoAparicionMs - now;
                        if (diff <= 0) break;
                        if (diff > 3) Thread.sleep(1);
                        else Thread.onSpinWait();
                    }

                    int ladoTemporal = resolucion.escalarUniformeMin(157 / 2, 80);
                    int xColumna = cancionBase.getColumnXForWidth(columna, ladoTemporal);

                    if ("hold".equalsIgnoreCase(ev.tipo)) {
                        Nota n = new Nota(xColumna, yAparicion, columna, true, ev.duracion, velocidadPxPorMs, resolucion);
                        SwingUtilities.invokeLater(() -> cancionBase.agregarNota(n));
                    } else {
                        Nota n = new Nota(xColumna, yAparicion, columna, false, 0L, velocidadPxPorMs, resolucion);
                        SwingUtilities.invokeLater(() -> cancionBase.agregarNota(n));
                    }
                }
            } catch (InterruptedException ignored) {
                // salida limpia del hilo
            }
        }, "Mapeado-Hilo");

        hiloCreador.setDaemon(true);
        hiloCreador.start();
    }



    public void detener() {
        if (hiloCreador != null && hiloCreador.isAlive()) {
            hiloCreador.interrupt();
            hiloCreador = null;
        }
        Sonido.detenerCancion();
    }
    
    public static void esperarCarga(int cancion, javax.swing.JFrame ventana, ResolucionManager resolucion) {
        if (ventana == null) return;

        // Mostrar overlay y cursor ocupado
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
//                    case 1 -> panel = new GUI.canciones.Cancion1.Cancion1(resolucion);
//                    case 2 -> panel = new GUI.canciones.Cancion2.Cancion2(resolucion);
//                    case 3 -> panel = new GUI.canciones.Cancion3.Cancion3(resolucion);
//                    case 4 -> panel = new GUI.canciones.Cancion4.Cancion4(resolucion);
                    case 5 -> panel = new GUI.canciones.Cancion5.Cancion5(resolucion);
//                    case 6 -> panel = new GUI.canciones.Cancion6.Cancion6(resolucion);
//                    case 7 -> panel = new GUI.canciones.Cancion7.Cancion7(resolucion);
//                    case 8 -> panel = new GUI.canciones.Cancion8.Cancion8(resolucion);
//                    case 9 -> panel = new GUI.canciones.Cancion9.Cancion9(resolucion);
                    default -> { /* nada */ }
                }

                if (panel != null) {
                    ventana.setContentPane(panel);
                    ventana.revalidate();
                    ventana.repaint();
                    javax.swing.SwingUtilities.invokeLater(panel::requestFocusInWindow);
                }

                // Ocultar overlay y restaurar cursor cuando realmente terminamos
                GUI.Animaciones.ocultarCargando(ventana);
                ventana.setCursor(java.awt.Cursor.getDefaultCursor());
            });
            t.setRepeats(false);
            t.start();
        });
    }




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
        String tipo;
        int columna;
        long tiempo;
        long duracion;
    }
}
