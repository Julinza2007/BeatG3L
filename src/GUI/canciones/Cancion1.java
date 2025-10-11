package GUI.canciones;

import java.awt.Color;
import java.util.ArrayList;
import GUI.Nota;
import config.CancionBase;
import config.ResolucionManager;

public class Cancion1 extends CancionBase {

    private static final long serialVersionUID = 1L;
    private ArrayList<Nota> notas = new ArrayList<>();

    public Cancion1(ResolucionManager resolucion) {
        super(resolucion); // ← sin Runnable, sin lambda, sin static
    }


    @Override
    protected void construirCancion(ResolucionManager resolucion) {
        int wCae = resolucion.escalarY(157 / 2); // Usa escalarY para el ancho de la nota que cae

        // Calculamos las posiciones centradas de cada nota activa
        int posX_D = notaD.getX() + (notaD.getWidth() - wCae) / 2;
        int posX_F = notaF.getX() + (notaF.getWidth() - wCae) / 2;
        int posX_J = notaJ.getX() + (notaJ.getWidth() - wCae) / 2;
        int posX_K = notaK.getX() + (notaK.getWidth() - wCae) / 2;

        // Creamos las notas que caen en cada columna
        Nota n1 = new Nota(posX_D, -100, Color.RED, resolucion);
        Nota n2 = new Nota(posX_F, -300, Color.YELLOW, resolucion);
        Nota n3 = new Nota(posX_J, -500, Color.MAGENTA, resolucion);
        Nota n4 = new Nota(posX_K, -700, Color.GREEN, resolucion);

        // Las agregamos al panel y a la lista
        this.notas.add(n1); super.agregarNota(n1);
        this.notas.add(n2); super.agregarNota(n2);
        this.notas.add(n3); super.agregarNota(n3);
        this.notas.add(n4); super.agregarNota(n4);

        // Iniciamos el movimiento de las notas
        iniciarMovimientoNotas();
    }
}
