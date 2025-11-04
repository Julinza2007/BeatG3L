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
import GUI.Componentes;
import GUI.Fondo;
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

//		JButton btnCancion1 = Componentes.crearBotonConImagen("cancion 1", btnBase, resolucion);
//		panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
//		panelCentral.add(Box.createVerticalGlue());
//		panelCentral.add(btnCancion1);
//		panelCentral.add(Box.createVerticalGlue());
//		
		JButton btnCancion4 = Componentes.crearBotonConImagen("cancion 4", btnBase, resolucion);
		panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
		panelCentral.add(Box.createVerticalGlue());
		panelCentral.add(btnCancion4);
		panelCentral.add(Box.createVerticalGlue());
//		
//		JButton btnCancion5 = Componentes.crearBotonConImagen("cancion 5", btnBase, resolucion);
//		panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
//		panelCentral.add(Box.createVerticalGlue());
//		panelCentral.add(btnCancion5);
//		panelCentral.add(Box.createVerticalGlue());
//		
//		JButton btnCancion6 = Componentes.crearBotonConImagen("cancion 6", btnBase, resolucion);
//		panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
//		panelCentral.add(Box.createVerticalGlue());
//		panelCentral.add(btnCancion6);
//		panelCentral.add(Box.createVerticalGlue());
		
		JButton btnCancion7 = Componentes.crearBotonConImagen("cancion 7", btnBase, resolucion);
		panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
		panelCentral.add(Box.createVerticalGlue());
		panelCentral.add(btnCancion7);
		panelCentral.add(Box.createVerticalGlue());
		
		JButton btnCancion8 = Componentes.crearBotonConImagen("cancion 8", btnBase, resolucion);
		panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
		panelCentral.add(Box.createVerticalGlue());
		panelCentral.add(btnCancion8);
		panelCentral.add(Box.createVerticalGlue());
		
		JButton btnCancion9 = Componentes.crearBotonConImagen("cancion 9", btnBase, resolucion);
		panelCentral.add(Box.createRigidArea(new Dimension(0, resolucion.escalarY(30))));
		panelCentral.add(Box.createVerticalGlue());
		panelCentral.add(btnCancion9);
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

//		btnCancion1.addActionListener(e -> {
//		    javax.swing.JFrame ventana = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
//		    if (ventana == null) return;
//
//		    // (Opcional) anti-doble click
//		    btnCancion1.setEnabled(false);
//
//		    // Lanza la espera con overlay; adentro se oculta y se cambia el content pane
//		    config.Mapeado.esperarCarga(1, ventana, resolucion);
//
//		    // Rehabilitar el botón un poquito después para evitar spam
//		    new javax.swing.Timer(800, ev2 -> btnCancion1.setEnabled(true)).start();
//		});
//		
//		btnCancion4.addActionListener(e -> {
//		    javax.swing.JFrame ventana = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
//		    if (ventana == null) return;
//
//		    // (Opcional) anti-doble click
//		    btnCancion4.setEnabled(false);
//
//		    // Lanza la espera con overlay; adentro se oculta y se cambia el content pane
//		    Mapeado.esperarCarga(4, ventana, resolucion);
//
//		    // Rehabilitar el botón un poquito después para evitar spam
//		    new javax.swing.Timer(800, ev2 -> btnCancion4.setEnabled(true)).start();
//		});

//		btnCancion5.addActionListener(e -> {
//		    javax.swing.JFrame ventana = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
//		    if (ventana == null) return;
//
//		    // (Opcional) anti-doble click
//		    btnCancion5.setEnabled(false);
//
//		    // Lanza la espera con overlay; adentro se oculta y se cambia el content pane
//		    config.Mapeado.esperarCarga(5, ventana, resolucion);
//
//		    // Rehabilitar el botón un poquito después para evitar spam
//		    new javax.swing.Timer(800, ev2 -> btnCancion5.setEnabled(true)).start();
//		});
//		
//		btnCancion6.addActionListener(e -> {
//		    javax.swing.JFrame ventana = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
//		    if (ventana == null) return;
//
//		    // (Opcional) anti-doble click
//		    btnCancion6.setEnabled(false);
//
//		    // Lanza la espera con overlay; adentro se oculta y se cambia el content pane
//		    config.Mapeado.esperarCarga(6, ventana, resolucion);
//
//		    // Rehabilitar el botón un poquito después para evitar spam
//		    new javax.swing.Timer(800, ev2 -> btnCancion6.setEnabled(true)).start();
//		});
		
		
		btnCancion7.addActionListener(e -> {
		    javax.swing.JFrame ventana = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
		    if (ventana == null) return;

		    // (Opcional) anti-doble click
		    btnCancion7.setEnabled(false);

		    // Lanza la espera con overlay; adentro se oculta y se cambia el content pane
		    config.Mapeado.esperarCarga(7, ventana, resolucion);

		    // Rehabilitar el botón un poquito después para evitar spam
		    new javax.swing.Timer(800, ev2 -> btnCancion7.setEnabled(true)).start();
		});
		
		
		btnCancion8.addActionListener(e -> {
		    javax.swing.JFrame ventana = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
		    if (ventana == null) return;

		    // (Opcional) anti-doble click
		    btnCancion8.setEnabled(false);

		    // Lanza la espera con overlay; adentro se oculta y se cambia el content pane
		    config.Mapeado.esperarCarga(8, ventana, resolucion);

		    // Rehabilitar el botón un poquito después para evitar spam
		    new javax.swing.Timer(800, ev2 -> btnCancion8.setEnabled(true)).start();
		});
		
		

		btnCancion9.addActionListener(e -> {
		    javax.swing.JFrame ventana = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(MenuJuego.this);
		    if (ventana == null) return;

		    // (Opcional) anti-doble click
		    btnCancion9.setEnabled(false);

		    // Lanza la espera con overlay; adentro se oculta y se cambia el content pane
		    config.Mapeado.esperarCarga(9, ventana, resolucion);

		    // Rehabilitar el botón un poquito después para evitar spam
		    new javax.swing.Timer(800, ev2 -> btnCancion9.setEnabled(true)).start();
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