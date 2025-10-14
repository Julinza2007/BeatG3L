package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import config.ResolucionManager;
import config.Sonido;



public class Componentes extends JButton{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static JButton crearBotonConImagen(String texto, ImageIcon icono, ResolucionManager resolucion) {
	    JButton boton = new JButton(texto, icono);
	    boton.setHorizontalTextPosition(SwingConstants.CENTER);
	    boton.setVerticalTextPosition(SwingConstants.CENTER);
	    boton.setForeground(Color.WHITE);
	    boton.setFocusPainted(false);
	    boton.setBorderPainted(false);
	    boton.setContentAreaFilled(false);

	    // Tamaño base (referencia 1920x1080)
	    int baseAncho = 300;
	    int baseAlto  = 130;

	    // Escalamos con un límite mínimo para no achicarlos demasiado
	    int anchoEscalado = resolucion.escalarX(baseAncho);
	    int altoEscalado  = resolucion.escalarY(baseAlto);

	    // 🔥 Nuevo: no dejar que bajen de cierto tamaño mínimo
	    anchoEscalado = Math.max(anchoEscalado, 220);
	    altoEscalado  = Math.max(altoEscalado, 90);

	    boton.setPreferredSize(new Dimension(anchoEscalado, altoEscalado));
	    boton.setMaximumSize(new Dimension(anchoEscalado, altoEscalado));
	    boton.setMinimumSize(new Dimension(anchoEscalado, altoEscalado));
	    boton.setAlignmentX(CENTER_ALIGNMENT);

	    // Tamaño de fuente adaptable con piso mínimo
	    int fontSize = Math.max(resolucion.escalarY(22), 16);
	    boton.setFont(new Font("Arial", Font.BOLD, fontSize));

	    // Reescalar imagen manteniendo proporción
	    int wImg = icono.getIconWidth();
	    int hImg = icono.getIconHeight();
	    double r = Math.min(anchoEscalado / (double) wImg, altoEscalado / (double) hImg);
	    int wEsc = (int) Math.round(wImg * r);
	    int hEsc = (int) Math.round(hImg * r);
	    Image img = icono.getImage().getScaledInstance(wEsc, hEsc, Image.SCALE_SMOOTH);
	    boton.setIcon(new ImageIcon(img));

	    boton.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseEntered(java.awt.event.MouseEvent e) {
	            Sonido.reproducirEfecto("sonidos/pickOpcion.wav");
	        }
	    });
	    boton.addFocusListener(new java.awt.event.FocusAdapter() {
	        @Override
	        public void focusGained(java.awt.event.FocusEvent e) {
	            Sonido.reproducirEfecto("sonidos/pickOpcion.wav");
	        }
	    });

	    return boton;
	}

}
