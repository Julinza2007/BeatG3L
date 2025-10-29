package config;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import GUI.Fondo;
import GUI.Nota;
import GUI.NotasUsuario;
import GUI.PantallaFinCancion;
import GUI.PrecisionPuntaje;

public abstract class CancionBase extends JPanel {

    private static final long serialVersionUID = 1L;

    protected NotasUsuario notaD, notaF, notaJ, notaK;
    protected JPanel panelJuego;
    protected List<Nota> notasActivas = new ArrayList<>();
    protected boolean nivelSuperado = false;
    protected ResolucionManager resolucion;
    private Timer timerNotas;
    protected PrecisionPuntaje precision;
    protected boolean notasGeneradas = true;

    private long tiempoInicioNotasMs = 0;
    protected long tiempoUltimaNotaMs = 0; // tiempo relativo desde inicio

    // base (1920x1080)
    private static final int BASE_SEPARACION_X = 120;
    private static final int BASE_MARGIN_BOTTOM = 180;

    private final int[] centrosCol = new int[4];

    protected int yImpacto = 0; // línea de hit vertical
    private final boolean[] teclaActiva = new boolean[4];
    private final long[] ultimoTickHoldMs = new long[4];

    private boolean columnasListas = false;

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

        precision = new PrecisionPuntaje(0, resolucion);
        precision.setBounds(resolucion.escalarX(1375), resolucion.escalarY(50), 600, 30);
        panelJuego.add(precision);

        SwingUtilities.invokeLater(() -> {
            crearTeclasJugador();
            layoutColumnasYAnclarAbajo();
        });

