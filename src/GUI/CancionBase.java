package GUI;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

public class CancionBase extends JPanel {
    private boolean dPressed = false;
    private boolean fPressed = false;
    private boolean jPressed = false;
    private boolean kPressed = false;
    private boolean nivelSuperado = false;

    private Fondo contentPane;

    public CancionBase(ResolucionManager resolucion) {
        contentPane = new Fondo(resolucion.getFondoOpciones());
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setFocusable(true);

        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);

        configurarNotas();
    }

    private void configurarNotas() {
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

        // Para que reciba eventos de teclado
        contentPane.requestFocusInWindow();
    }
}
