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
    protected boolean notasGeneradas = true;
    
    private long tiempoInicioNotasMs = 0;
    protected long tiempoUltimaNotaMs = 0; // tiempo relativo desde inicio

    // base (1920x1080)
    private static final int BASE_SEPARACION_X = 120;
    private static final int BASE_MARGIN_BOTTOM = 180;

    // centros de columna (pixeles en panelJuego)
    private final int[] centrosCol = new int[4];

    // === NUEVO: estado para holds ===
    /** Y (top) de las teclas, actúa como "línea de hit" vertical. */
    protected int yImpacto = 0;
    /** Teclas presionadas por columna: 0=D,1=F,2=J,3=K */
    private final boolean[] teclaActiva = new boolean[4];
    /** Anti-spam para puntaje continuo (uno cada ~80 ms por columna) */
    private final long[] ultimoTickHoldMs = new long[4];

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
        precision.setBounds(resolucion.escalarX(1375), resolucion.escalarY(250), 600, 500);
        panelJuego.add(precision);

        SwingUtilities.invokeLater(() -> {
            crearTeclasJugador();
            layoutColumnasYAnclarAbajo();
        });

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

        // === NUEVO: línea de hit vertical (top de las teclas)
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

    // ====== Entrada de usuario (con estado por columna) ======
    private void presionar(int col, NotasUsuario notaUsuario) {
        teclaActiva[col] = true;
        notaUsuario.presionar();
        verificarColision(notaUsuario);
    }

    private void soltar(int col, NotasUsuario notaUsuario) {
        teclaActiva[col] = false;
        notaUsuario.soltar();
    }

/* <<<<<<< HEAD
    /** Hit detection con soporte de holds: la cabeza puntúa una sola vez y NO se remueve el componente. 
=======
>>>>>>> 48d8f5bbd6c8f744661c53289ea53e198d5f012f
*/
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
// <<<<<<< HEAD
            if (Math.abs(centroUsuario - centroNota) > tolX) continue;

            if (n.getBounds().intersects(hitUsuario)) {
                // Puntaje básico de impacto (cabeza o tap)
            	if (diferencia > 50 && diferencia < 100) {
                    precision.Bad();
                    precision.setForeground(Color.LIGHT_GRAY);
                } else if (diferencia > 30 && diferencia < 50) {
                    precision.Good();
                    precision.setForeground(Color.GREEN);
                } else if (diferencia <= 30) {
                    precision.Perfect();
                    precision.setForeground(Color.YELLOW);
                } 

                if (n.esHold) {
                	n.marcarResuelta();
                    // Cabeza del hold: puntuar una sola vez y NO remover
                    n.marcarHeadSiNoFue();
                    return;
                }

                // Tap normal: remover
/*=======

            if (Math.abs(centroUsuario - centroNota) > tolX) continue;

            if (n.getBounds().intersects(hitUsuario)) {
                if (diferencia > 30) {
                    precision.Good();
                    precision.setForeground(Color.GREEN);
                } else if (diferencia < 30) {
                    precision.Perfect();
                    precision.setForeground(Color.YELLOW);
                }
>>>>>>> 48d8f5bbd6c8f744661c53289ea53e198d5f012f
*/
                panelJuego.remove(n);
                notasActivas.remove(i);
                panelJuego.repaint();
                return;
            }
        }
    }

    // ====== Movimiento + puntaje continuo de holds ======
    protected void iniciarMovimientoNotas() {
        final int velocidad = resolucion.escalarY(5);
        tiempoInicioNotasMs = System.currentTimeMillis();
        final long tiempoFinalizacionEsperadoMs = tiempoUltimaNotaMs + 4000; // tiempo relativo desde inicio
        timerNotas = new Timer(10, e -> {
            // mover
            for (int i = notasActivas.size() - 1; i >= 0; i--) {
            	
                Nota n = notasActivas.get(i);
                n.setLocation(n.getX(), n.getY() + velocidad);
            }
            // puntaje continuo para holds (si corresponde)
            tickHoldScoring();

            // eliminar (taps pasados al borde; holds al pasar la cola por la línea)
            for (int i = notasActivas.size() - 1; i >= 0; i--) {
                eliminarNotasPasadas(notasActivas.get(i));
            }

            panelJuego.repaint();
            
            
            long ahora = System.currentTimeMillis();
            long tiempoTranscurrido = ahora - tiempoInicioNotasMs;
            boolean tiempoSuficiente = tiempoTranscurrido >= tiempoFinalizacionEsperadoMs;
            /*
            System.out.printf("⏱️ Tiempo: %d ms | tiempo delay: " + tiempoFinalizacionEsperadoMs + " | Generadas: %b | Activas: %d%n",
            	    ahora - tiempoInicioNotasMs, notasGeneradas, notasActivas.size());
            */
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

    /**
     * Puntaje continuo: si la tecla de su columna está presionada
     * y el cuerpo del hold está cruzando la línea de hit, sumamos ticks
     * cada ~80ms. (Muy simple, puedes sofisticarlo luego.)
     */
    private void tickHoldScoring() {
        final long ahora = System.currentTimeMillis();
        final long intervaloMs = 80; // cada 80ms da un "Good"

        for (Nota n : notasActivas) {
            if (!n.esHold) continue;

            int col = n.getColumna();
            if (!teclaActiva[col]) continue;

            // ¿El cuerpo del hold cruza la línea de hit?
            // Consideramos cruzado si y < yImpacto < y+H
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

    /**
     * Eliminación de notas:
     *  - Tap: si salió por abajo (comportamiento anterior).
     *  - Hold: si la COLA pasó la línea de hit (bottom >= yImpacto + mitad de la tecla),
     *          lo removemos sin marcar Miss (ya lo evaluó el puntaje continuo).
     */
    private void eliminarNotasPasadas(Nota n) {
// <<<<<<< HEAD
/*        if (n.esHold) {
            // Cola = borde superior del componente
            int top = n.getY();
            int umbral = yImpacto + (notaD.getHeight() / 2); // un poquito por debajo de la línea
            if (top >= umbral) {
                panelJuego.remove(n);
                notasActivas.remove(n);
            }
            return;
        }
*/
        // Tap normal: si sale de pantalla => Miss

    	int bordeInferior = panelJuego.getHeight();
        int margen = Math.max(1, resolucion.escalarY(n.getHeight() / 2));
    	
    	if (n.fueResuelta()){ 
    		if (n.getY() > bordeInferior + margen) {
    			panelJuego.remove(n);
                notasActivas.remove(n);
    		}
    		return;
    	} // no evaluar ni marcar Miss
    	
       
        if (n.getY() > bordeInferior + margen) {
        	long ahora = System.currentTimeMillis();
            precision.Miss();
            System.out.printf("❌ MISS registrado: columna=%d, tiempoActual=%d%n",
            	    n.getColumna(), ahora - tiempoInicioNotasMs);
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

        String nombre = JOptionPane.showInputDialog(
            this,
            "¡Canción terminada!\nIngresa tu nombre:",
            "Fin del nivel",
            JOptionPane.PLAIN_MESSAGE
        );

        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = "Jugador";
        }

        int puntajeFinal = precision.getPuntos();
        Ranking.guardarPuntuacion(nombre, puntajeFinal);

        nivelSuperado = true;

        // Mostrar el ranking con botón "Volver"
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
            RankingPanel rankingPanel = new RankingPanel(resolucion); // ✅ constructor corregido
            ventana.setContentPane(rankingPanel);
            ventana.revalidate();
            ventana.repaint();
        });
    }

    protected abstract void construirCancion(ResolucionManager resolucion);
}