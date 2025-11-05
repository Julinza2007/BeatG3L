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
    
 // === Atraso manual de la generación de notas (en ms) ===
 // Editá el valor por canción. Si querés que todas se atrasen igual,
 // devolvé el mismo número en todos los cases o usá el default.
 private static long obtenerAtrasoSpawn(String rutaAudio) {
     switch (rutaAudio) {
         case "GUI/canciones/Cancion1/soda stereo - la historia de soda estereo - de musica ligera.mp3": return 1000;
         case "GUI/canciones/Cancion2/audio.mp3": return -1500;
         case "GUI/canciones/Cancion3/audio.mp3": return 0;
         case "GUI/canciones/Cancion4/audio.mp3": return 1000;
         case "GUI/canciones/Cancion5/audio.mp3": return -2300;
         case "GUI/canciones/Cancion6/audio.mp3": return 0;
         case "GUI/canciones/Cancion7/audio.mp3": return 0;
         case "GUI/canciones/Cancion8/audio.mp3": return 0;
         case "GUI/canciones/Cancion9/audio.mp3": return 0;
         default: return 0;
     }
 }


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
                if (u != null) new javax.swing.ImageIcon(u).getImage();
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
        Sonido.detenerMusica();

        List<EventoNota> mapaOriginal = cargarMapaDesdeJson();
        if (mapaOriginal == null || mapaOriginal.isEmpty()) {
            System.err.println("No se pudieron cargar notas del JSON.");
            cancionBase.iniciarMovimientoNotas();
            return;
        }

        // 🧹 Limpieza avanzada de duplicados y solapamientos
        List<EventoNota> mapaDeNotas = limpiarMapa(mapaOriginal);

        // Orden por seguridad
        mapaDeNotas.sort(Comparator.comparingLong(n -> n.tiempo));
        cancionBase.tiempoUltimaNotaMs = mapaDeNotas.get(mapaDeNotas.size() - 1).tiempo;

        // Parámetros de caída
        final int tickMs = 10;
        final int velocidadPxPorTick = resolucion.escalarY(5);
        final double velocidadPxPorMs = velocidadPxPorTick / (double) tickMs;

        final int yImpacto = cancionBase.notaD.getY();
        final int yAparicion = -resolucion.escalarY(200);
        final int distanciaPx = Math.max(1, yImpacto - yAparicion);
        final long anticipacionMs = Math.round(distanciaPx / velocidadPxPorMs);

