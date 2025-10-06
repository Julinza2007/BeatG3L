package GUI;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

public class BeatG3L extends JFrame {

    private static final long serialVersionUID = 1L;
    private ResolucionManager resolucion;

    public BeatG3L() {
        // === DETECCIÓN AUTOMÁTICA DE LA RESOLUCIÓN DEL MONITOR ===

        // Obtener entorno gráfico del sistema
        GraphicsEnvironment entornoGrafico = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // Obtener el dispositivo de pantalla principal
        GraphicsDevice dispositivoPantalla = entornoGrafico.getDefaultScreenDevice();

        // Obtener el modo de pantalla actual
        DisplayMode modoPantalla = dispositivoPantalla.getDisplayMode();

        // Guardar dimensiones actuales del monitor
        int anchoPantalla = modoPantalla.getWidth();
        int altoPantalla = modoPantalla.getHeight();

        if (anchoPantalla >= 1920 && altoPantalla >= 1080) {
            resolucion = new ResolucionManager(1920, 1080);
        } else if (anchoPantalla >= 1600 && altoPantalla >= 900) {
            resolucion = new ResolucionManager(1600, 900);
        } else if (anchoPantalla >= 1366 && altoPantalla >= 768) {
            resolucion = new ResolucionManager(1366, 768);
        } else if (anchoPantalla >= 1280 && altoPantalla >= 720) {
            resolucion = new ResolucionManager(1280, 720);
        } else {
            resolucion = new ResolucionManager(800, 600);
        }

        setTitle("BeatG3L");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Se sacan los bordes y barra del sistema
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Se pone en pantalla completa

        setContentPane(new PantallaInicio(resolucion));
        setVisible(true);
    }
    
    public void setearResolucion(ResolucionManager nueva) {
        this.resolucion = nueva; // solo guardamos, sin cambiar el content pane
    }

    public ResolucionManager getResolucion() {
        return resolucion;
    }
}