        panelJuego.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutColumnasYAnclarAbajo(); realinearNotasActivas(); }
            @Override public void componentShown(ComponentEvent e) { layoutColumnasYAnclarAbajo(); realinearNotasActivas(); }
        });
        addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
            @Override public void ancestorResized(HierarchyEvent e) { layoutColumnasYAnclarAbajo(); realinearNotasActivas(); }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (nivelSuperado) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D -> presionar(0, notaD);
                    case KeyEvent.VK_F -> presionar(1, notaF);
                    case KeyEvent.VK_J -> presionar(2, notaJ);
                    case KeyEvent.VK_K -> presionar(3, notaK);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (nivelSuperado) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D -> soltar(0, notaD);
                    case KeyEvent.VK_F -> soltar(1, notaF);
                    case KeyEvent.VK_J -> soltar(2, notaJ);
                    case KeyEvent.VK_K -> soltar(3, notaK);
                }
            }
        });
    }

    private void crearTeclasJugador() {
        notaD = new NotasUsuario("/img/notas/notasUsuario/izquierda.png",
                                 "/img/notas/notasUsuario/izquierdaPresion.png",
                                 0, 0,
                                 resolucion.escalarXMin(125/2, 80),
                                 resolucion.escalarYMin(175/2, 110));

        notaF = new NotasUsuario("/img/notas/notasUsuario/abajo.png",
                                 "/img/notas/notasUsuario/abajoPresion.png",
                                 0, 0,
                                 resolucion.escalarXMin(125/2, 80),
                                 resolucion.escalarYMin(175/2, 110));

        notaJ = new NotasUsuario("/img/notas/notasUsuario/arriba.png",
                                 "/img/notas/notasUsuario/arribaPresion.png",
                                 0, 0,
                                 resolucion.escalarXMin(125/2, 80),
                                 resolucion.escalarYMin(175/2, 110));

        notaK = new NotasUsuario("/img/notas/notasUsuario/derecha.png",
                                 "/img/notas/notasUsuario/derechaPresion.png",
                                 0, 0,
                                 resolucion.escalarXMin(125/2, 80),
                                 resolucion.escalarYMin(175/2, 110));

        panelJuego.add(notaD);
        panelJuego.add(notaF);
        panelJuego.add(notaJ);
        panelJuego.add(notaK);
    }

    private void layoutColumnasYAnclarAbajo() {
        int w = panelJuego.getWidth();
        int h = panelJuego.getHeight();
        if (w <= 0 || h <= 0) return;

        final int anchoNota = notaD.getWidth();
        final int altoNota  = notaD.getHeight();
        final int sepX = resolucion.escalarX(BASE_SEPARACION_X);

        final int anchoBloque = (anchoNota * 4) + (sepX * 3);
        final int baseX = (w - anchoBloque) / 2;
        final int marginBottom = resolucion.escalarY(BASE_MARGIN_BOTTOM);
        final int y = h - marginBottom - altoNota;

        int x0 = baseX + (anchoNota + sepX) * 0;
        int x1 = baseX + (anchoNota + sepX) * 1;
        int x2 = baseX + (anchoNota + sepX) * 2;
        int x3 = baseX + (anchoNota + sepX) * 3;

        notaD.setLocation(x0, y);
        notaF.setLocation(x1, y);
        notaJ.setLocation(x2, y);
        notaK.setLocation(x3, y);

        centrosCol[0] = x0 + anchoNota/2;
        centrosCol[1] = x1 + anchoNota/2;
        centrosCol[2] = x2 + anchoNota/2;
        centrosCol[3] = x3 + anchoNota/2;

        yImpacto = y;

        panelJuego.revalidate();
        panelJuego.repaint();

        if (!columnasListas) {
            columnasListas = true;
            SwingUtilities.invokeLater(() -> construirCancion(resolucion));
        }
    }

    private void realinearNotasActivas() {
        for (Nota n : new ArrayList<>(notasActivas)) {
            int col = n.getColumna();
            int x = getColumnXForWidth(col, n.getWidth());
            n.setLocation(x, n.getY());
        }
        panelJuego.repaint();
    }

    protected int getColumnXForWidth(int columna, int anchoNota) {
        columna = Math.max(0, Math.min(3, columna));
        return centrosCol[columna] - anchoNota/2;
    }

    private void presionar(int col, NotasUsuario notaUsuario) {
        teclaActiva[col] = true;
        notaUsuario.presionar();
        verificarColision(notaUsuario);
    }

    private void soltar(int col, NotasUsuario notaUsuario) {
        teclaActiva[col] = false;
        notaUsuario.soltar();
    }

    private void verificarColision(JPanel notaUsuario) {
        Rectangle hitUsuario = notaUsuario.getBounds();
        int tolX = Math.max(1, resolucion.escalarX(10));

        for (int i = notasActivas.size() - 1; i >= 0; i--) {
            Nota n = notasActivas.get(i);

            int centroUsuarioY = hitUsuario.y + hitUsuario.height / 2;
            int centroNotaY = n.getY() + n.getHeight() / 2;
            int diferencia = Math.abs(centroUsuarioY - centroNotaY);

            int centroUsuario = hitUsuario.x + hitUsuario.width / 2;
            int centroNota = n.getX() + n.getWidth() / 2;

            if (Math.abs(centroUsuario - centroNota) > tolX) continue;

            if (n.getBounds().intersects(hitUsuario)) {
                if (diferencia > 30) {
                    precision.Good();
                    precision.setForeground(Color.GREEN);
                } else {
                    precision.Perfect();
                    precision.setForeground(Color.YELLOW);
                }

                if (n.esHold) {
                    n.marcarResuelta();
                    n.marcarHeadSiNoFue();
                    return;
                }

                panelJuego.remove(n);
                notasActivas.remove(i);
                panelJuego.repaint();
                return;
            }
        }
    }

    protected void iniciarMovimientoNotas() {
        final int velocidad = resolucion.escalarY(5);
        tiempoInicioNotasMs = System.currentTimeMillis();
        final long tiempoFinalizacionEsperadoMs = tiempoUltimaNotaMs + 4000;

        timerNotas = new Timer(10, e -> {
            for (int i = notasActivas.size() - 1; i >= 0; i--) {
                Nota n = notasActivas.get(i);
                n.setLocation(n.getX(), n.getY() + velocidad);
            }

            tickHoldScoring();

            for (int i = notasActivas.size() - 1; i >= 0; i--) {
                eliminarNotasPasadas(notasActivas.get(i));
            }

            panelJuego.repaint();

            long ahora = System.currentTimeMillis();
            long tiempoTranscurrido = ahora - tiempoInicioNotasMs;
            boolean tiempoSuficiente = tiempoTranscurrido >= tiempoFinalizacionEsperadoMs;

            if (!notasGeneradas && notasActivas.isEmpty() && tiempoSuficiente) {
                ((Timer) e.getSource()).stop();
                finalizarCancion();
            }
        });

        timerNotas.start();
    }

    protected void detenerMovimientoNotas() {
        if (timerNotas != null && timerNotas.isRunning()) timerNotas.stop();
    }

    private void tickHoldScoring() {
        final long ahora = System.currentTimeMillis();
        final long intervaloMs = 80;

        for (Nota n : notasActivas) {
            if (!n.esHold) continue;

            int col = n.getColumna();
            if (!teclaActiva[col]) continue;

            int top = n.getY();
            int bottom = n.getY() + n.getHeight();
            if (top <= yImpacto && yImpacto <= bottom) {
                if (ahora - ultimoTickHoldMs[col] >= intervaloMs) {
                    ultimoTickHoldMs[col] = ahora;
                    precision.Good();
                    precision.setForeground(Color.GREEN);
                }
            }
        }
    }

    private void eliminarNotasPasadas(Nota n) {
        int bordeInferior = panelJuego.getHeight();
        int margen = Math.max(1, resolucion.escalarY(n.getHeight() / 2));

        if (n.fueResuelta()) {
            if (n.getY() > bordeInferior + margen) {
                panelJuego.remove(n);
                notasActivas.remove(n);
            }
            return;
        }

        if (n.getY() > bordeInferior + margen) {
            long ahora = System.currentTimeMillis();
            precision.Miss();
            precision.setForeground(Color.RED);
            panelJuego.remove(n);
            notasActivas.remove(n);
        }

        panelJuego.repaint();
    }

    public void agregarNota(Nota nota) {
        panelJuego.add(nota, 0);
        notasActivas.add(nota);
        panelJuego.setComponentZOrder(nota, 0);

        this.revalidate();
        this.repaint();
    }

    protected void finalizarCancion() {
        detenerMovimientoNotas();
        nivelSuperado = true;

        int puntajeFinal = precision.getPuntos();

        SwingUtilities.invokeLater(() -> {
            JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(CancionBase.this);
            if (ventana != null) {
                ventana.setContentPane(new PantallaFinCancion(puntajeFinal, resolucion));
                ventana.revalidate();
                ventana.repaint();
            }
        });
    }

    protected abstract void construirCancion(ResolucionManager resolucion);
}
