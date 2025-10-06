package GUI;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Cancion1 extends CancionBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Nota nota;

    public Cancion1(ResolucionManager resolucion) {
        super(resolucion); // Llama al constructor de CancionBase
        construirCancion(resolucion); // Invoca la lógica específica de esta canción
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
    	
    	Point puntoD = SwingUtilities.convertPoint(notaD, 0, 0, panelJuego);
    	int posX = puntoD.x;
    	
        // Crear una nota y agregarla al contentPane
        nota = new Nota(notaD.getX(), 100, Color.CYAN, resolucion); // posición inicial y color
        panelJuego.add(nota); // Agrega la nota al fondo

        // Timer para mover la nota
        int velocidad = resolucion.escalarY(5); // ajusta el valor base según lo que se vea fluido
        Timer timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nota.setLocation(nota.getX(), nota.getY() + velocidad); // velocidad de caída
            }
        });
        timer.start();
    }
}
