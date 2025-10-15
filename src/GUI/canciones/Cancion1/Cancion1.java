package GUI.canciones.Cancion1;

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
        final int y1 = -100, y2 = -300, y3 = -500, y4 = -700, y5 = -800, y6 = -900 , y7 = -1000, y8 = -1100, y9 = -1200 , y10 = -1300, y11 = -1400;

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
        
        Nota n5 = new Nota(0, y4, Color.GREEN,   resolucion);
        n5.setLocation(getColumnXForWidth(3, n5.getWidth()), y5);
        agregarNota(n5);
        
        Nota n6 = new Nota(0, y4, Color.GREEN,   resolucion);
        n6.setLocation(getColumnXForWidth(3, n6.getWidth()), y6);
        agregarNota(n6);
        
        Nota n7 = new Nota(0, y4, Color.GREEN,   resolucion);
        n7.setLocation(getColumnXForWidth(3, n7.getWidth()), y7);
        agregarNota(n7);
        
        Nota n8 = new Nota(0, y4, Color.GREEN,   resolucion);
        n8.setLocation(getColumnXForWidth(3, n8.getWidth()), y8);
        agregarNota(n8);
        
        Nota n9 = new Nota(0, y4, Color.GREEN,   resolucion);
        n9.setLocation(getColumnXForWidth(3, n9.getWidth()), y9);
        agregarNota(n9);
        
        Nota n10 = new Nota(0, y4, Color.GREEN,   resolucion);
        n10.setLocation(getColumnXForWidth(3, n10.getWidth()), y10);
        agregarNota(n10);
        
        Nota n11 = new Nota(0, y4, Color.GREEN,   resolucion);
        n11.setLocation(getColumnXForWidth(3, n11.getWidth()), y11);
        agregarNota(n11);

        // Arrancar la caída
        iniciarMovimientoNotas();
    }
}
