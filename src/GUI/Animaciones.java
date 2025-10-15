package GUI;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class Animaciones {
    public static Timer animarLogo(JLabel logoLabel, ImageIcon logoBase) {
        int pasos = 5;
        int delay = 40;

        final int baseWidth = logoBase.getIconWidth();
        final int baseHeight = logoBase.getIconHeight();
        final int[] pasoActual = {0};
        final boolean[] creciendo = {true};

        Timer timer = new Timer(delay, e -> {
            if (creciendo[0]) pasoActual[0]++; else pasoActual[0]--;

            double factor = 1.0 + (0.1 * pasoActual[0] / pasos);
            int newWidth = (int) (baseWidth * factor);
            int newHeight = (int) (baseHeight * factor);

            Image img = logoBase.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));

            if (pasoActual[0] >= pasos) creciendo[0] = false;
            else if (pasoActual[0] <= 0) {
                creciendo[0] = true;
                ((Timer) e.getSource()).stop();
                logoLabel.setIcon(logoBase);
            }
        });

        timer.setInitialDelay(0);
        timer.start();
        return timer;
    }
    
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
    
}
