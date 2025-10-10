package GUI.canciones;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import GUI.Nota;
import config.CancionBase;
import config.ResolucionManager;

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
    			
    			int anchoNotaJugador = notaD.getWidth(); // Obtenemos el ancho escalado, e.g., 62 * factor.
                
                // 2. Definir el ancho escalado de la nota que cae (W_cae)
                // NOTA: Asumo que el ancho base de la Nota que cae es 50px.
                // Si el ancho base de tu nota que cae es diferente, reemplaza 50 con el valor correcto.
                final int ANCHO_BASE_NOTA_CAIDA = 62; 
                int anchoNotaCaida = resolucion.escalarX(ANCHO_BASE_NOTA_CAIDA);
                
                // 3. Calcular el desplazamiento necesario para centrar la nota que cae.
                // Si W_jugador > W_cae, el ajuste es positivo. Si W_jugador < W_cae, el ajuste es negativo.
                int ajusteCentrado = (anchoNotaJugador / 2) - (anchoNotaCaida / 2);

    			Point puntoD = SwingUtilities.convertPoint(notaD, 0, 0, panelJuego);
    			int posX_D = puntoD.x;
    			System.out.println("El valor de posX_D es: " + posX_D);
    			
    			Point puntoF = SwingUtilities.convertPoint(notaF, 0, 0, panelJuego);
    			int posX_F = puntoF.x;
    			System.out.println("El valor de posX_F es: " + posX_F);
    			
    			Point puntoJ = SwingUtilities.convertPoint(notaJ, 0, 0, panelJuego);
    			int posX_J = puntoJ.x;
    			System.out.println("El valor de posX_J es: " + posX_J);
    			
    			Point puntoK = SwingUtilities.convertPoint(notaK, 0, 0, panelJuego);
    			int posX_K = puntoK.x;
    			System.out.println("El valor de posX_K es: " + posX_K);

    			// Crear una nota y agregarla al contentPane
    			Nota nota1 = new Nota(posX_D, resolucion.escalarY(100), Color.RED, resolucion); // posición inicial y color
    			panelJuego.add(nota1); // Agrega la nota al fondo
    			this.notas.add(nota1);
    			super.agregarNota(nota1);
    			
    			Nota nota2 = new Nota(posX_F, resolucion.escalarY(-200), Color.YELLOW, resolucion); // posición inicial y color
    			panelJuego.add(nota2); // Agrega la nota al fondo
    			this.notas.add(nota2);
    			super.agregarNota(nota2);
    			
    			Nota nota3 = new Nota(posX_J, resolucion.escalarY(-100), Color.MAGENTA, resolucion); // posición inicial y color
    			panelJuego.add(nota3); // Agrega la nota al fondo
    			this.notas.add(nota3);
    			super.agregarNota(nota3);
    			
    			Nota nota4 = new Nota(posX_K, resolucion.escalarY(150), Color.GREEN, resolucion); // posición inicial y color
    			panelJuego.add(nota4); // Agrega la nota al fondo
    			this.notas.add(nota4);
    			super.agregarNota(nota4);
    			
    			panelJuego.revalidate(); // Necesario para redibujar la nota


    			// Timer para mover la nota
    			int velocidad = resolucion.escalarY(5); // ajusta el valor base según lo que se vea fluido
    			Timer timer = new Timer(10, new ActionListener() {
    				public void actionPerformed(ActionEvent e) {
    					for (Nota nota : notasActivas) { //notasActivas pertenece a cancionBase
    					nota.setLocation(nota.getX(), nota.getY() + velocidad); // velocidad de caída
    					eliminarNotasPasadas(nota);
    					}
    				}

    			});

    			timer.start();
    		});
    	});
    }
}
