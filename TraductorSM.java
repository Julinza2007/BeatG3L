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
            System.out.println("Notas exportadas: " + mapa.notas.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Mapa leerArchivoSM(String ruta) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            Mapa mapa = new Mapa();

            // --- meta del chart ---
            Double offsetSeg = 0.0;                 // #OFFSET: en segundos (puede ser negativo)
            List<BpmChange> cambios = new ArrayList<>();  // beat -> bpm
            cambios.add(new BpmChange(0.0, 120.0)); // default por si no hubiera #BPMS

            // --- estado del bloque de notas ---
            boolean leyendoNotas = false;
            int compas = 0;                 // número de compás (0..N)
            List<String> filasCompas = new ArrayList<>(); // acumulamos filas del compás actual

            // buffers para holds por columna (se dimensionan al ver la 1ª fila)
            long[] inicioHold = null;
            boolean[] holdActivo = null;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                // ===== encabezado =====
                if (linea.startsWith("#TITLE:")) {
                    mapa.titulo = linea.substring(7, linea.length() - 1);
                } else if (linea.startsWith("#OFFSET:")) {
                    // #OFFSET: <segundos>;
                    String s = linea.substring(8, linea.length() - 1);
                    try { offsetSeg = Double.parseDouble(s); } catch (Exception ignored) {}
                } else if (linea.startsWith("#BPMS:")) {
                    // #BPMS: beat=bpm,beat=bpm, ... ;
                    String datos = linea.substring(6, linea.length() - 1);
                    cambios.clear();
                    for (String par : datos.split(",")) {
                        String[] kv = par.split("=");
                        if (kv.length == 2) {
                            double beat = Double.parseDouble(kv[0].trim());
                            double bpm  = Double.parseDouble(kv[1].trim());
                            cambios.add(new BpmChange(beat, bpm));
                        }
                    }
                    cambios.sort(Comparator.comparingDouble(b -> b.beat));
                    if (cambios.isEmpty()) cambios.add(new BpmChange(0.0, 120.0));
                }
                // ===== bloque de notas =====
                else if (linea.startsWith("#NOTES:")) {
                    leyendoNotas = true;
                    // saltar 6 líneas de metadata del chart (tipo, autor pasos, dificultad, etc.)
                    for (int i = 0; i < 6; i++) br.readLine();
                    compas = 0;
                    filasCompas.clear();
                    inicioHold = null;
                    holdActivo = null;
                    continue;
                } else if (leyendoNotas) {
                    if (linea.equals(";")) {
                        // fin del chart
                        // por si quedó un compás sin volcar (no debería, pero limpiamos)
                        procesarCompas(mapa, filasCompas, compas, offsetSeg, cambios, inicioHold, holdActivo);
                        leyendoNotas = false;
                    } else if (linea.equals(",")) {
                        // fin de compás → procesar con SU cantidad de filas
                        procesarCompas(mapa, filasCompas, compas, offsetSeg, cambios, inicioHold, holdActivo);
                        compas++;
                        filasCompas.clear();
                    } else if (!linea.isEmpty()) {
                        filasCompas.add(linea);
                        // dimensionar buffers de holds según columnas
                        int columnas = linea.length();
                        if (inicioHold == null) inicioHold = new long[columnas];
                        if (holdActivo == null) holdActivo = new boolean[columnas];
                    }
                }
            }
            return mapa;
        }
    }

    // ---- helpers internos (colocalos dentro de TraductorSM) ----
    private static class BpmChange {
        final double beat; // beat absoluto donde cambia
        final double bpm;
        BpmChange(double beat, double bpm) { this.beat = beat; this.bpm = bpm; }
    }

    /** Convierte un beat absoluto a ms acumulando cada tramo de BPM. */
    private static long beatToMs(double beatObjetivo, List<BpmChange> cambios) {
        cambios.sort(Comparator.comparingDouble(b -> b.beat));
        double ms = 0.0;
        for (int i = 0; i < cambios.size(); i++) {
            double beatIni = cambios.get(i).beat;
            double bpm = cambios.get(i).bpm;
            double beatFin = (i + 1 < cambios.size()) ? cambios.get(i + 1).beat : beatObjetivo;
            if (beatObjetivo <= beatIni) break; // nada que sumar
            double hasta = Math.min(beatFin, beatObjetivo);
            double deltaBeats = Math.max(0, hasta - beatIni);
            ms += (60000.0 / bpm) * deltaBeats;
            if (beatObjetivo <= beatFin) break;
        }
        return Math.round(ms);
    }

    /** Vuelca un compás completo usando su #filas real como subdivisión. */
    private static void procesarCompas(
            Mapa mapa,
            List<String> filasCompas,
            int compas,
            double offsetSeg,
            List<BpmChange> cambios,
            long[] inicioHold,
            boolean[] holdActivo
    ) {
        if (filasCompas.isEmpty()) return;
        final int filas = filasCompas.size(); // subdivisión REAL de este compás
        for (int fila = 0; fila < filas; fila++) {
            String row = filasCompas.get(fila);
            int columnas = row.length();
            for (int col = 0; col < columnas; col++) {
                char c = row.charAt(col);

                // beat absoluto = (compás * 4) + (fracción dentro del compás)*4
                double frac = (double) fila / (double) filas;
                double beat = compas * 4.0 + frac * 4.0;

                long tiempoMs = beatToMs(beat, cambios) + Math.round(offsetSeg * 1000.0);

                if (c == '1' && !holdActivo[col]) {
                    // solo agregar tap si no hay hold activo
                    Nota n = new Nota();
                    n.tipo = "tap";
                    n.columna = col;
                    n.tiempo = tiempoMs;
                    n.duracion = 0;
                    mapa.notas.add(n);
                } else if (c == '2') {
                    holdActivo[col] = true;
                    inicioHold[col] = tiempoMs;
                } else if (c == '3') {
                    if (holdActivo[col]) {
                        Nota n = new Nota();
                        n.tipo = "hold";
                        n.columna = col;
                        n.tiempo = inicioHold[col];
                        n.duracion = Math.max(0, tiempoMs - inicioHold[col]);
                        mapa.notas.add(n);
                        holdActivo[col] = false;
                    }
                }
            }
        }
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
    String artista = "";
    String dificultad = "";
    List<Nota> notas = new ArrayList<>();
}

class Nota {
    String tipo = "";   // "tap" o "hold"
    int columna;        // 0..3
    long tiempo;        // ms desde el inicio
    long duracion;      // 0 si no es hold
}
