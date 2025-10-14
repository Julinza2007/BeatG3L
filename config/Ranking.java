package config;

import java.io.*;
import java.util.*;

public class Ranking {
	private static final String FILE_NAME = "ranking.txt";
	
	public static void guardarPuntuacion(String nombre, int puntaje) {
		try (FileWriter writer = new FileWriter(FILE_NAME, true)){
			writer.write(nombre + "," + puntaje + "\n");
			
		} catch (IOException e) {
			System.out.println("Error al guardar el puntaje: " + e.getMessage());
			
		}
	}
	
	
	public static void mostrar() {
		List<String[]> lista = new ArrayList<>();
		
		
		try(BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))){
			String linea;
			while ((linea = reader.readLine()) != null) {
				lista.add(linea.split(","));
			}
		} 
		catch (IOException e) {
			System.out.println("No hay rankings todavia");
			return;
		}
		
		lista.sort((a,b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));
		
		System.out.println("RANKING");
		for (int i = 0; i < lista.size(); i++) {
			System.out.println((i+1) + "." + lista.get(i)[0] + " - " + lista.get(i)[1]);	
		}
	}

}
