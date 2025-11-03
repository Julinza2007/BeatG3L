package config;

import javax.sound.sampled.*;

public class Sonido {
    // --- Estado principal ---
    private static SourceDataLine lineaCancion;
    private static Thread hiloCancion;
    private static volatile boolean enPausa = false;
    private static final Object lockPausa = new Object();

    private static float volume = 0.5f;
    // Reloj lógico (tiempo transcurrido desde que “suena” 0 ms del chart)
    private static long t0Ns = 0L;           // instante de inicio lógico
    private static long pausaAcumNs = 0L;    // suma de pausas
    private static long pausaDesdeNs = 0L;   // marca al entrar en pausa

    // Offset global (por si querés setear por canción)
    private static long offsetMs = 0L;

    // ==== Volumen ====
    public static synchronized void setearVolumen(float v) {
        volume = Math.max(0f, Math.min(1f, v));
        aplicarVolumen(lineaCancion);
    }
    public static synchronized float getVolumen() { return volume; }

    private static void aplicarVolumen(Line line) {
        if (line == null) return;
        try {
            FloatControl gain = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gain.getMinimum();
            float dB  = (volume <= 0f) ? min : (float)(20.0 * Math.log10(volume));
            if (dB > 0f) dB = 0f;
            if (dB < min) dB = min;
            gain.setValue(dB);
            return;
        } catch (Exception ignored) {}
        try {
            FloatControl vol = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
            vol.setValue(Math.max(0f, Math.min(1f, volume)));
        } catch (Exception ignored) {}
    }

