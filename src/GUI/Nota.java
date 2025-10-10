package GUI;

import java.awt.*;
import javax.swing.*;

import config.ResolucionManager;

public class Nota extends JPanel {

    private static final long serialVersionUID = 1L;
    private Image imagenNota;
    private Color color;

    public Nota(int posX, int posY, Color color, ResolucionManager resolucion) {
        this.color = color;

        int baseTam = 157 / 2;
        int ancho = resolucion.escalarY(baseTam);
        int alto  = resolucion.escalarY(baseTam);

        int posXEscalado = posX; // ya viene ajustado
        int posYEscalado = resolucion.escalarY(posY);

        setOpaque(false);
        setSize(ancho, alto);
        setLocation(posXEscalado, posYEscalado);

        if (color.equals(Color.RED)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/izquierda.png")).getImage();
        } else if (color.equals(Color.YELLOW)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/abajo.png")).getImage();
        } else if (color.equals(Color.MAGENTA)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/arriba.png")).getImage();
        } else if (color.equals(Color.GREEN)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/derecha.png")).getImage();
        } else {
            imagenNota = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenNota != null) {
            g.drawImage(imagenNota, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
