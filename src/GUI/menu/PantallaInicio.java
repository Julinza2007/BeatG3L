package GUI.menu;

import java.awt.*;
import java.awt.Font;
import GUI.Animaciones;
import javax.swing.*;
import config.ResolucionManager;

import java.awt.event.*;

public class PantallaInicio extends JPanel {
    private static final long serialVersionUID = 1L;
    private Font fuenteElectric_kicks;

    public PantallaInicio(ResolucionManager resolucion) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);


        // Texto superior
        JLabel texto = new JLabel("Desarrollado por G3L", SwingConstants.CENTER);
        try {
        	fuenteElectric_kicks = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/config/fonts/electricKicks.ttf"));
        }
        catch (Exception e) { // Si no se cumple la promesa, entra al catch y se usa por default ARIAL.
			e.printStackTrace();
			fuenteElectric_kicks = new Font("Arial", Font.BOLD, 48);
		}
        texto.setFont(fuenteElectric_kicks.deriveFont(Font.PLAIN, resolucion.escalarY(30)));
        
        texto.setForeground(Color.WHITE);
        add(texto, BorderLayout.NORTH);
        
        
        // Logo centrado
        ImageIcon logo = new ImageIcon(getClass().getResource("/img/G3LgroupMasChico.png"));
        JLabel labelLogo = new JLabel(logo, SwingConstants.CENTER);
        add(labelLogo, BorderLayout.CENTER);
        
        Animaciones.zoomPantalla(labelLogo, logo);

      
        // Timer para cambiar a MenuInicio
        new Timer(7000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame ventana = (JFrame) getTopLevelAncestor();
                ventana.setContentPane(new MenuInicio(resolucion));
                ventana.revalidate();
                ventana.repaint();
            }
        }) {/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
            setRepeats(false);
            start();
        }};
    }
    
}
