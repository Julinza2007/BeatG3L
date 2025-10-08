package GUI.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import GUI.canciones.Cancion1;
import GUI.Componentes;
import GUI.Fondo;
import config.ResolucionManager;

public class MenuJuego extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MenuJuego(ResolucionManager resolucion) {
    	ImageIcon btnBase = new ImageIcon(getClass().getResource("/img/fondo/btnEscalado.png"));
    	
    	setLayout(new BorderLayout());

        // Fondo del menú de opciones
        Fondo contentPane = new Fondo(resolucion.getFondoOpciones());
        contentPane.setLayout(new BorderLayout(10, 10));
        add(contentPane);
        
        // Panel superior con título
        JLabel titulo = new JLabel("Canciones", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, resolucion.escalarY(36)));
        titulo.setForeground(Color.WHITE);
        contentPane.add(titulo, BorderLayout.NORTH);
        
        
        // Panel central con Botones de canciones
        JPanel panelCentral = new JPanel();
        panelCentral.setOpaque(false);
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        contentPane.add(panelCentral, BorderLayout.CENTER);
        
        JButton btnCancion1 = Componentes.crearBotonConImagen("cancion 1", btnBase, resolucion);
        panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
        panelCentral.add(Box.createVerticalGlue());
        panelCentral.add(btnCancion1);
        panelCentral.add(Box.createVerticalGlue());
        
        
        
        
        
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));
        contentPane.add(panelBotones, BorderLayout.SOUTH);
        
        JButton btnVolver = new JButton("Volver");
        panelBotones.add(Box.createRigidArea(new Dimension(resolucion.escalarX(20), 0)));
        panelBotones.add(Box.createHorizontalGlue());
        panelBotones.add(btnVolver);
        panelBotones.add(Box.createHorizontalGlue());
        
        Font fuenteBotones = new Font("Arial", Font.BOLD, resolucion.escalarY(22));
        Dimension tamBoton = new Dimension(resolucion.escalarX(200), resolucion.escalarY(60));

        for (JButton boton : new JButton[]{btnVolver, btnCancion1}) {
            boton.setFont(fuenteBotones);
            boton.setForeground(Color.WHITE);
            boton.setFocusPainted(false);
            boton.setContentAreaFilled(false);
            boton.setOpaque(false);
            boton.setPreferredSize(tamBoton);
            boton.setMaximumSize(tamBoton);
        }
        
        btnCancion1.addActionListener(e -> {
            JFrame ventana = (JFrame) this.getTopLevelAncestor();
            Cancion1 cancion = new Cancion1(resolucion);
            ventana.setContentPane(cancion);
            ventana.revalidate();
            ventana.repaint();

            // 🔥 Asegura el foco al nuevo panel después de ser visible
            javax.swing.SwingUtilities.invokeLater(() -> {
                cancion.requestFocusInWindow();
            });
        });
        
        btnVolver.addActionListener(e -> {
            JFrame ventana = (JFrame) this.getTopLevelAncestor();
            MenuInicio menu = new MenuInicio(resolucion);
            ventana.setContentPane(menu);
            ventana.revalidate();
            ventana.repaint();
        });
        
    }
}
