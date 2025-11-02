package config;

import javax.sound.sampled.*;

public class Sonido {
    private static Clip musicaFondo;
    private static Clip efecto;

    // Streaming para MP3
    private static SourceDataLine lineaCancion;
    private static Thread hiloCancion;

    // Volumen global 0.0–1.0 (50% por defecto)
    private static float volume = 0.5f;

    public static synchronized void setearVolumen(float v) {
        volume = Math.max(0f, Math.min(1f, v));
        aplicarVolumen(musicaFondo);
        aplicarVolumen(efecto);
        aplicarVolumen(lineaCancion); // importante para la canción en streaming
    }

    public static synchronized float getVolumen() {
        return volume;
    }

    private static void aplicarVolumen(Line line) {
        if (line == null) return;
        try {
            FloatControl gain = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gain.getMinimum();
            float dB = (volume <= 0f) ? min : (float) (20.0 * Math.log10(volume));
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

    public static void reproducirMusicaLoop(String rutaClasspath) {
        try {
            if (musicaFondo != null && musicaFondo.isRunning()) return;

            detenerMusica();
            AudioInputStream in = AudioSystem.getAudioInputStream(
                Sonido.class.getResource("/" + rutaClasspath)
            );
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(in);
            aplicarVolumen(musicaFondo);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void detenerMusica() {
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.close();
            musicaFondo = null;
        }
    }

    public static void reproducirEfecto(String rutaClasspath) {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(
                Sonido.class.getResource("/" + rutaClasspath)
            );
            efecto = AudioSystem.getClip();
            efecto.open(in);
            aplicarVolumen(efecto);
            efecto.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- MP3 por streaming (evita invalid frame size: NOT_SPECIFIED) ---

    public static synchronized void detenerCancion() {
        if (hiloCancion != null && hiloCancion.isAlive()) {
            hiloCancion.interrupt();
            hiloCancion = null;
        }
        if (lineaCancion != null) {
            try {
                lineaCancion.stop();
                lineaCancion.flush();
                lineaCancion.close();
            } catch (Exception ignored) {}
            lineaCancion = null;
        }
    }

    public static synchronized void reproducirCancion(String rutaClasspath) {
        try {
            // si ya está sonando, no hagas nada
            if (lineaCancion != null && lineaCancion.isActive()) return;

            detenerMusica();   // por si había música de menú (Clip/WAV)
            detenerCancion();  // limpiar cualquier resto

            final var url = Sonido.class.getResource("/" + rutaClasspath);
            if (url == null) throw new IllegalArgumentException("Recurso no encontrado: " + rutaClasspath);

            // Abrir MP3 y convertir a PCM_SIGNED 16-bit con fallback de SR/canales
            AudioInputStream in = AudioSystem.getAudioInputStream(url);
            AudioFormat base = in.getFormat();

            float sr = (base.getSampleRate() == AudioSystem.NOT_SPECIFIED) ? 44100f : base.getSampleRate();
            int   ch = (base.getChannels()    == AudioSystem.NOT_SPECIFIED) ? 2      : base.getChannels();

            AudioFormat decoded = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sr,
                    16,
                    ch,
                    ch * 2,          // 16 bits * canales / 8
                    sr,
                    false            // little endian
            );

            AudioInputStream din = AudioSystem.getAudioInputStream(decoded, in);

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, decoded);
            try {
                lineaCancion = (SourceDataLine) AudioSystem.getLine(info);
                lineaCancion.open(decoded);
                aplicarVolumen(lineaCancion);
            } catch (Exception e) {
                System.err.println("[Sonido] ❌ Error al abrir línea de audio: " + e.getMessage());
                lineaCancion = null;
                return; // salir del método antes de crear el hilo
            }

            // 🔹 Offset negativo: retrasar inicio del audio
            if (offsetMs < 0) {
                System.out.println("[Sonido] Offset negativo, esperando " + Math.abs(offsetMs) + " ms antes de reproducir...");
                Thread.sleep(Math.abs(offsetMs));
            }

            // 🔹 Reproducir en hilo separado
            hiloCancion = new Thread(() -> {
            	
            	 if (lineaCancion == null) {
            	        System.err.println("[Sonido] Línea de audio no disponible, abortando hilo.");
            	        return;
            	    }
            	
            	
                try (AudioInputStream stream = din) {
                    lineaCancion.start();

                    // 🔹 Offset positivo: adelantar inicio descartando bytes
                    if (offsetMs > 0) {
                        long bytesPorMs = (long) ((decoded.getFrameRate() * decoded.getFrameSize()) / 1000.0);
                        long bytesAAvanzar = offsetMs * bytesPorMs;
                        byte[] basura = new byte[8192];
                        long leidos = 0;
                        while (leidos < bytesAAvanzar) {
                            long faltan = bytesAAvanzar - leidos;
                            int n = stream.read(basura, 0, (int) Math.min(basura.length, faltan));
                            if (n == -1) break;
                            leidos += n;
                        }
                        System.out.println("[Sonido] Offset positivo aplicado (descartando " + offsetMs + " ms de audio)...");
                    }

                    // 🔹 Bucle principal de reproducción
                    byte[] buffer = new byte[8192];
                    int n;
                    while (!Thread.currentThread().isInterrupted()
                           && (n = stream.read(buffer, 0, buffer.length)) != -1) {
                        lineaCancion.write(buffer, 0, n);
                    }
                    lineaCancion.drain();
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
        }
    }

    
    
    public static synchronized long getPosMs() {
        return (lineaCancion != null) ? (lineaCancion.getMicrosecondPosition() / 1000L) : 0L;
    }
    
    private static long offsetMs = 0;

    public static synchronized void setearOffset(long nuevoOffsetMs) {
        offsetMs = nuevoOffsetMs;
        System.out.println("[Sonido] Offset configurado en " + offsetMs + " ms");
    }
    
    public static synchronized long getOffset() {
        return offsetMs;
    }


    public static synchronized boolean cancionActiva() {
        return lineaCancion != null && lineaCancion.isActive();
    }
    public static synchronized void pausarCancion() {
        if (lineaCancion != null) lineaCancion.stop();
    }
    public static synchronized void reanudarCancion() {
        if (lineaCancion != null) {
            aplicarVolumen(lineaCancion);
            lineaCancion.start();
        }
    }
}
