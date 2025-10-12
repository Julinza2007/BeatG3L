package GUI.canciones;

import java.awt.Color;
import GUI.Nota;
import config.CancionBase;
import config.ResolucionManager;

public class Cancion1 extends CancionBase {

    private static final long serialVersionUID = 1L;

    public Cancion1(ResolucionManager resolucion) {
        super(resolucion);
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
        // Se llama automáticamente DESPUÉS de que las columnas están bien posicionadas

        // Spawn Y en pixeles de panel (negativos para entrar desde arriba)
        final int y1 = -100, y2 = -300, y3 = -500, y4 = -700;

        // Creamos notas “de color” como venías, pero la X la alineamos con el helper:
        Nota n1 = new Nota(0, y1, Color.RED,     resolucion);
        n1.setLocation(getColumnXForWidth(0, n1.getWidth()), y1);
        agregarNota(n1);

        Nota n2 = new Nota(0, y2, Color.YELLOW,  resolucion);
        n2.setLocation(getColumnXForWidth(1, n2.getWidth()), y2);
        agregarNota(n2);

        Nota n3 = new Nota(0, y3, Color.MAGENTA, resolucion);
        n3.setLocation(getColumnXForWidth(2, n3.getWidth()), y3);
        agregarNota(n3);

        Nota n4 = new Nota(0, y4, Color.GREEN,   resolucion);
        n4.setLocation(getColumnXForWidth(3, n4.getWidth()), y4);
        agregarNota(n4);

        // Arrancar la caída
        iniciarMovimientoNotas();
    }
}
