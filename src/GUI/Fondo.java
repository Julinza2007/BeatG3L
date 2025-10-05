package GUI;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Fondo extends JPanel {

	private static final long serialVersionUID = 1L;
    private ImageIcon imagen;

    public Fondo(String path) {
        imagen = new ImageIcon(getClass().getResource(path));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);
    }

}
