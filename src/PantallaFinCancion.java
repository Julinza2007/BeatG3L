package GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import config.Ranking;
import config.RankingPanel;
import config.ResolucionManager;
import GUI.menu.MenuInicio;

public class PantallaFinCancion extends JPanel {

    private static final long serialVersionUID = 1L;

    public PantallaFinCancion(int puntajeFinal, ResolucionManager resolucion) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Título
        JLabel titulo = new JLabel("CANCION COMPLETADA!", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(Color.YELLOW);
        titulo.setBorder(BorderFactory.createEmptyBorder(50, 10, 10 ,10));
        add(titulo, BorderLayout.NORTH);

        // Panel central
        JPanel panelCentro = new JPanel();
        panelCentro.setOpaque(false);
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        
        JPanel posicionMedalla = new JPanel();
        	posicionMedalla.setOpaque(false);
        	posicionMedalla.setLayout(new BoxLayout(posicionMedalla, BoxLayout.X_AXIS));
        	posicionMedalla.setAlignmentX(Component.CENTER_ALIGNMENT);
        

        JLabel labelPuntaje = new JLabel("Tu puntaje final: " + puntajeFinal + " pts", SwingConstants.CENTER);
        labelPuntaje.setFont(new Font("Arial", Font.PLAIN, 20));
        labelPuntaje.setForeground(Color.WHITE);
        labelPuntaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
      JLabel labelMedalla = new JLabel(getMedallaPorPuntaje(puntajeFinal));
      labelMedalla.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        JLabel labelNombre = new JLabel("Ingresa tu nombre:", SwingConstants.CENTER);
        labelNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        labelNombre.setForeground(Color.LIGHT_GRAY);
        labelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField campoNombre = new JTextField(15);
        campoNombre.setMaximumSize(new Dimension(250, 30));
        campoNombre.setFont(new Font("Arial", Font.PLAIN, 16));
        campoNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCentro.add(labelPuntaje);
        panelCentro.add(Box.createVerticalStrut(20));
        panelCentro.add(labelNombre);
        panelCentro.add(labelMedalla);
        panelCentro.add(Box.createVerticalStrut(10));
        panelCentro.add(campoNombre);
        panelCentro.add(Box.createVerticalStrut(40));

        add(panelCentro, BorderLayout.CENTER);

        // Botones
        JPanel botones = new JPanel();
        botones.setBackground(Color.BLACK);

        JButton btnGuardar = new JButton("Guardar y ver Ranking");
        btnGuardar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnGuardar.setFocusPainted(false);

        JButton btnMenu = new JButton("Volver al Menú");
        btnMenu.setFont(new Font("Arial", Font.PLAIN, 14));
        btnMenu.setFocusPainted(false);

        botones.add(btnGuardar);
        botones.add(Box.createHorizontalStrut(20));
        botones.add(btnMenu);

        add(botones, BorderLayout.SOUTH);

        // Acción para guardar y mostrar ranking
        btnGuardar.addActionListener((ActionEvent e) -> {
            String nombre = campoNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresá tu nombre.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ranking.guardarPuntuacion(nombre, puntajeFinal);

            // Cambiar a RankingPanel
            JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
            ventana.setContentPane(new RankingPanel(resolucion));
            ventana.revalidate();
            ventana.repaint();
        });

        // Volver al menú principal
        btnMenu.addActionListener(e -> {
            JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
            ventana.setContentPane(new MenuInicio(resolucion));
            ventana.revalidate();
            ventana.repaint();
        });
    }
    
    private ImageIcon getMedallaPorPuntaje(int puntaje) {
    	String ruta;
    	
    	
    	if(puntaje >= 25000) {
    		ruta = "src/img/medallaOroTransparente.png";
    	} else if(puntaje >= 15000) {
    		ruta = "src/img/medallaPlataTransparente.png";
    	} else {
    		ruta = "src/img/medallaBronceTransparente.png";
    	}
    	
    	
    	
    	
    	
    	
    	ImageIcon icon = new ImageIcon(ruta);
    	Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
    	return new ImageIcon(img);
    	
    	
    }
    
    
    
    
}
