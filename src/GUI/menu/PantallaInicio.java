package GUI.menu;

import java.awt.*;
import javax.swing.*;

import config.ResolucionManager;

import java.awt.event.*;

public class PantallaInicio extends JPanel {
    private static final long serialVersionUID = 1L;

    public PantallaInicio(ResolucionManager resolucion) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Texto superior
        JLabel texto = new JLabel("Desarrollado por G3L", SwingConstants.CENTER);
        texto.setFont(new Font("Arial", Font.BOLD, resolucion.escalarY(24)));
        texto.setForeground(Color.WHITE);
        add(texto, BorderLayout.NORTH);
        
        
        // Logo centrado
        ImageIcon logo = new ImageIcon(getClass().getResource("/img/G3LgroupMasChico.png"));
        JLabel labelLogo = new JLabel(logo, SwingConstants.CENTER);
        add(labelLogo, BorderLayout.CENTER);

      
        // Timer para cambiar a MenuInicio
        new Timer(100, new ActionListener() {
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
