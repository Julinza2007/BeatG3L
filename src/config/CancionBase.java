package config;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import GUI.Fondo;
import GUI.Nota;
import GUI.NotasUsuario;

public abstract class CancionBase extends JPanel {

    private static final long serialVersionUID = 1L;

    protected NotasUsuario notaD, notaF, notaJ, notaK;
    protected JPanel panelJuego;
    protected List<Nota> notasActivas = new ArrayList<>();
    protected boolean nivelSuperado = false;
    protected ResolucionManager resolucion;
    private Timer timerNotas;

    public CancionBase(ResolucionManager resolucion) {
        this.resolucion = resolucion;
        setLayout(new BorderLayout());
        setFocusable(true);

        Fondo fondo = new Fondo(resolucion.getFondoOpciones());
        fondo.setLayout(new BorderLayout());
        add(fondo, BorderLayout.CENTER);

        panelJuego = new JPanel(null);
        panelJuego.setOpaque(false);
        fondo.add(panelJuego, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            notasJugador(resolucion);
            construirCancion(resolucion); // ← ¡llamada directa aquí!
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (nivelSuperado) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D -> presionar(notaD);
                    case KeyEvent.VK_F -> presionar(notaF);
                    case KeyEvent.VK_J -> presionar(notaJ);
                    case KeyEvent.VK_K -> presionar(notaK);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (nivelSuperado) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D -> soltar(notaD);
                    case KeyEvent.VK_F -> soltar(notaF);
                    case KeyEvent.VK_J -> soltar(notaJ);
                    case KeyEvent.VK_K -> soltar(notaK);
                }
            }
        });

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }


    private void notasJugador(ResolucionManager resolucion) {
        panelJuego.setLayout(null);

        System.out.println("Ancho panelJuego: " + panelJuego.getWidth());
        System.out.println("Ancho pantalla: " + resolucion.getAncho());
        
        // Dimensiones base
        int baseAncho = 125 / 2;
        int baseAlto = 175 / 2;
        int separacionBase = 120; // Separación base (120)
        int baseY = 850;

        // Dimensiones de la nota: escaladas por Y
        int ancho = resolucion.escalarX(baseAncho); 
        int alto = resolucion.escalarY(baseAlto);   
        int yPos = resolucion.escalarY(baseY); 
        
        // Separación horizontal: escalada por X para centrado horizontal
        int separacionEscalada = resolucion.escalarX(separacionBase);

        // --- CÁLCULO DE CENTRADO HORIZONTAL USANDO ESCALA X ---
        
        // 1. Ancho total del bloque de notas (en píxeles base)
        // Ancho Total Base = (Ancho_Nota * 4) + (Separacion * 3)
        int anchoTotalBloqueBase = (baseAncho * 4) + (separacionBase * 3);
        
        // 2. Escalar el ancho total del bloque usando el factor X
        int anchoTotalEscalado = resolucion.escalarX(anchoTotalBloqueBase); 
        
        // 3. Obtener el ancho del contenedor (ancho de la ventana/panel)
        int anchoPantalla = panelJuego.getWidth();
        
        // 4. Calcular la nueva baseX (posición inicial X para la nota D):
        // baseX_centrada = (Ancho_Pantalla - Ancho_Total_Bloque_Escalado) / 2
        int baseX = (anchoPantalla - anchoTotalEscalado) / 2;
        // ----------------------------------------------------
        
        // Se crean las NotasUsuario usando las posiciones X centradas
        // Posición X para D: baseX
        notaD = new NotasUsuario("/img/notas/notasUsuario/izquierda.png",
                "/img/notas/notasUsuario/izquierdaPresion.png",
                baseX, yPos, ancho, alto);
        // Posición X para F: baseX + 1 * SeparacionEscalada
        notaF = new NotasUsuario("/img/notas/notasUsuario/abajo.png",
                "/img/notas/notasUsuario/abajoPresion.png",
                baseX + separacionEscalada, yPos, ancho, alto);
        // Posición X para J: baseX + 2 * SeparacionEscalada
        notaJ = new NotasUsuario("/img/notas/notasUsuario/arriba.png",
                "/img/notas/notasUsuario/arribaPresion.png",
                baseX + separacionEscalada * 2, yPos, ancho, alto);
        // Posición X para K: baseX + 3 * SeparacionEscalada
        notaK = new NotasUsuario("/img/notas/notasUsuario/derecha.png",
                "/img/notas/notasUsuario/derechaPresion.png",
                baseX + separacionEscalada * 3, yPos, ancho, alto);

        panelJuego.add(notaD);
        panelJuego.add(notaF);
        panelJuego.add(notaJ);
        panelJuego.add(notaK);
    }

    private void presionar(NotasUsuario notaUsuario) {
        notaUsuario.presionar();
        verificarColision(notaUsuario);
    }

    private void soltar(NotasUsuario notaUsuario) {
        notaUsuario.soltar();
    }

    // --- Nueva colisión mejorada ---
    private void verificarColision(JPanel notaUsuario) {
        Rectangle hitUsuario = notaUsuario.getBounds();
        int tolX = Math.max(1, resolucion.escalarX(10)); // tolerancia lateral

        for (int i = notasActivas.size() - 1; i >= 0; i--) {
            Nota n = notasActivas.get(i);

            int centroUsuario = hitUsuario.x + hitUsuario.width / 2;
            int centroNota = n.getX() + n.getWidth() / 2;

            if (Math.abs(centroUsuario - centroNota) > tolX) continue;

            if (n.getBounds().intersects(hitUsuario)) {
                panelJuego.remove(n);
                notasActivas.remove(i);
                panelJuego.repaint();
                System.out.println("le pegaste");
                return;
            }
        }
    }

    protected void iniciarMovimientoNotas() {
        int velocidad = resolucion.escalarY(5);

        timerNotas = new Timer(10, e -> {
            for (int i = notasActivas.size() - 1; i >= 0; i--) {
                Nota n = notasActivas.get(i);
                n.setLocation(n.getX(), n.getY() + velocidad);
                eliminarNotasPasadas(n);
            }
            panelJuego.repaint();
        });
        timerNotas.start();
    }

    protected void detenerMovimientoNotas() {
        if (timerNotas != null && timerNotas.isRunning()) {
            timerNotas.stop();
        }
    }

    private void eliminarNotasPasadas(Nota n) {
        int margen = Math.max(1, resolucion.escalarY(n.getHeight() / 2));
        int bordeInferior = resolucion.escalarY(1080);
        if (n.getY() > bordeInferior + margen) {
            panelJuego.remove(n);
            panelJuego.repaint();
        }
    }

    public void agregarNota(Nota nota) {
        panelJuego.add(nota);
        notasActivas.add(nota);
        panelJuego.setComponentZOrder(nota, 0);
    }

    protected abstract void construirCancion(ResolucionManager resolucion);
}
