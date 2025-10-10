package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import config.ResolucionManager;

public class Nota extends JPanel {

    private static final long serialVersionUID = 1L;

    private Image imagenNota;  // imagen visual de la nota
    private Color color;
    private int ancho;
    private int alto;

    public Nota(int posX, int posY, Color color, ResolucionManager resolucion) {
        this.color = color;

        ancho = resolucion.escalarX(75);
        alto = resolucion.escalarY(75);
        int posXEscalado = resolucion.escalarX(posX);
        int posYEscalado = resolucion.escalarY(posY);

        setBackground(color); // conserva el color original (por compatibilidad)
        setPreferredSize(new Dimension(ancho, alto));
        setMaximumSize(new Dimension(ancho, alto));
        setSize(ancho, alto);
        setLocation(posXEscalado, posYEscalado);

        // --- Asignar la imagen según el color de la nota ---
        if (color.equals(Color.RED)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/izquierda.png")).getImage();
        } else if (color.equals(Color.YELLOW)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/abajo.png")).getImage();
        } else if (color.equals(Color.MAGENTA)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/arriba.png")).getImage();
        } else if (color.equals(Color.GREEN)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/derecha.png")).getImage();
        } else {
            imagenNota = null; // por si se usa otro color más adelante
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar la imagen si existe
        if (imagenNota != null) {
            g.drawImage(imagenNota, 0, 0, ancho, alto, this);
        } else {
            // En caso de que no haya imagen, mantiene el color sólido
            g.setColor(color);
            g.fillRect(0, 0, ancho, alto);
        }
    }
}
