package config;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TraductorSM {

    public static void main(String[] args) {
        String rutaSM = "src/GUI/canciones/Cancion6/Artic Monkeys - 505 (aadurite).sm";
        String rutaJSON = "src/GUI/canciones/Cancion6/Artic Monkeys - 505 (aadurite).json";

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

            double offsetSeg = 0.0;
            List<BpmChange> cambios = new ArrayList<>();
            cambios.add(new BpmChange(0.0, 120.0));

            boolean leyendoNotas = false;
            int compas = 0;
            List<String> filasCompas = new ArrayList<>();
            long[] inicioHold = null;
            boolean[] holdActivo = null;
            double beatGlobal = 0.0;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

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
                    mapa.bpm = cambios.get(0).bpm;
                }

                else if (linea.startsWith("#NOTES:")) {
                    leyendoNotas = true;
                    for (int i = 0; i < 5; i++) br.readLine(); // estilo, autor, diff, etc.
                    compas = 0;
                    filasCompas.clear();
                    inicioHold = null;
                    holdActivo = null;
                    beatGlobal = 0.0;
                    continue;
                } else if (leyendoNotas) {
                    if (linea.equals(";")) {
                        beatGlobal = procesarCompas(mapa, filasCompas, compas, offsetSeg, cambios, inicioHold, holdActivo, beatGlobal);
                        leyendoNotas = false;
                    } else if (linea.equals(",")) {
                        beatGlobal = procesarCompas(mapa, filasCompas, compas, offsetSeg, cambios, inicioHold, holdActivo, beatGlobal);
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

            limpiarNotasTapDentroDeHold(mapa);
            return mapa;
        }
    }

    private static class BpmChange {
        final double beat;
        final double bpm;
        BpmChange(double beat, double bpm) { this.beat = beat; this.bpm = bpm; }
    }

    private static double procesarCompas(
            Mapa mapa,
            List<String> filasCompas,
            int compas,
            double offsetSeg,
            List<BpmChange> cambios,
            long[] inicioHold,
            boolean[] holdActivo,
            double beatGlobal
    ) {
        int filas = filasCompas.size();
        if (filas == 0) return beatGlobal;

        if (inicioHold == null) inicioHold = new long[4];
        if (holdActivo == null) holdActivo = new boolean[4];

        double beatsPorLinea = 4.0 / filas;

        for (int f = 0; f < filas; f++) {
            String fila = filasCompas.get(f);
            if (fila.length() < 4) continue;

            double beat = beatGlobal;
            long tMs = msAtBeat(beat, cambios) + Math.round(offsetSeg * 1000.0);

            for (int col = 0; col < 4; col++) {
                char c = fila.charAt(col);
                if (c == '1') {
                    Nota n = new Nota();
                    n.tipo = "tap";
                    n.columna = col;
                    n.tiempo = tMs;
                    mapa.notas.add(n);
                } else if (c == '2' || c == '4') {
                	if (!holdActivo[col]) {
                        holdActivo[col] = true;
                        inicioHold[col] = tMs;
                    }
                } else if (c == '3') {
                    if (holdActivo[col]) {
                        Nota n = new Nota();
                        n.tipo = "hold";
                        n.columna = col;
                        n.tiempo = inicioHold[col];
                        n.duracion = Math.max(1, tMs - inicioHold[col]);
                        mapa.notas.add(n);
                        holdActivo[col] = false;
                    }
                }
            }

            beatGlobal += beatsPorLinea;
        }

        filasCompas.clear();
        return beatGlobal;
    }

    private static long msAtBeat(double beat, List<BpmChange> cambios) {
        double ms = 0.0;
        for (int i = 0; i < cambios.size(); i++) {
            double b0 = cambios.get(i).beat;
            double bpm = cambios.get(i).bpm;
            double b1 = (i + 1 < cambios.size()) ? cambios.get(i + 1).beat : beat;
            double hasta = Math.min(beat, b1);
            if (hasta <= b0) break;
            double beatsEnTramo = hasta - b0;
            ms += (60000.0 / bpm) * beatsEnTramo;
            if (beat <= b1) break;
        }
        return Math.round(ms);
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

    public static void guardarComoJSON(Mapa mapa, String ruta) throws IOException {
        if (mapa != null && mapa.notas != null) {
            mapa.notas.sort(
                Comparator.comparingLong((Nota n) -> n.tiempo)
                    .thenComparing(n -> "hold".equalsIgnoreCase(n.tipo) ? 0 : 1)
            );
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(gson.toJson(mapa));
        }
    }
}

class Mapa {
    String titulo = "";
    double bpm = 120;
    List<Nota> notas = new ArrayList<>();
}

class Nota {
    String tipo = "";
    int columna;
    long tiempo;
    long duracion;
}
