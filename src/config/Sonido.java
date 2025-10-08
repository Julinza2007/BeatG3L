package config;

import javax.sound.sampled.*;

public class Sonido {
    private static Clip musicaFondo;
    private static Clip efecto;

    // Volumen global 0.0–1.0 (50% por defecto)
    private static float volume = 0.5f;

    public static synchronized void setVolume(float v) {
        volume = Math.max(0f, Math.min(1f, v));
        // Reaplicar al vuelo si hay clips activos
        aplicarVolumen(musicaFondo);
        aplicarVolumen(efecto);
    }

    public static synchronized float getVolume() {
        return volume;
    }

    private static void aplicarVolumen(Clip clip) {
        if (clip == null) return;

        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gain.getMinimum();
            // Mapear 0..1 a (-80..0] dB en forma logarítmica
            float dB = (volume <= 0f) ? min : (float) (20.0 * Math.log10(volume));
            if (dB > 0f) dB = 0f;                 // nunca amplificar
            if (dB < min) dB = min;               // respetar mínimo del control
            gain.setValue(dB);
            return;
        } catch (Exception ignored) {}

        try {
            FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
            vol.setValue(Math.max(0f, Math.min(1f, volume)));
            return;
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
            aplicarVolumen(musicaFondo);              // ← aplicar volumen actual
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
            aplicarVolumen(efecto);                   // ← aplicar volumen actual
            efecto.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

