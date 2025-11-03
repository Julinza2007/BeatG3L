package config;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import GUI.menu.MenuInicio;


public class RankingPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String FILE_NAME = "ranking.txt";
    private JPanel listaPanel;
    public RankingPanel(ResolucionManager resolucion) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel titulo = new JLabel("RANKING DE JUGADORES", SwingConstants.CENTER);
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

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnVolver, BorderLayout.SOUTH);
        btnVolver.setFocusPainted(false);

        btnVolver.addActionListener(e -> {
            JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
            ventana.setContentPane(new MenuInicio(resolucion));
            ventana.revalidate();
            ventana.repaint();
        });
    }

    private void cargarRanking() {
        listaPanel.removeAll();
        java.util.List<String[]> lista = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lista.add(linea.split(","));
            }
        } catch (IOException e) {
            JLabel sinDatos = new JLabel("No hay rankings, ¡sé el primero!", SwingConstants.CENTER);
            sinDatos.setForeground(Color.GRAY);
            sinDatos.setFont(new Font("Arial", Font.PLAIN, 14));
            listaPanel.add(sinDatos);
            revalidate();
            repaint();
            return;
        }

        lista.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));
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
}