    // ==== Música de menú / efectos (igual que antes si los usás) ====
    private static Clip musicaFondo, efecto;
    public static void reproducirMusicaLoop(String rutaClasspath) {
        try {
            if (musicaFondo != null && musicaFondo.isRunning()) return;
            detenerMusica();
            AudioInputStream in = AudioSystem.getAudioInputStream(Sonido.class.getResource("/" + rutaClasspath));
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(in);
            aplicarVolumen(musicaFondo);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) { e.printStackTrace(); }
    }
    public static void detenerMusica() {
        if (musicaFondo != null) { musicaFondo.stop(); musicaFondo.close(); musicaFondo = null; }
    }
    public static void reproducirEfecto(String rutaClasspath) {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(Sonido.class.getResource("/" + rutaClasspath));
            efecto = AudioSystem.getClip();
            efecto.open(in);
            aplicarVolumen(efecto);
            efecto.start();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==== Canción (MP3 por streaming) ====
    public static synchronized void setearOffset(long nuevoOffsetMs) {
        offsetMs = nuevoOffsetMs;
        System.out.println("[Sonido] Offset configurado en " + offsetMs + " ms");
    }
    public static synchronized long getOffset() { return offsetMs; }

    public static synchronized void detenerCancion() {
        Thread h = hiloCancion;
        hiloCancion = null;
        if (h != null && h.isAlive()) h.interrupt();
        if (lineaCancion != null) {
            try {
                lineaCancion.stop();
                lineaCancion.flush();
                lineaCancion.close();
            } catch (Exception ignored) {}
            lineaCancion = null;
        }
        enPausa = false;
    }

    public static synchronized void reproducirCancion(String rutaClasspath) {
        reproducirCancion(rutaClasspath, offsetMs);
    }

    public static synchronized void reproducirCancion(String rutaClasspath, long offsetMsPlay) {
        // Reinicio limpio
        detenerMusica();
        detenerCancion();

        try {
            final var url = Sonido.class.getResource("/" + rutaClasspath);
            if (url == null) throw new IllegalArgumentException("Recurso no encontrado: " + rutaClasspath);

            // Abrimos MP3 y lo convertimos a PCM_SIGNED 16-bit
            AudioInputStream in = AudioSystem.getAudioInputStream(url);
            AudioFormat base = in.getFormat();
            float sr = (base.getSampleRate() == AudioSystem.NOT_SPECIFIED) ? 44100f : base.getSampleRate();
            int   ch = (base.getChannels()    == AudioSystem.NOT_SPECIFIED) ? 2      : base.getChannels();
            AudioFormat decoded = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sr, 16, ch, ch * 2, sr, false);
            AudioInputStream din = AudioSystem.getAudioInputStream(decoded, in);

            // Si offset positivo, adelantamos saltando bytes (antes de abrir línea)
            if (offsetMsPlay > 0) {
                long bytesPorMs = (long)((decoded.getFrameRate() * decoded.getFrameSize()) / 1000.0);
                long aSaltar = offsetMsPlay * bytesPorMs;
                long leidos = 0;
                byte[] basura = new byte[8192];
                while (leidos < aSaltar) {
                    long faltan = aSaltar - leidos;
                    int n = din.read(basura, 0, (int)Math.min(basura.length, faltan));
                    if (n == -1) break;
                    leidos += n;
                }
                System.out.println("[Sonido] Offset positivo aplicado: +" + offsetMsPlay + " ms");
            }

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, decoded);
            lineaCancion = (SourceDataLine) AudioSystem.getLine(info);
            lineaCancion.open(decoded);
            aplicarVolumen(lineaCancion);
            final AudioInputStream stream = din; // capturamos para usar en el hilo

            hiloCancion = new Thread(() -> {
                try (AudioInputStream s = stream) {
                    // Si offset negativo, esperamos antes de iniciar
                    if (offsetMsPlay < 0) {
                        long espera = Math.abs(offsetMsPlay);
                        System.out.println("[Sonido] Offset negativo: esperando " + espera + " ms antes de iniciar");
                        try { Thread.sleep(espera); } catch (InterruptedException ie) { return; }
                    }

                    // Arranque: fijamos el reloj lógico
                    synchronized (Sonido.class) {
                        t0Ns = System.nanoTime();
                        pausaAcumNs = 0L;
                        enPausa = false;
                    }

                    lineaCancion.start();

                    byte[] buffer = new byte[8192];
                    int n;
                    while (!Thread.currentThread().isInterrupted()
                           && lineaCancion != null
                           && (n = s.read(buffer, 0, buffer.length)) != -1) {

                        // Gate de pausa (sin cerrar la línea ni leer flujo)
                        if (enPausa) {
                            synchronized (lockPausa) {
                                pausaDesdeNs = System.nanoTime();
                                lineaCancion.stop();
                                try { while (enPausa) lockPausa.wait(); }
                                catch (InterruptedException ie) { return; }
                                // saliendo de pausa
                                long ahora = System.nanoTime();
                                synchronized (Sonido.class) { pausaAcumNs += (ahora - pausaDesdeNs); }
                                lineaCancion.start();
                            }
                        }

                        lineaCancion.write(buffer, 0, n);
                    }
                    if (lineaCancion != null) lineaCancion.drain();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    detenerCancion();
                }
            }, "MP3-Streaming");
            hiloCancion.setDaemon(true);
            hiloCancion.start();

        } catch (Exception e) {
            e.printStackTrace();
            detenerCancion();
        }
    }

    // Reloj de audio (ms) estable across pausa/reanudar/cerrar-abrir
    public static synchronized long getClockMs() {
        if (hiloCancion == null) return 0L;
        long ahora = System.nanoTime();
        if (enPausa) ahora = pausaDesdeNs;
        long ns = (ahora - t0Ns - pausaAcumNs);
        return (ns <= 0) ? 0L : (ns / 1_000_000L);
    }

    public static synchronized boolean cancionActiva() {
        return hiloCancion != null && hiloCancion.isAlive();
    }

    public static void pausarCancion() {
        if (lineaCancion == null || enPausa) return;
        enPausa = true; // el hilo entra al gate y hace stop()
    }

    public static void reanudarCancion() {
        if (lineaCancion == null || !enPausa) return;
        synchronized (lockPausa) {
            enPausa = false;
            lockPausa.notifyAll();
        }
    }
}
