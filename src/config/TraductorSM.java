package config;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TraductorSM {

    public static void main(String[] args) {
        String rutaSM = "src/GUI/canciones/Cancion5/I-Dont-Wanna-Be-Me.sm";
        String rutaJSON = "src/GUI/canciones/Cancion5/I-Dont-Wanna-Be-Me.json";

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
            double offsetSeg = 0.0;
            List<BpmChange> cambios = new ArrayList<>();
            cambios.add(new BpmChange(0.0, 120.0));

            // --- estado ---
            boolean leyendoNotas = false;
            int compas = 0;
            List<String> filasCompas = new ArrayList<>();
            long[] inicioHold = null;
            boolean[] holdActivo = null;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                // encabezado
                if (linea.startsWith("#TITLE:")) {
                    mapa.titulo = linea.substring(7, linea.length() - 1);
                } else if (linea.startsWith("#OFFSET:")) {
                    String s = linea.substring(8, linea.length() - 1);
                    try { offsetSeg = Double.parseDouble(s); } catch (Exception ignored) {}
                } else if (linea.startsWith("#BPMS:")) {
                    String datos = linea.substring(6, linea.length() - 1);
                    cambios.clear();
                    for (String par : datos.split(",")) {
                        String[] kv = par.split("=");
                        if (kv.length == 2) {
                            double beat = Double.parseDouble(kv[0].trim());
                            double bpm = Double.parseDouble(kv[1].trim());
                            cambios.add(new BpmChange(beat, bpm));
                        }
                    }
                    cambios.sort(Comparator.comparingDouble(b -> b.beat));
                    if (cambios.isEmpty()) cambios.add(new BpmChange(0.0, 120.0));
                    mapa.bpm = cambios.get(0).bpm; // exporta el BPM real al JSON

                }

                // bloque de notas
                else if (linea.startsWith("#NOTES:")) {
                    leyendoNotas = true;
                    for (int i = 0; i < 5; i++) br.readLine(); // estilo, autor, diff, meter, radar
                    compas = 0;
                    filasCompas.clear();
                    inicioHold = null;
                    holdActivo = null;
                    continue;
                } else if (leyendoNotas) {
                    if (linea.equals(";")) {
                        procesarCompas(mapa, filasCompas, compas, offsetSeg, cambios, inicioHold, holdActivo);
                        leyendoNotas = false;
                    } else if (linea.equals(",")) {
                        procesarCompas(mapa, filasCompas, compas, offsetSeg, cambios, inicioHold, holdActivo);
                        compas++;
                        filasCompas.clear();
                    } else if (!linea.isEmpty()) {
                        filasCompas.add(linea);
                        int columnas = linea.length();
                        if (inicioHold == null) inicioHold = new long[columnas];
                        if (holdActivo == null) holdActivo = new boolean[columnas];
                    }
                }
            }

            // limpiar taps dentro de holds (ahora sí hay notas)
            limpiarNotasTapDentroDeHold(mapa);

            return mapa;
        }
    }

    // --- Helpers internos ---
    private static class BpmChange {
        final double beat;
        final double bpm;
        BpmChange(double beat, double bpm) { this.beat = beat; this.bpm = bpm; }
    }

    private static long beatToMs(double beatObjetivo, List<BpmChange> cambios) {
        cambios.sort(Comparator.comparingDouble(b -> b.beat));
        double ms = 0.0;
        for (int i = 0; i < cambios.size(); i++) {
            double beatIni = cambios.get(i).beat;
            double bpm = cambios.get(i).bpm;
            double beatFin = (i + 1 < cambios.size()) ? cambios.get(i + 1).beat : beatObjetivo;
            if (beatObjetivo <= beatIni) break;
            double hasta = Math.min(beatFin, beatObjetivo);
            double deltaBeats = Math.max(0, hasta - beatIni);
            ms += (60000.0 / bpm) * deltaBeats;
            if (beatObjetivo <= beatFin) break;
        }
        return Math.round(ms);
    }

    private static void procesarCompas(
            Mapa mapa,
            List<String> filasCompas,
            int compas,
            double offsetSeg,
            List<BpmChange> cambios,
            long[] inicioHold,        // tamaño 4 o null
            boolean[] holdActivo      // tamaño 4 o null
    ) {
        int filas = filasCompas.size();
        if (filas == 0) return;

        if (inicioHold == null) inicioHold = new long[4];
        if (holdActivo == null) holdActivo = new boolean[4];

        for (int f = 0; f < filas; f++) {
            String fila = filasCompas.get(f);
            if (fila.length() < 4) continue; // esperamos 4 columnas

            double beat = beatDeFila(compas, filas, f);
            long tMs = msAtBeat(beat, cambios) + Math.round(offsetSeg * 1000.0);

            for (int col = 0; col < 4; col++) {
                char c = fila.charAt(col);

                if (c == '1') {
                    // TAP
                    Nota n = new Nota();
                    n.tipo = "tap";
                    n.columna = col;
                    n.tiempo = tMs;
                    n.duracion = 0;
                    mapa.notas.add(n);

                } else if (c == '2' || c == '4') {
                    // HEAD de HOLD o ROLL
                    holdActivo[col] = true;
                    inicioHold[col] = tMs;

                } else if (c == '3') {
                    // TAIL de HOLD
                    if (holdActivo[col]) {
                        Nota n = new Nota();
                        n.tipo = "hold";
                        n.columna = col;
                        n.tiempo = inicioHold[col];
                        n.duracion = Math.max(1, tMs - inicioHold[col]); // ≥1 ms
                        mapa.notas.add(n);
                        holdActivo[col] = false;
                    }
                }
                // '0' => vacío; otros tokens de SM (mines, etc.) se ignoran por ahora
            }
        }

        // limpiar para el próximo compás
        filasCompas.clear();
    }


    public static void limpiarNotasTapDentroDeHold(Mapa mapa) {
        List<Nota> filtradas = new ArrayList<>();
        for (Nota tap : mapa.notas) {
            if (!"tap".equals(tap.tipo)) {
                filtradas.add(tap);
                continue;
            }
            boolean dentroDeHold = false;
            for (Nota hold : mapa.notas) {
                if ("hold".equals(hold.tipo) && hold.columna == tap.columna) {
                    long inicio = hold.tiempo;
                    long fin = hold.tiempo + hold.duracion;
                    if (tap.tiempo >= inicio && tap.tiempo <= fin) {
                        dentroDeHold = true;
                        break;
                    }
                }
            }
            if (!dentroDeHold) filtradas.add(tap);
        }
        mapa.notas = filtradas;
    }
    
    private static long msAtBeat(double beat, List<BpmChange> cambios) {
        // Integración por tramos: suma tiempo en cada segmento de BPM hasta 'beat'
        double ms = 0.0;
        for (int i = 0; i < cambios.size(); i++) {
            double b0 = cambios.get(i).beat;
            double bpm = cambios.get(i).bpm;
            double b1 = (i + 1 < cambios.size()) ? cambios.get(i + 1).beat : beat;
            double hasta = Math.min(beat, b1);
            if (hasta <= b0) break;
            double beatsEnTramo = hasta - b0;
            ms += (60000.0 / bpm) * beatsEnTramo; // 1 beat = 60k/BPM ms
            if (beat <= b1) break;
        }
        return Math.round(ms);
    }

    private static double beatDeFila(int compas, int filasEnCompas, int filaIndex) {
        // En StepMania un compás = 4 beats (negras)
        // Cada fila subdivide equidistantemente el compás
        double dentro = (filasEnCompas == 0) ? 0.0 : (4.0 * filaIndex / filasEnCompas);
        return compas * 4.0 + dentro;
    }

    

    public static void guardarComoJSON(Mapa mapa, String ruta) throws IOException {
        // Asegurar orden estable (tiempo asc; holds antes que taps si empatan)
        if (mapa != null && mapa.notas != null) {
            mapa.notas.sort(
                java.util.Comparator
                    .comparingLong((Nota n) -> n.tiempo)
                    .thenComparing(n -> "hold".equalsIgnoreCase(n.tipo) ? 0 : 1)
            );
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(mapa);
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(json);
        }
    }

}

// ===== Clases auxiliares =====

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
