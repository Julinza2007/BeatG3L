package GUI;

import java.awt.*;
import javax.swing.*;
import config.ResolucionManager;

public class Nota extends JPanel {

    private static final long serialVersionUID = 1L;

    private Image imagenNota;
    private Color color;
    private int columna = 0; // opcional: para re-alinear en resizes

    public Nota(int posX, int posY, Color color, ResolucionManager resolucion) {
        this.color = color;

        // Tamaño base real de la nota móvil: 157x157 → la dibujamos a la mitad, con escala uniforme
        int base = 157 / 2;
        int lado = resolucion.escalarUniformeMin(base, 80); // usa UNIFORME (no X/Y distinto)

        // NO re-escalar Y al posicionar, recibimos posY en pixeles del panel (puede ser negativo para spawn)
        setOpaque(false);
        setSize(lado, lado);
        setLocation(posX, posY);

        // Elegir sprite según color (tu lógica actual)
        if (color.equals(Color.RED)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/izquierda.png")).getImage();
            columna = 0;
        } else if (color.equals(Color.YELLOW)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/abajo.png")).getImage();
            columna = 1;
        } else if (color.equals(Color.MAGENTA)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/arriba.png")).getImage();
            columna = 2;
        } else if (color.equals(Color.GREEN)) {
            imagenNota = new ImageIcon(getClass().getResource("/img/notas/derecha.png")).getImage();
            columna = 3;
        } else {
            imagenNota = null;
        }
    }

    /** Para que CancionBase pueda re-alinear tras un resize. */
    public int getColumna() {
        return columna;
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
