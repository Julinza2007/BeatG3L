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
import javax.swing.JPanel;

import GUI.Componentes;
import GUI.Fondo;
import GUI.canciones.Cancion4.Cancion4;
import config.ResolucionManager;

public class MenuRankings extends JPanel {
	
	ResolucionManager resolucion;

	public MenuRankings(ResolucionManager resolucion) {
		ImageIcon btnBase = new ImageIcon(getClass().getResource("/img/fondo/btnEscalado.png"));
		
		this.resolucion = resolucion;
		
				// Fondo del menú de opciones
				Fondo contentPane = new Fondo(resolucion.getFondoOpciones());
				contentPane.setLayout(new BorderLayout(10, 10));
				add(contentPane);
			
				// Panel central con Botones de canciones
				JPanel panelCentral = new JPanel();
				panelCentral.setOpaque(false);
				panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
				contentPane.add(panelCentral, BorderLayout.CENTER);

				JButton btnCancion4 = Componentes.crearBotonConImagen("cancion 1", btnBase, resolucion);
				panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
				panelCentral.add(Box.createVerticalGlue());
				panelCentral.add(btnCancion4);
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

				for (JButton boton : new JButton[] { btnVolver, btnCancion4 }) {
					boton.setFont(fuenteBotones);
					boton.setForeground(Color.WHITE);
					boton.setFocusPainted(false);
					boton.setContentAreaFilled(false);
					boton.setOpaque(false);
					boton.setPreferredSize(tamBoton);
					boton.setMaximumSize(tamBoton);
				}
				
				btnCancion4.addActionListener(e -> {
					JFrame ventana = (JFrame) this.getTopLevelAncestor();
					Cancion4 cancion = new Cancion4(resolucion);
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
					MenuJuego menu = new MenuJuego(resolucion);
					ventana.setContentPane(menu);
					ventana.revalidate();
					ventana.repaint();
				});

	}
	
	
			
	
}