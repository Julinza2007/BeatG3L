package config;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TraductorSM {

    public static void main(String[] args) {
        String rutaSM = "src/GUI/canciones/Cancion1/my-8-bit-hero-301280.sm";
        String rutaJSON = "src/GUI/canciones/Cancion1/my-8-bit-hero-301280.json";

        try {
            Mapa mapa = leerArchivoSM(rutaSM);
            guardarComoJSON(mapa, rutaJSON);
            System.out.println("✅ Traducción completada: " + rutaJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Mapa leerArchivoSM(String ruta) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        String linea;
        Mapa mapa = new Mapa();
        boolean leyendoNotas = false;
        int medida = 0;
        int fila = 0;

        while ((linea = br.readLine()) != null) {
            linea = linea.trim();

            if (linea.startsWith("#TITLE:")) {
                mapa.titulo = linea.substring(7, linea.length() - 1);
            } 
            else if (linea.startsWith("#BPMS:")) {
                String datos = linea.substring(6, linea.length() - 1);
                String[] partes = datos.split("=");
                if (partes.length == 2) {
                    mapa.bpm = Double.parseDouble(partes[1]);
                }
            } 
            else if (linea.startsWith("#NOTES:")) {
                leyendoNotas = true;
                // Saltar las primeras líneas de metadata de NOTES
                for (int i = 0; i < 6; i++) br.readLine();
                continue;
            } 
            else if (leyendoNotas) {
                if (linea.equals(",")) {
                    medida++;
                    fila = 0;
                } else if (linea.equals(";")) {
                    leyendoNotas = false;
                } else if (!linea.isEmpty()) {
                    for (int col = 0; col < linea.length(); col++) {
                        if (linea.charAt(col) == '1') {
                            Nota n = new Nota();
                            n.medida = medida;
                            n.fila = fila;
                            n.columna = col;
                            mapa.notas.add(n);
                        }
                    }
                    fila++;
                }
            }
        }
        br.close();
        return mapa;
    }

    public static void guardarComoJSON(Mapa mapa, String ruta) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(mapa);
        FileWriter fw = new FileWriter(ruta);
        fw.write(json);
        fw.close();
    }
}

class Mapa {
    String titulo = "";
    double bpm = 120;
    List<Nota> notas = new ArrayList<>();
}

class Nota {
    int medida;
    int fila;
    int columna;
}
