package config;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class Ranking {
    private static final String FILE_NAME = "ranking.txt";
    
    public static void guardarPuntuacion(String nombre, int puntaje) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(nombre + "," + puntaje + "\n");
        } catch (IOException e) {
            System.out.println("Error al guardar el puntaje: " + e.getMessage());
        }
    }
    
    public static void mostrar() {
        List<String[]> lista = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lista.add(linea.split(","));
            }
        } catch (IOException e) {
            System.out.println("No hay rankings todavia");
            return;
        }
        
        lista.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));
        
        System.out.println("RANKING");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i)[0] + " - " + lista.get(i)[1]);
        }
    }
    
    // Versión corregida de mostrarEnVentana()
    public static void mostrarEnVentana() {
        List<String[]> lista = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lista.add(linea.split(","));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No hay ranking todavía.",
                    "Ranking", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Ordena por puntaje (mayor a menor)
        lista.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

        // Construimos el texto para mostrar
        StringBuilder sb = new StringBuilder(" RANKING \n\n");
        for (int i = 0; i < lista.size(); i++) {
            sb.append((i + 1)).append(". ")
              .append(lista.get(i)[0])
              .append(" - ")
              .append(lista.get(i)[1])
              .append("\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString(),
                "Ranking de jugadores", JOptionPane.INFORMATION_MESSAGE);
    }
}