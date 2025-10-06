package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NotasUsuario extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean dPressed = false;
    private boolean fPressed = false;
    private boolean jPressed = false;
    private boolean kPressed = false;
    private boolean nivelSuperado = false;

    private Fondo contentPane;

    public NotasUsuario(ResolucionManager resolucion) {
        this.setTitle("Juego");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new Fondo(resolucion.getFondoOpciones());
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setFocusable(true);
        this.setContentPane(contentPane);

        configurarNotas();
        
        JPanel notaD = new JPanel();
        notaD.setLayout(getLayout());

        this.setVisible(true);
    }

    private void configurarNotas() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                contentPane.requestFocusInWindow();
            }
        });

        contentPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D -> dPressed = true;
                    case KeyEvent.VK_F -> fPressed = true;
                    case KeyEvent.VK_J -> jPressed = true;
                    case KeyEvent.VK_K -> kPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (nivelSuperado) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D -> dPressed = false;
                    case KeyEvent.VK_F -> fPressed = false;
                    case KeyEvent.VK_J -> jPressed = false;
                    case KeyEvent.VK_K -> kPressed = false;
                }
            }
        });
    }
}
