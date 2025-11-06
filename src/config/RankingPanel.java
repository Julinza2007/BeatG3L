package config;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class RankingPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel listaPanel;
    private int numeroCancion;
    
    
    private static final String[] nombres_canciones = {
            "",  // índice 0 vacío
            "De Música Ligera - Soda Stereo",      // Canción 1
            "Givenchy - Duki",                      // Canción 2
            "Viva La Vida - Coldplay",             // Canción 3
            "My 8 Bit Hero",                        // Canción 4
            "I Don't Wanna Be Me",                  // Canción 5
            "505 - Arctic Monkeys",                 // Canción 6
            "A Match Into Water - Pierce The Veil", // Canción 7
            "Goosebumps - Travis Scott",            // Canción 8
            "Bring Me To Life - Evanescence"        // Canción 9
        };
    
    
    public RankingPanel(ResolucionManager resolucion, int numeroCancion) {
        this.numeroCancion = numeroCancion;
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        String nombreCancion = obtenerNombreCancion(numeroCancion);
        JLabel titulo = new JLabel(nombreCancion, SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setOpaque(true);
        titulo.setBackground(Color.YELLOW);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBackground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(listaPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(titulo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        	
        cargarRanking();

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnContinuar, BorderLayout.SOUTH);
        btnContinuar.setFocusPainted(false);

        btnContinuar.addActionListener(e -> {
            JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
            mostrarDialogoSegunCancion(ventana, resolucion);
        });
    }
    
    private String obtenerNombreCancion(int numero) {
    	if(numero >= 1 && numero < nombres_canciones.length) {
    		return "RANKING DE JUGADORES - " + nombres_canciones[numero];
    	}
    	return "RANKING DE JUGADORES - CANCION " + numero;
    }

    private void cargarRanking() {
        listaPanel.removeAll();
        java.util.List<String[]> lista = Ranking.obtenerRanking(numeroCancion);

        if (lista.isEmpty()) {
            JLabel sinDatos = new JLabel("¡Felicitaciones! Sos el primero en esta canción", SwingConstants.CENTER);
            sinDatos.setForeground(Color.GRAY);
            sinDatos.setFont(new Font("Arial", Font.PLAIN, 14));
            listaPanel.add(sinDatos);
            revalidate();
            repaint();
            return;
        }

        int limite = Math.min(10, lista.size());
        for (int i = 0; i < limite; i++) {
            String nombre = lista.get(i)[0];
            String puntaje = lista.get(i)[1];
            JPanel fila = crearCajaJugador(i + 1, nombre, puntaje);
            listaPanel.add(fila);
        }
        revalidate();
        repaint();
    }

    private JPanel crearCajaJugador(int posicion, String nombre, String puntaje) {
        JPanel caja = new JPanel(new BorderLayout());
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        caja.setBorder(new LineBorder(Color.DARK_GRAY, 2, true));
        caja.setBackground(new Color(30, 30, 30));
        caja.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.setOpaque(true);

        Color colorTexto = Color.white;
        if (posicion == 1) colorTexto = Color.YELLOW;
        else if (posicion == 2) colorTexto = Color.LIGHT_GRAY;
        else if (posicion == 3) colorTexto = new Color(205, 127, 50);

        JLabel lblPos = new JLabel(String.format("%02d", posicion), SwingConstants.CENTER);
        lblPos.setFont(new Font("Consolas", Font.BOLD, 30));
        lblPos.setForeground(colorTexto);
        lblPos.setPreferredSize(new Dimension(60, 55));

        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 30));
        lblNombre.setForeground(colorTexto);

        JLabel lblPuntaje = new JLabel(puntaje + " pts", SwingConstants.CENTER);
        lblPuntaje.setFont(new Font("Arial", Font.PLAIN, 30));
        lblPuntaje.setForeground(colorTexto);
        lblPuntaje.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        caja.add(lblPos, BorderLayout.WEST);
        caja.add(lblNombre, BorderLayout.CENTER);
        caja.add(lblPuntaje, BorderLayout.EAST);

        caja.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { caja.setBackground(new Color(50, 50, 50)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { caja.setBackground(new Color(30, 30, 30)); }
        });

        return caja;
    }

    private void mostrarDialogoSegunCancion(JFrame ventana, ResolucionManager resolucion) {
        if (ventana == null) return;

        switch (numeroCancion) {
            case 1 -> ventana.setContentPane(new GUI.dialogos2(ventana, resolucion));
            case 2 -> ventana.setContentPane(new GUI.dialogos3(ventana, resolucion));
            case 3 -> ventana.setContentPane(new GUI.dialogos4(ventana, resolucion));
            case 4 -> ventana.setContentPane(new GUI.dialogos5(ventana, resolucion));
            case 5 -> ventana.setContentPane(new GUI.dialogos6(ventana, resolucion));
            case 6 -> ventana.setContentPane(new GUI.dialogos7(ventana, resolucion));
            case 7 -> ventana.setContentPane(new GUI.dialogos8(ventana, resolucion));
            case 8 -> ventana.setContentPane(new GUI.dialogos9(ventana, resolucion));
            default -> ventana.setContentPane(new GUI.menu.MenuJuego(resolucion));
        }
        
        ventana.revalidate();
        ventana.repaint();
    }
}
