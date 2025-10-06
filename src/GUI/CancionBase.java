package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public abstract class CancionBase extends JPanel {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JPanel notaD;
    protected JPanel notaF;
    protected JPanel notaJ;
    protected JPanel notaK;
    
    private boolean dPressed = false;
    private boolean fPressed = false;
    private boolean jPressed = false;
    private boolean kPressed = false;
    private boolean nivelSuperado = false;
    
    protected JPanel panelJuego;

    private Fondo contentPane;
    
    protected abstract void construirCancion(ResolucionManager resolucion); // cada nivel define sus notas y ritmo

    public CancionBase(ResolucionManager resolucion) {
    	
    	setLayout(new BorderLayout());
    	
        // Fondo principal
        contentPane = new Fondo(resolucion.getFondoOpciones());
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setOpaque(true);

        panelJuego = new JPanel(null); // layout nulo para posicionamiento absoluto
        panelJuego.setOpaque(false);


        contentPane.add(panelJuego, BorderLayout.CENTER);
        
        add(contentPane, BorderLayout.CENTER);

        // El panel principal recibe eventos de teclado
        setFocusable(true);

        // --- KeyListener para detectar las teclas D, F, J, K ---
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        notaD.setBackground(Color.WHITE);
                        notaD.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                        dPressed = true;
                        break;
                    case KeyEvent.VK_F:
                        notaF.setBackground(Color.WHITE);
                        notaF.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                        fPressed = true;
                        break;
                    case KeyEvent.VK_J:
                        notaJ.setBackground(Color.WHITE);
                        notaJ.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 3));
                        jPressed = true;
                        break;
                    case KeyEvent.VK_K:
                        notaK.setBackground(Color.WHITE);
                        notaK.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                        kPressed = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        notaD.setBackground(Color.RED);
                        notaD.setBorder(null);
                        dPressed = false;
                        break;
                    case KeyEvent.VK_F:
                        notaF.setBackground(Color.YELLOW);
                        notaF.setBorder(null);
                        fPressed = false;
                        break;
                    case KeyEvent.VK_J:
                        notaJ.setBackground(Color.MAGENTA);
                        notaJ.setBorder(null);
                        jPressed = false;
                        break;
                    case KeyEvent.VK_K:
                        notaK.setBackground(Color.GREEN);
                        notaK.setBorder(null);
                        kPressed = false;
                        break;
                }
            }
        });

        // Configurar las notas del jugador (los paneles de colores)
        notasJugador(resolucion);

        // --- Asegurar que el foco llegue correctamente ---
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });
    }

    // --- Panel inferior con las notas ---
    private void notasJugador(ResolucionManager resolucion) {
        JPanel panelSur = new JPanel();
        panelSur.setOpaque(false);
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.X_AXIS));
        contentPane.add(panelSur, BorderLayout.SOUTH);

        int ancho = resolucion.escalarX(50);
        int alto = resolucion.escalarY(50);

        notaD = new JPanel();
        notaD.setPreferredSize(new Dimension(ancho, alto));
        notaD.setMaximumSize(new Dimension(ancho, alto));
        notaD.setBackground(Color.RED);

        notaF = new JPanel();
        notaF.setPreferredSize(new Dimension(ancho, alto));
        notaF.setMaximumSize(new Dimension(ancho, alto));
        notaF.setBackground(Color.YELLOW);

        notaJ = new JPanel();
        notaJ.setPreferredSize(new Dimension(ancho, alto));
        notaJ.setMaximumSize(new Dimension(ancho, alto));
        notaJ.setBackground(Color.MAGENTA);

        notaK = new JPanel();
        notaK.setPreferredSize(new Dimension(ancho, alto));
        notaK.setMaximumSize(new Dimension(ancho, alto));
        notaK.setBackground(Color.GREEN);

        // Separaciones y orden
        panelSur.add(Box.createHorizontalGlue());
        panelSur.add(notaD);
        panelSur.add(Box.createRigidArea(new Dimension(40, 0)));
        panelSur.add(notaF);
        panelSur.add(Box.createRigidArea(new Dimension(40, 0)));
        panelSur.add(notaJ);
        panelSur.add(Box.createRigidArea(new Dimension(40, 0)));
        panelSur.add(notaK);
        panelSur.add(Box.createHorizontalGlue());
        panelSur.add(Box.createRigidArea(new Dimension(0, 250)));
    }
    
    protected Fondo getContentPaneFondo() {
        return contentPane;
    }
    
}
