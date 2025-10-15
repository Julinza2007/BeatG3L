
package config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import GUI.menu.MenuInicio;

public class RankingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String FILE_NAME = "ranking.txt";
	private JPanel listaPanel;
	
	
	public RankingPanel() {
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		
		
		JLabel titulo = new JLabel("RANKING DE JUGADORES", SwingConstants.CENTER);
		titulo.setFont(new Font("Arial", Font.BOLD, 18));
		titulo.setBackground(Color.yellow);
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
		btnVolver.setFont("Arial", Font.BOLD, 14);
		add (btnVolver, BorderLayout.SOUTH);
		
		
		btnVolver.addActionListener(e -> {
			JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(this);
			GUI.menu.TransicionPantallas.volverAlInicio(this, resolucion);
		});
	}
	
	private void cargarRanking() {
		listaPanel.removeAll();
		List<String[]> lista = new ArrayList<>();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))){
			String linea;
			while((linea = reader.readLine()) != null) { 
				lista.add(linea.split(","));
			}
			
		}
		catch(IOException e) {
			JLabel sinDatos = new JLabel("No hay rankings, Se el primero!", SwingConstants.CENTER);
			sinDatos.setForeground(Color.GRAY);
			sinDatos.setFont(new Font("Arial", Font.PLAIN, 14));
			listaPanel.add(sinDatos);
			revalidate();
			repaint();
			return;
		}
		lista.sort((a,b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));
		
		int limite = Math.min(10, lista.size());
		for(int i = 0; i<limite;i++) {
			String nombre = lista.get(i)[0];
			String puntaje = lista.get(i)[1];
			
			JLabel fila = new JLabel(String.format("%02d. %-10s %6s pts", i+1, nombre, puntaje));
			fila.setFont(new Font("Arial", Font.PLAIN, 14)); 
			fila.setForeground(Color.white);
			fila.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 10));
			
			
			if (i == 0) fila.setForeground(Color.YELLOW);
			if (i == 1) fila.setForeground(Color.LIGHT_GRAY);
			if (i == 2) fila.setForeground(new Color(205, 127, 50));
			
			listaPanel.add(fila);
			
		}
		revalidate();
		repaint();
	}
	
	public static void mostrarVentanaRanking() {
		JFrame frame = new JFrame("Ranking"); 
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 600);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(new RankingPanel());
		frame.setVisible(true);
	}
	
	

}
