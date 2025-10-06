package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;



public class Componentes extends JButton{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ResolucionManager resolucion;
	
	 public static JButton crearBotonConImagen(String texto, ImageIcon icono, ResolucionManager resolucion) {
	        JButton boton = new JButton(texto, icono);

	        boton.setHorizontalTextPosition(SwingConstants.CENTER);
	        boton.setVerticalTextPosition(SwingConstants.CENTER);

	        boton.setFont(new Font("Arial", Font.BOLD, resolucion.escalarY(22)));
	        boton.setForeground(Color.WHITE);

	        boton.setFocusPainted(false);
	        boton.setBorderPainted(false);
	        boton.setContentAreaFilled(false);

	        int ancho = resolucion.escalarX(300);
	        int alto  = resolucion.escalarY(130);
	        boton.setPreferredSize(new Dimension(ancho, alto));
	        boton.setMaximumSize(new Dimension(ancho, alto));
	        boton.setMinimumSize(new Dimension(ancho, alto));
	        boton.setAlignmentX(CENTER_ALIGNMENT);

	        int wImg = icono.getIconWidth();
	        int hImg = icono.getIconHeight();
	        double r = Math.min(ancho / (double) wImg, alto / (double) hImg);
	        int wEsc = Math.max(1, (int) Math.round(wImg * r));
	        int hEsc = Math.max(1, (int) Math.round(hImg * r));
	        java.awt.Image img = icono.getImage().getScaledInstance(wEsc, hEsc, java.awt.Image.SCALE_SMOOTH);
	        boton.setIcon(new ImageIcon(img));

	        boton.addMouseListener(new java.awt.event.MouseAdapter() {
	            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
	                Sonido.reproducirEfecto("GUI/sonidos/pickOpcion.wav");
	            }
	        });
	        boton.addFocusListener(new java.awt.event.FocusAdapter() {
	            @Override public void focusGained(java.awt.event.FocusEvent e) {
	                Sonido.reproducirEfecto("GUI/sonidos/pickOpcion.wav");
	            }
	        });

	        return boton;
	    }
}