//        // Offset por canción
//        switch (rutaAudio) {
//        case "GUI/canciones/Cancion1/audio.mp3" -> Sonido.setearOffset(-150);
////        case "GUI/canciones/Cancion2/audio.mp3" -> Sonido.setearOffset(0);
//        case "GUI/canciones/Cancion4/audio.mp3" -> Sonido.setearOffset(0);
//        case "GUI/canciones/Cancion5/audio.mp3" -> Sonido.setearOffset(0);
//        default -> Sonido.setearOffset(0);
//    }
        Sonido.reproducirCancion(rutaAudio);

        while (Sonido.cancionActiva() && Sonido.getClockMs() == 0L) Thread.onSpinWait();
       
        
        cancionBase.iniciarMovimientoNotas();

        final long EPS_MS = 3;
        final Map<Integer, Long> ultimoSpawnMsPorCol = new HashMap<>();

        hiloCreador = new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> cancionBase.notasGeneradas = false);

                for (EventoNota ev : mapaDeNotas) {
                    if (Thread.currentThread().isInterrupted()) break;

                    int columna = Math.max(0, Math.min(3, ev.columna));
                    final long atrasoSpawnMs = obtenerAtrasoSpawn(rutaAudio); // ⟵ una sola vez antes del bucle o fuera
                    long momentoAparicionMs = Math.max(0, ev.tiempo - anticipacionMs + atrasoSpawnMs);

                    while (true) {
                        if (Thread.currentThread().isInterrupted()) return;
                        long now = Sonido.getClockMs();
                        long diff = momentoAparicionMs - now;
                        if (diff <= 0) break;
                        if (diff > 3) {
                            try { Thread.sleep(1); } catch (InterruptedException ie) { return; }
                        } else Thread.onSpinWait();
                    }

                    Long last = ultimoSpawnMsPorCol.get(columna);
                    if (last != null && Math.abs(last - momentoAparicionMs) <= EPS_MS) continue;
                    ultimoSpawnMsPorCol.put(columna, momentoAparicionMs);

                    Nota n;
                    if ("hold".equalsIgnoreCase(ev.tipo)) {
                        n = new Nota(0, yAparicion, columna, true, ev.duracion, velocidadPxPorMs, resolucion);
                    } else {
                        n = new Nota(0, yAparicion, columna, false, 0L, velocidadPxPorMs, resolucion);
                    }

                    int anchoNota = n.getWidth();
                    int xColumna = cancionBase.getColumnXForWidth(columna, anchoNota);

                    // 👉 Ajuste clave: para holds, subimos el spawn según el offset de la cabeza
                    int yInicial;
                    if (n.esHold) {
                        int headOffset = n.getHeadOffsetFromTop(); // cola + cuerpo (en px)
                        yInicial = yAparicion - headOffset;        // spawnear más arriba
                    } else {
                        yInicial = yAparicion - 1;                  // como ya tenías
                    }

                    final int yIni = yInicial;
                    SwingUtilities.invokeLater(() -> {
                        n.setLocation(xColumna, yIni);
                        cancionBase.agregarNota(n);
                    });

                    SwingUtilities.invokeLater(() -> {
                        n.setLocation(xColumna, yInicial);
                        cancionBase.agregarNota(n);
                    });
                }
            } catch (Exception ignored) {}
        }, "Mapeado-Hilo");

        hiloCreador.setDaemon(true);
        hiloCreador.start();

        System.out.println("[Cancion] #notas limpias = " + mapaDeNotas.size());
    }

    public void detener() {
        if (hiloCreador != null && hiloCreador.isAlive()) {
            hiloCreador.interrupt();
            hiloCreador = null;
        }
        Sonido.detenerCancion();
    }

    // ===== Limpieza avanzada =====
    private List<EventoNota> limpiarMapa(List<EventoNota> lista) {
        Map<Integer, List<EventoNota>> porColumna = new HashMap<>();
        for (EventoNota ev : lista) porColumna.computeIfAbsent(ev.columna, c -> new ArrayList<>()).add(ev);

        List<EventoNota> limpio = new ArrayList<>();

        for (var entry : porColumna.entrySet()) {
            List<EventoNota> notas = entry.getValue();
            notas.sort(Comparator.comparingLong(n -> n.tiempo));

            List<EventoNota> filtradas = new ArrayList<>();
            EventoNota holdActivo = null;

            for (EventoNota ev : notas) {
                if ("hold".equalsIgnoreCase(ev.tipo)) {
                    long inicio = ev.tiempo;
                    long fin = ev.tiempo + ev.duracion;

                    // Elimina hold duplicado o muy solapado con anterior
                    if (holdActivo != null) {
                        long anteriorFin = holdActivo.tiempo + holdActivo.duracion;
                        if (inicio < anteriorFin - 20) {
                            double solap = (double)(anteriorFin - inicio) / ev.duracion;
                            if (solap > 0.7) continue; // se superponen demasiado
                        }
                    }
                    filtradas.add(ev);
                    holdActivo = ev;
                } else { // tap
                    if (holdActivo != null) {
                        long inicioHold = holdActivo.tiempo;
                        long finHold = holdActivo.tiempo + holdActivo.duracion;
                        if (ev.tiempo >= inicioHold && ev.tiempo <= finHold) continue;
                    }
                    filtradas.add(ev);
                }
            }
            limpio.addAll(filtradas);
        }

        limpio.sort(Comparator.comparingLong(n -> n.tiempo));
        return limpio;
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
        String tipo;
        int columna;
        long tiempo;
        long duracion;
    }

    // ===== Pantalla de carga =====
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
                	case 1 -> panel = new GUI.canciones.Cancion1.Cancion1(resolucion);
                	case 2 -> panel = new GUI.canciones.Cancion2.Cancion2(resolucion);
                	case 3 -> panel = new GUI.canciones.Cancion3.Cancion3(resolucion);
                    case 4 -> panel = new GUI.canciones.Cancion4.Cancion4(resolucion);
                    case 5 -> panel = new GUI.canciones.Cancion5.Cancion5(resolucion);
                    case 6 -> panel = new GUI.canciones.Cancion6.Cancion6(resolucion);
                    case 7 -> panel = new GUI.canciones.Cancion7.Cancion7(resolucion);
                    case 8 -> panel = new GUI.canciones.Cancion8.Cancion8(resolucion);
                    case 9 -> panel = new GUI.canciones.Cancion9.Cancion9(resolucion);
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
