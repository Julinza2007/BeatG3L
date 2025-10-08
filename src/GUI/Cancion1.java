package GUI;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Cancion1 extends CancionBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Nota> notas;

    public Cancion1(ResolucionManager resolucion) {
        super(resolucion); // Llama al constructor de CancionBase
        construirCancion(resolucion); // Invoca la lógica específica de esta canción
    }

    @Override
    protected void construirCancion(ResolucionManager resolucion) {
    	
    	// 1. Aplica un retraso hasta que el componente esté dibujado y las coordenadas sean válidas.

    	SwingUtilities.invokeLater(() -> {
    		SwingUtilities.invokeLater(() -> {
    			this.notas = new ArrayList<>();
    			

    			Point puntoD = SwingUtilities.convertPoint(notaD, 0, 0, panelJuego);
    			int posX_D = puntoD.x;
    			
    			Point puntoF = SwingUtilities.convertPoint(notaF, 0, 0, panelJuego);
    			int posX_F = puntoF.x;
    			
    			Point puntoJ = SwingUtilities.convertPoint(notaJ, 0, 0, panelJuego);
    			int posX_J = puntoJ.x;
    			
    			Point puntoK = SwingUtilities.convertPoint(notaK, 0, 0, panelJuego);
    			int posX_K = puntoK.x;

    			// Crear una nota y agregarla al contentPane
    			Nota nota1 = new Nota(posX_D, 100, Color.RED, resolucion); // posición inicial y color
    			panelJuego.add(nota1); // Agrega la nota al fondo
    			this.notas.add(nota1);
    			
    			Nota nota2 = new Nota(posX_F, 100, Color.YELLOW, resolucion); // posición inicial y color
    			panelJuego.add(nota2); // Agrega la nota al fondo
    			this.notas.add(nota2);
    			
    			Nota nota3 = new Nota(posX_J, 100, Color.MAGENTA, resolucion); // posición inicial y color
    			panelJuego.add(nota3); // Agrega la nota al fondo
    			this.notas.add(nota3);
    			
    			Nota nota4 = new Nota(posX_K, 100, Color.GREEN, resolucion); // posición inicial y color
    			panelJuego.add(nota4); // Agrega la nota al fondo
    			this.notas.add(nota4);
    			
    			panelJuego.revalidate(); // Necesario para redibujar la nota


    			// Timer para mover la nota
    			int velocidad = resolucion.escalarY(5); // ajusta el valor base según lo que se vea fluido
    			Timer timer = new Timer(10, new ActionListener() {
    				public void actionPerformed(ActionEvent e) {
    					for (Nota nota : notas) {
    					nota.setLocation(nota.getX(), nota.getY() + velocidad); // velocidad de caída
    					}
    				}

    			});

    			timer.start();
    		});
    	});
    }
}
