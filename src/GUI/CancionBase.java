package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
        
        notasJugador(resolucion);
        
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
                        ((NotasUsuario) notaD).presionar();
                        dPressed = true;
                        break;
                    case KeyEvent.VK_F:
                        ((NotasUsuario) notaF).presionar();
                        fPressed = true;
                        break;
                    case KeyEvent.VK_J:
                        ((NotasUsuario) notaJ).presionar();
                        jPressed = true;
                        break;
                    case KeyEvent.VK_K:
                        ((NotasUsuario) notaK).presionar();
                        kPressed = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        ((NotasUsuario) notaD).soltar();
                        dPressed = false;
                        break;
                    case KeyEvent.VK_F:
                        ((NotasUsuario) notaF).soltar();
                        fPressed = false;
                        break;
                    case KeyEvent.VK_J:
                        ((NotasUsuario) notaJ).soltar();
                        jPressed = false;
                        break;
                    case KeyEvent.VK_K:
                        ((NotasUsuario) notaK).soltar();
                        kPressed = false;
                        break;
                }
            }
        });

        // Configurar las notas del jugador (los paneles de colores)


        // --- Asegurar que el foco llegue correctamente ---
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });
    }

   
    private void notasJugador(ResolucionManager resolucion) {
        JPanel panelSur = new JPanel();
        panelSur.setOpaque(false);
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.X_AXIS));
        panelJuego.add(panelSur);
        
        

        int separacion = resolucion.escalarX(0);

        // === Crear las 4 notas fijas con tus imágenes ===
        notaD = new NotasUsuario(
            "/GUI/img/notas/notasUsuario/izquierda.png",
            "/GUI/img/notas/notasUsuario/izquierdaPresion.png",
            0, 0, resolucion
        );
        notaF = new NotasUsuario(
            "/GUI/img/notas/notasUsuario/abajo.png",
            "/GUI/img/notas/notasUsuario/abajoPresion.png",
            0, 0, resolucion
        );
        notaJ = new NotasUsuario(
            "/GUI/img/notas/notasUsuario/arriba.png",
            "/GUI/img/notas/notasUsuario/arribaPresion.png",
            0, 0, resolucion
        );
        notaK = new NotasUsuario(
            "/GUI/img/notas/notasUsuario/derecha.png",
            "/GUI/img/notas/notasUsuario/derechaPresion.png",
            0, 0, resolucion
        );

        // Añadir con separaciones
        panelSur.add(Box.createHorizontalGlue());
        panelSur.add(notaD);
        panelSur.add(Box.createRigidArea(new Dimension(separacion, 0)));
        panelSur.add(notaF);
        panelSur.add(Box.createRigidArea(new Dimension(separacion, 0)));
        panelSur.add(notaJ);
        panelSur.add(Box.createRigidArea(new Dimension(separacion, 0)));
        panelSur.add(notaK);
        panelSur.add(Box.createHorizontalGlue());

        // Espacio inferior (altura donde se ubican las flechas)
        panelSur.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(250))));
        
        int altoNotaUsuario = resolucion.escalarY(80) + resolucion.escalarY(250); 
        // Los 80px del componente NotasUsuario + los 250px del Box.createRigidArea
        
        SwingUtilities.invokeLater(() -> {
            int anchoJuego = panelJuego.getWidth();
            int altoJuego = panelJuego.getHeight();
            
            // Y donde panelSur debe empezar (casi al final de panelJuego)
            int posY = altoJuego - altoNotaUsuario; 
            int posX = 0;
            
            panelSur.setBounds(posX, posY, anchoJuego, altoNotaUsuario);
            
            panelJuego.revalidate();
            panelJuego.repaint();
        });
    }
    

    
    protected Fondo getContentPaneFondo() {
        return contentPane;
    }
    
    
    
}
