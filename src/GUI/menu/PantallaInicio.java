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
        JLabel texto = new JLabel("G3L Studios", SwingConstants.CENTER);
        texto.setBorder(BorderFactory.createEmptyBorder(30, 0, 70, 0)); 
        try {
        	fuenteElectric_kicks = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/config/fonts/electric.ttf"));
        }
        catch (Exception e) { // Si no se cumple la promesa, entra al catch y se usa por default ARIAL.
			e.printStackTrace();
			fuenteElectric_kicks = new Font("Arial", Font.BOLD, 48);
		}
        texto.setFont(fuenteElectric_kicks.deriveFont(Font.PLAIN, resolucion.escalarY(50)));
        
        texto.setForeground(Color.WHITE);
        add(texto, BorderLayout.SOUTH);
        
        
        // Logo centrado
        ImageIcon logo = new ImageIcon(getClass().getResource("/img/G3LgroupMasChico.png"));
        JLabel labelLogo = new JLabel(logo, SwingConstants.CENTER);
        add(labelLogo, BorderLayout.CENTER);
       
        Animaciones.zoomPantalla(labelLogo, logo);
        
     // Permitir que el usuario saltee la pantalla de inicio
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Si presiona cualquier tecla, pasa al menu
                JFrame ventana = (JFrame) getTopLevelAncestor();
                ventana.setContentPane(new MenuInicio(resolucion));
                ventana.revalidate();
                ventana.repaint();
            }
        });
        setFocusable(true);
        requestFocusInWindow();


      
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
