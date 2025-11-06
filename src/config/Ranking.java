package config;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class Ranking {
    private static final String FOLDER_NAME = "rankings/";

    // Asegurarse de que la carpeta exista
    static {
        new File(FOLDER_NAME).mkdirs();
    }

    public static void guardarPuntuacion(String nombre, int puntaje, int numeroCancion) {
        String fileName = FOLDER_NAME + "cancion" + numeroCancion + ".txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(nombre + "," + puntaje + "\n");
        } catch (IOException e) {
            System.out.println("Error al guardar el puntaje: " + e.getMessage());
        }
    }

    public static List<String[]> obtenerRanking(int numeroCancion) {
        List<String[]> lista = new ArrayList<>();
        String fileName = FOLDER_NAME + "cancion" + numeroCancion + ".txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lista.add(linea.split(","));
            }
        } catch (IOException e) {
            return lista; // Retorna lista vacía si no existe el archivo
        }

        // Ordena por puntaje (mayor a menor)
        lista.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));
        return lista;
    }

    public static void mostrar(int numeroCancion) {
        List<String[]> lista = obtenerRanking(numeroCancion);

        if (lista.isEmpty()) {
            System.out.println("No hay rankings todavía para esta canción");
            return;
        }

        System.out.println("RANKING - CANCIÓN " + numeroCancion);
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i)[0] + " - " + lista.get(i)[1]);
        }
    }
}