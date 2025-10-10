package GUI;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import config.ResolucionManager;

public class NotasUsuario extends JPanel {

    private static final long serialVersionUID = 1L;

    private Image imgNormal;
    private Image imgPresion;
    private Image actual;
    private int ancho;
    private int alto;

    public NotasUsuario(String rutaNormal, String rutaPresion, int x, int y, int ancho, int alto) {
        setOpaque(false);
        imgNormal = new ImageIcon(getClass().getResource(rutaNormal)).getImage();
        imgPresion = new ImageIcon(getClass().getResource(rutaPresion)).getImage();
        actual = imgNormal;
        this.ancho = ancho;
        this.alto = alto;
        setBounds(x, y, ancho, alto);
    }

    public void presionar() {
        actual = imgPresion;
        repaint();
    }

    public void soltar() {
        actual = imgNormal;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (actual != null) {
            g.drawImage(actual, 0, 0, ancho, alto, this);
        }
    }
}
