package GUI;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class Animaciones {
	
	
 public static void zoomPantalla (JLabel logoLabel, ImageIcon logoBase) {
	 final int duracion = 4000;
	 final int delay = 30;
	 final int pasos = duracion / delay;
	 final int baseWidth = logoBase.getIconWidth();
     final int baseHeight = logoBase.getIconHeight();
     final double maxFactor = 1.40; // cuanto se agranda el logo
     
     final int[] pasoAct = {0}; 
     
     Timer timer = new Timer (delay, e -> {
    	 double t = (double) pasoAct[0] / pasos;
         double factor = 1.0 + (maxFactor - 1.0) * Math.sin(t * Math.PI / 2); 
         int newWidth = (int) (baseWidth * factor);
         int newHeight = (int) (baseHeight * factor);
         
         Image img = logoBase.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
         logoLabel.setIcon(new ImageIcon(img));

         pasoAct[0]++;
         if (pasoAct[0] >= pasos) ((Timer) e.getSource()).stop();
     });
     
     timer.start();
 }
    
 public void btnHover() {
 	
 }
 
 private static ImageIcon iconoCargando;
 
 public static void precargarIconoCargando() {
	    if (iconoCargando != null) return;
	    java.net.URL u = Animaciones.class.getResource("/img/Cargando.png");
	    if (u != null) iconoCargando = new ImageIcon(u);
	}
 
 public static void mostrarCargando(javax.swing.JFrame ventana) {
	    if (ventana == null) return;
	    precargarIconoCargando();

	    javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
	    panel.setOpaque(true);
	    panel.setBackground(new java.awt.Color(0, 0, 0, 120)); // fondo semitransparente

	    javax.swing.ImageIcon icono;
	    if (iconoCargando != null) {
	        icono = iconoCargando;
	    } else {
	        icono = new javax.swing.ImageIcon();
	    }

	    javax.swing.JLabel lbl = new javax.swing.JLabel(icono, javax.swing.SwingConstants.CENTER);

	    panel.add(lbl, java.awt.BorderLayout.CENTER);

	    ventana.setGlassPane(panel);
	    panel.setVisible(true);
	}
 
 public static void ocultarCargando(javax.swing.JFrame ventana) {
	    if (ventana == null) return;
	    java.awt.Component gp = ventana.getGlassPane();
	    if (gp != null) gp.setVisible(false);
	}
 
    
}
