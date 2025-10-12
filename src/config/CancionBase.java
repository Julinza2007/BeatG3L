package config;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import GUI.Fondo;
import GUI.Nota;
import GUI.NotasUsuario;
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

    // base (1920x1080)
    private static final int BASE_SEPARACION_X = 120;
    private static final int BASE_MARGIN_BOTTOM = 180;

    // centros de columna (pixeles en panelJuego)
    private final int[] centrosCol = new int[4];

    private boolean columnasListas = false;  // para disparar construirCancion una única vez

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

        this.precision = new PrecisionPuntaje(0, resolucion);
        precision.setBounds(resolucion.escalarX(1300), resolucion.escalarY(500), 600, 30); // x, y, ancho, alto
        panelJuego.add(precision);

        SwingUtilities.invokeLater(() -> {
            crearTeclasJugador();
            // Intentar posicionar columnas (si el panel ya tiene tamaño)
            layoutColumnasYAnclarAbajo();
        });

        // Reposicionar en cada resize/shown con tamaño REAL
        panelJuego.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutColumnasYAnclarAbajo(); realinearNotasActivas(); }
            @Override public void componentShown(ComponentEvent e)   { layoutColumnasYAnclarAbajo(); realinearNotasActivas(); }
        });
        addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
            @Override public void ancestorResized(HierarchyEvent e) { layoutColumnasYAnclarAbajo(); realinearNotasActivas(); }
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
    }

    /** Crea las 4 teclas; las posiciones definitivas se fijan en layoutColumnasYAnclarAbajo() */
    private void crearTeclasJugador() {
        // Tamaños de tecla ya te los da tu constructor: ancho/alto por parámetros
        // Acá solo instanciamos con X/Y dummy; luego se posicionan.
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

    /** Calcula X centradas y Y pegada abajo usando el tamaño REAL del panel; guarda centros de columna. */
    private void layoutColumnasYAnclarAbajo() {
        int w = panelJuego.getWidth();
        int h = panelJuego.getHeight();
        if (w <= 0 || h <= 0) return; // esperar a tener tamaño real

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

        panelJuego.revalidate();
        panelJuego.repaint();

        // Construir la canción sólo cuando las columnas YA están correctamente posicionadas
        if (!columnasListas) {
            columnasListas = true;
            SwingUtilities.invokeLater(() -> construirCancion(resolucion));
        }
    }

    /** Re-alinea las notas activas a la columna (por si cambió el tamaño). */
    private void realinearNotasActivas() {
        for (Nota n : new ArrayList<>(notasActivas)) {
            int col = n.getColumna(); // si tu Nota “color” no tiene columna, podés añadir un setter/getter opcional
            int x = getColumnXForWidth(col, n.getWidth());
            n.setLocation(x, n.getY());
        }
        panelJuego.repaint();
    }

    /** Devuelve la X (izquierda) para centrar una nota del ancho indicado en la columna dada (0..3). */
    protected int getColumnXForWidth(int columna, int anchoNota) {
        columna = Math.max(0, Math.min(3, columna));
        return centrosCol[columna] - anchoNota/2;
    }

    private void presionar(NotasUsuario notaUsuario) {
        notaUsuario.presionar();
        verificarColision(notaUsuario);
    }

    private void soltar(NotasUsuario notaUsuario) {
        notaUsuario.soltar();
    }

    // Igual que tuya, solo revisá tolX si querés más “ancho de hit”
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
            		System.out.println("La diferencia es: " + diferencia);
            	}
            	else if (diferencia < 30) {
            		precision.Perfect();
            		precision.setForeground(Color.YELLOW);
            		System.out.println("La diferencia es: " + diferencia);
            	}
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
        if (timerNotas != null && timerNotas.isRunning()) timerNotas.stop();
    }

    private void eliminarNotasPasadas(Nota n) {
        int bordeInferior = panelJuego.getHeight(); // respecto al panel real
        int margen = Math.max(1, resolucion.escalarY(n.getHeight() / 2));
        if (n.getY() > bordeInferior + margen) {
        	System.out.println("se borro una nota pasada");
        	precision.Miss();
        	precision.setForeground(Color.RED);
            panelJuego.remove(n);
            notasActivas.remove(n);
            panelJuego.repaint();
            return;
        }
    }

    public void agregarNota(Nota nota) {
        panelJuego.add(nota);
        notasActivas.add(nota);
        panelJuego.setComponentZOrder(nota, 0);
    }

    protected abstract void construirCancion(ResolucionManager resolucion);
}
