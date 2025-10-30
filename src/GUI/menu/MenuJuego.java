package GUI.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import GUI.Componentes;
import GUI.Fondo;
import GUI.canciones.Cancion1.Cancion1;
import GUI.canciones.Cancion5.Cancion5;
import config.Mapeado;
import config.ResolucionManager;

public class MenuJuego extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MenuJuego(ResolucionManager resolucion) {
//<<<<<<< HEAD
		ImageIcon btnBase = new ImageIcon(getClass().getResource("/img/fondo/btnEscalado.png"));

		setLayout(new BorderLayout());

		// Fondo del menú de opciones
		Fondo contentPane = new Fondo(resolucion.getFondoOpciones());
		contentPane.setLayout(new BorderLayout(10, 10));
		add(contentPane);

		JPanel contenedorJuego = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
		contenedorJuego.setOpaque(false);

		JPanel panelNorte = new JPanel();
		panelNorte.setOpaque(false);
		panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));

		// Panel superior con título
		JLabel titulo = new JLabel("Canciones", SwingConstants.CENTER);
		titulo.setFont(new Font("Arial", Font.BOLD, Math.max(resolucion.escalarY(36), 20)));
		titulo.setForeground(Color.WHITE);

		JButton btnRankings = Componentes.crearBotonConImagen("Rankings", btnBase, resolucion);

		panelNorte.add(titulo);
		panelNorte.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(75))));
		panelNorte.add(btnRankings);

		contenedorJuego.add(panelNorte);
		contentPane.add(contenedorJuego, BorderLayout.NORTH);

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
		
		JButton btnCancion5 = Componentes.crearBotonConImagen("cancion 5", btnBase, resolucion);
		panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
		panelCentral.add(Box.createVerticalGlue());
		panelCentral.add(btnCancion5);
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

		for (JButton boton : new JButton[] { btnVolver, btnCancion1 }) {
			boton.setFont(fuenteBotones);
			boton.setForeground(Color.WHITE);
			boton.setFocusPainted(false);
			boton.setContentAreaFilled(false);
			boton.setOpaque(false);
			boton.setPreferredSize(tamBoton);
			boton.setMaximumSize(tamBoton);
		}

		btnCancion1.addActionListener(e -> {
		    javax.swing.JFrame ventana = (javax.swing.JFrame)
		            javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
		    if (ventana == null) return;

		    btnCancion1.setEnabled(false);
		    ventana.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		    try {
		        config.Mapeado.esperarCarga(1, ventana, resolucion); // ← ahora con mínimo 1000 ms
		    } finally {
		        ventana.setCursor(java.awt.Cursor.getDefaultCursor());
		        btnCancion1.setEnabled(true);
		    }
		});


		
		btnCancion5.addActionListener(e -> {
		    javax.swing.JFrame ventana = (javax.swing.JFrame)
		            javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
		    if (ventana == null) return;

		    btnCancion5.setEnabled(false);
		    ventana.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

		    // Llama directo a esperarCarga (que ya espera)
		    try {
		        config.Mapeado.esperarCarga(5, ventana, resolucion);
		    } finally {
		        ventana.setCursor(java.awt.Cursor.getDefaultCursor());
		        btnCancion5.setEnabled(true);
		    }
		});
		
		
		btnRankings.addActionListener(e -> {
			JFrame ventana = (JFrame) this.getTopLevelAncestor();
			MenuRankings rankings = new MenuRankings(resolucion);
			ventana.setContentPane(rankings);
			ventana.revalidate();
			ventana.repaint();
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