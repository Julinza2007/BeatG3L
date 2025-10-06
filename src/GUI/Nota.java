package GUI;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class Nota extends JPanel {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Nota (int posX, int posY, Color color, ResolucionManager resolucion) {
		int ancho = resolucion.escalarX(50);
	    int alto = resolucion.escalarY(50);
	    int posXEscalado = resolucion.escalarX(posX);
	    int posYEscalado = resolucion.escalarY(posY);

	    setPreferredSize(new Dimension(ancho, alto));
        setMaximumSize(new Dimension(ancho, alto));
	    
        setSize(ancho, alto);
        setLocation(posXEscalado, posYEscalado);
	    
	    
	}
}
